package de.Linus122.Handlers;

import java.security.Permissions;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.RegisteredServiceProvider;

import de.Linus122.Telegram.Telegram;
import de.Linus122.Telegram.TelegramActionListener;
import de.Linus122.TelegramChat.TelegramChat;
import de.Linus122.TelegramComponents.ChatMessageToMc;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import net.milkbowl.vault.permission.Permission;

public class CommandHandler extends ConsoleHandler implements TelegramActionListener {

	private Permission permissionsAdapter;
	private long lastChatId = -1;
	private long lastCommandTyped;
	
	private Telegram telegram;
	private Plugin plugin;
	
	public CommandHandler(Telegram telegram, Plugin plugin) {
		java.util.logging.Logger global = java.util.logging.Logger.getLogger("");
		//global.addHandler(this);

		LogManager.getLogManager().getLoggerNames().asIterator().forEachRemaining(c -> LogManager.getLogManager().getLogger(c).addHandler(this));
			
		
		 
		//Bukkit.getLogger().addHandler(this);

		//Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(k -> k.getLogger().addHandler(this));

		setupVault();
		
		this.telegram = telegram;
		this.plugin = plugin;
	}
	
	private void setupVault() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permissionsAdapter = rsp.getProvider();
	}

	@Override
	public void onSendToTelegram(ChatMessageToTelegram chat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendToMinecraft(ChatMessageToMc chatMsg) {
	
		if(permissionsAdapter == null) {
			// setting up vault permissions
			this.setupVault();
		}
		
		if(chatMsg.getContent().startsWith("/")) {
			
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(chatMsg.getUuid_sender());
			
			if(permissionsAdapter.playerHas(null, offlinePlayer, "telegramchat.console")) {
				lastChatId = chatMsg.getChatID_sender();
				lastCommandTyped = System.currentTimeMillis();
				
				Bukkit.getScheduler().runTask(this.plugin, () -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), chatMsg.getContent().substring(1, chatMsg.getContent().length()-1));
				});
			}

		}
		
		
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
	}

	@Override
	public void publish(LogRecord record) {
		if(lastChatId != -1) {
//			String s = String.format(record.getMessage(), record.getParameters());
			String s = this.getFormatter().format(record);
			telegram.sendMsg(lastChatId, s);	
			
		}
		
	}

}
