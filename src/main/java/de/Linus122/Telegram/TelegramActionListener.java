package de.Linus122.Telegram;

import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import de.Linus122.TelegramComponents.ChatMessageToMc;

public interface TelegramActionListener {
	public void onSendToTelegram(ChatMessageToTelegram chat);

	public void onSendToMinecraft(ChatMessageToMc chatMsg);
}
