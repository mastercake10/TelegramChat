package de.Linus122.TelegramChat;

import de.Linus122.TelegramComponents.Chat;
import de.Linus122.TelegramComponents.ChatMessageToMc;

public interface TelegramActionListener {
	public void onSendToTelegram(Chat chat);

	public void onSendToMinecraft(ChatMessageToMc chatMsg);
}
