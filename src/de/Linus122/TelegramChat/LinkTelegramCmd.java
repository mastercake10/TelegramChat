package de.Linus122.TelegramChat;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkTelegramCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if(!(cs instanceof Player)){
			cs.sendMessage("§cSorry, but you can't link the console currently.");
		}
		if(!cs.hasPermission("telegram.linktelegram")){
			cs.sendMessage("§cYou don't have permissions to use this!");
			return true;
		}
		if(Main.data == null){
			Main.data = new Data();
		}
		if(Telegram.authJson == null){
			cs.sendMessage("§cPlease add a bot to your server first! /telegram");
			return true;
		}
		
		String token = Main.generateLinkToken();
		Main.data.linkCodes.put(token, ((Player) cs).getUniqueId());
		cs.sendMessage("§aAdd " + Telegram.authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram and send this message to " + Telegram.authJson.getAsJsonObject("result").get("username").getAsString() + ":");
		cs.sendMessage("§c" + token);
		
		return true;
	}

}
