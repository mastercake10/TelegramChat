package de.Linus122.TelegramChat;

import com.google.gson.Gson;
import de.Linus122.Handlers.BanHandler;
import de.Linus122.Handlers.VanishHandler;
import de.Linus122.Metrics.Metrics;
import de.Linus122.Telegram.Telegram;
import de.Linus122.Telegram.Utils;
import de.Linus122.TelegramComponents.Chat;
import de.Linus122.TelegramComponents.ChatMessageToMc;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import de.Linus122.entity.User;
import de.Linus122.handler.ChattyEventsHandler;
import de.Linus122.repository.UserRepository;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TelegramChat extends JavaPlugin implements Listener {
	private static final File datad = new File("plugins/TelegramChat/data.json");
	private static FileConfiguration cfg;
	private static Data data;
	public static Telegram telegramHook;
	private static TelegramChat instance;
	private static boolean isSuperVanish;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		cfg = this.getConfig();
		instance = this;
		Utils.cfg = cfg;

		Bukkit.getPluginCommand("telegram").setExecutor(new TelegramCmd());
		Bukkit.getPluginCommand("linktelegram").setExecutor(new LinkTelegramCmd());
		Bukkit.getPluginCommand("tgon").setExecutor(new TgOnCmd());
		Bukkit.getPluginCommand("tgoff").setExecutor(new TgOffCmd());
		Bukkit.getPluginManager().registerEvents(this, this);

		if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
			isSuperVanish = true;
			Bukkit.getPluginManager().registerEvents(new VanishHandler(), this);
		}

		if (Bukkit.getPluginManager().isPluginEnabled("Chatty")) {
			Bukkit.getPluginManager().registerEvents(new ChattyEventsHandler(this), this);
		}

		File dir = new File("plugins/TelegramChat/");
		dir.mkdir();
		data = new Data();
		if (datad.exists()) {
			Gson gson = new Gson();
			try {
				FileReader fileReader = new FileReader(datad);
				StringBuilder sb = new StringBuilder();
				int c;
			    while((c = fileReader.read()) !=-1) {
			    	sb.append((char) c);
			    }

				data = gson.fromJson(sb.toString(), Data.class);
				
				fileReader.close();
			} catch (Exception e) {
				// old method for loading the data.yml file
				try {
					FileInputStream fin = new FileInputStream(datad);
					ObjectInputStream ois = new ObjectInputStream(fin);
					
					data = gson.fromJson((String) ois.readObject(), Data.class);
					ois.close();
					fin.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				this.getLogger().log(Level.INFO, "Converted old data.yml");
				save();
			}
		}

		telegramHook = new Telegram();
		telegramHook.auth(data.getToken());
		
		// Ban Handler (Prevents banned players from chatting)
		telegramHook.addListener(new BanHandler());
		
		// Console sender handler, allows players to send console commands (telegram.console permission)
		// telegramHook.addListener(new CommandHandler(telegramHook, this));

		Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
			if (telegramHook.connected) {
				telegramHook.getUpdate();
			} else {
				telegramHook.reconnect();
			}
		}, 10L, 10L);

		new Metrics(this);
	}

	@Override
	public void onDisable() {
		save();
	}

	public static void save() {
		Gson gson = new Gson();

		try {
			FileWriter fileWriter = new FileWriter(datad);
			fileWriter.write(gson.toJson(data));
			
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Data getBackend() {
		return data;
	}

	public static void initBackend() {
		data = new Data();
	}

	public static void sendToMC(ChatMessageToMc chatMsg) {
		sendToMC(chatMsg.getUuid_sender(), chatMsg.getContent(), chatMsg.getChatID_sender());
	}

	private static void sendToMC(UUID uuid, String msg, long sender_chat) {
		Bukkit.getScheduler().runTask(instance, () -> {
			final String name = Bukkit.getOfflinePlayer(uuid).getName();
			final String msgF = Utils.formatMSG("general-message-to-mc", name, msg)[0];
			final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
			final Set<String> ids = onlinePlayers.stream()
					.map(Player::getUniqueId)
					.map(UUID::toString)
					.collect(Collectors.toSet());

			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				final Set<String> users = UserRepository.getInstance()
						.findAllMsgOnUsersAmongOnlinePlayers(ids).stream()
						.map(User::getPlayerId)
						.collect(Collectors.toSet());

				Bukkit.getScheduler().runTask(instance, () -> {
					onlinePlayers.stream()
							.filter(player -> !users.contains(player.getUniqueId().toString()))
							.forEach(player -> player.sendMessage(msgF));
				});
			});
		});
	}

	public static void link(UUID player, long userID) {
		TelegramChat.data.addChatPlayerLink(userID, player);

		Bukkit.getScheduler().runTask(instance, () -> {
			String name = Bukkit.getOfflinePlayer(player).getName();

			Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
				telegramHook.sendMsg(userID, "Success! Linked " + name);
			});
		});
	}
	
	public boolean isChatLinked(Chat chat) {
		return TelegramChat.getBackend().getLinkedChats().containsKey(chat.getId());
	}

	public static String generateLinkToken() {

		Random rnd = new Random();
		int i = rnd.nextInt(9999999);
		String s = i + "";
		String finals = "";
		for (char m : s.toCharArray()) {
			int m2 = Integer.parseInt(m + "");
			int rndi = rnd.nextInt(2);
			if (rndi == 0) {
				m2 += 97;
				char c = (char) m2;
				finals = finals + c;
			} else {
				finals = finals + m;
			}
		}
		return finals;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!this.getConfig().getBoolean("enable-joinquitmessages"))
			return;

		if(isSuperVanish && VanishAPI.isInvisible(e.getPlayer()))
			return;

		if (telegramHook.connected) {
			ChatMessageToTelegram chat = new ChatMessageToTelegram();
			chat.parse_mode = "Markdown";
			chat.text = Utils.formatMSG("join-message", e.getPlayer().getName())[0];
			telegramHook.sendAll(chat);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (!this.getConfig().getBoolean("enable-deathmessages"))
			return;
		if (telegramHook.connected) {
			ChatMessageToTelegram chat = new ChatMessageToTelegram();
			chat.parse_mode = "Markdown";
			chat.text = Utils.formatMSG("death-message", e.getDeathMessage())[0];
			telegramHook.sendAll(chat);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (!this.getConfig().getBoolean("enable-joinquitmessages"))
			return;

		if(isSuperVanish && VanishAPI.isInvisible(e.getPlayer()))
			return;

		if (telegramHook.connected) {
			ChatMessageToTelegram chat = new ChatMessageToTelegram();
			chat.parse_mode = "Markdown";
			chat.text = Utils.formatMSG("quit-message", e.getPlayer().getName())[0];
			telegramHook.sendAll(chat);
		}
	}

	public static TelegramChat getInstance()
	{
		return instance;
	}

}
