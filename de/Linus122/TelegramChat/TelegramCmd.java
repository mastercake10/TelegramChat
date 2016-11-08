package de.Linus122.TelegramChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TelegramCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if(!cs.hasPermission("telegram.settoken")){
			cs.sendMessage("§cYou don't have permissions to use this!");
			return true;
		}
		if(args.length == 0){
			cs.sendMessage("§c/telegram [token]");
			return true;
		}
		if(Main.data == null){
			Main.data = new Data();
		}
		Main.data.token = args[0];
		Main.save();
		boolean success = false;
	
		success = Telegram.auth();
		if(success){
			cs.sendMessage("§cSuccessfully connected to Telegram!");
			cs.sendMessage("§aAdd " + Telegram.authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram!");
		}else{
			cs.sendMessage("§cWrong token. Paste in the whole token!");
		}
		return true;
	}

}
