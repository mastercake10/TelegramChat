package de.Linus122.TelegramChat;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Linus122.Telegram.Utils;

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
		if (TelegramChat.getBackend() == null) {
			TelegramChat.initBackend();
		}
		if (TelegramChat.telegramHook.authJson == null) {
			cs.sendMessage(Utils.formatMSG("need-to-add-bot-first")[0]);
			return true;
		}

		String token = TelegramChat.generateLinkToken();
		TelegramChat.getBackend().addLinkCode(token, ((Player) cs).getUniqueId());
		cs.sendMessage(Utils.formatMSG("get-token",
				TelegramChat.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString(),
				TelegramChat.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString(), token));

		return true;
	}

}
