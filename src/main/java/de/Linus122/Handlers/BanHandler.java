package de.Linus122.Handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.Linus122.Telegram.TelegramActionListener;
import de.Linus122.TelegramComponents.ChatMessageToMc;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;

public class BanHandler implements TelegramActionListener{

	@Override
	public void onSendToTelegram(ChatMessageToTelegram chat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendToMinecraft(ChatMessageToMc chatMsg) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(chatMsg.getUuid_sender());

		if(player != null && player.isBanned()) {
			chatMsg.setCancelled(true);
		}
	}

}
