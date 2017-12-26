package de.Linus122.TelegramChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import de.Linus122.Metrics.Metrics;
import de.Linus122.TelegramComponents.Chat;


public class Main extends JavaPlugin implements Listener{
	public static File datad = new File("plugins/TelegramChat/data.json");
	public static FileConfiguration cfg;
	
	public static Data data = new Data();
	static Plugin pl;
	public static Telegram telegramHook;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		cfg = this.getConfig();
		this.pl = this;
		Bukkit.getPluginCommand("telegram").setExecutor(new TelegramCmd());
		Bukkit.getPluginCommand("linktelegram").setExecutor(new LinkTelegramCmd());
		Bukkit.getPluginManager().registerEvents(this, this);
		File dir = new File("plugins/TelegramChat/");
		dir.mkdir();
		data = new Data();
		if(datad.exists()){
			try {
				FileInputStream fin = new FileInputStream(datad);
				ObjectInputStream ois = new ObjectInputStream(fin);
				Gson gson = new Gson();
				data = (Data) gson.fromJson((String) ois.readObject(), Data.class);
				ois.close();
				fin.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		telegramHook = new Telegram();
		telegramHook.auth(data.token);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			public void run(){
				if(telegramHook.connected){
					telegramHook.getUpdate();
				}
			}
		}, 20L, 20L);
		new Metrics(this);
	}
	public static void save(){
		Gson gson = new Gson();
		
		try {
			FileOutputStream fout= new FileOutputStream (datad);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			
			oos.writeObject(gson.toJson(data));
			fout.close();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onDisable(){
		save();
	}
	public static void sendToMC(UUID uuid, String msg, int sender){
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		List<Integer> recievers = new ArrayList<Integer>();
		recievers.addAll(Main.data.ids);
		recievers.remove((Object) sender);
		String msgF = Main.cfg.getString("chat-format").replace('&', '§').replace("%player%", op.getName()).replace("%message%", msg);
		for(int id : recievers){
			telegramHook.sendMsg(id, msgF);
		}
		Bukkit.broadcastMessage(msgF.replace("&", "§"));
	
	}
	public static void link(UUID player, int chatID){
		Main.data.linkedChats.put(chatID, player);
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		telegramHook.sendMsg(chatID, "Success! Linked " + p.getName());
	}
	public static String generateLinkToken(){
		Random rnd = new Random();
		int i = rnd.nextInt(9999999);
		String s = i + "";
		String finals = "";
		for(char m : s.toCharArray()){
			int m2 = Integer.parseInt(m + "");
			int rndi = rnd.nextInt(2);
			if(rndi == 0){
				m2+=97;
				char c = (char) m2;
				finals = finals + c;
			}else{
				finals = finals + m;
			}
		}
		return finals;
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(!this.getConfig().getBoolean("enable-joinquitmessages")) return;
		if(telegramHook.connected){
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " joined the game.`";
			telegramHook.sendAll(chat);
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		if(!this.getConfig().getBoolean("enable-deathmessages")) return;
		if(telegramHook.connected){
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`"+e.getDeathMessage() + "`";
			telegramHook.sendAll(chat);
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(!this.getConfig().getBoolean("enable-joinquitmessages")) return;
		if(telegramHook.connected){
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " left the game.`";
			System.out.println(chat.text);
			telegramHook.sendAll(chat);
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(!this.getConfig().getBoolean("enable-chatmessages")) return;
		if(telegramHook.connected){
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = e.getPlayer().getName() + ": _" + e.getMessage().replaceAll("§.", "") + "_";
			telegramHook.sendAll(chat);
		}
	}
}
