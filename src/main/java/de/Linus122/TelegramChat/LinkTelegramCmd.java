package de.Linus122.TelegramChat;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkTelegramCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args) {
		if (!(cs instanceof Player)) {
			cs.sendMessage(Utils.formatMSG("cant-link-console")[0]);
		}
		if (!cs.hasPermission("telegram.linktelegram")) {
			cs.sendMessage(Utils.formatMSG("no-permissions")[0]);
			return true;
		}
		if (Main.getBackend() == null) {
			Main.initBackend();
		}
		if (Main.telegramHook.authJson == null) {
			cs.sendMessage(Utils.formatMSG("need-to-add-bot-first")[0]);
			return true;
		}

		String token = Main.generateLinkToken();
		Main.getBackend().addLinkCode(token, ((Player) cs).getUniqueId());
		cs.sendMessage(Utils.formatMSG("get-token",
				Main.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString(),
				Main.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString(), token));

		return true;
	}

}
