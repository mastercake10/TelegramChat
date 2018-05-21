package de.Linus122.TelegramComponents;

import java.util.UUID;

public class ChatMessageToMc extends Cancellable {
	UUID uuid_sender;
	String content;
	int chatID_sender;

	public ChatMessageToMc(UUID uuid_sender, String content, int chatID_sender) {
		this.uuid_sender = uuid_sender;
		this.content = content;
		this.chatID_sender = chatID_sender;
	}

	public UUID getUuid_sender() {
		return uuid_sender;
	}

	public void setUuid_sender(UUID uuid_sender) {
		this.uuid_sender = uuid_sender;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getChatID_sender() {
		return chatID_sender;
	}

	public void setChatID_sender(int chatID_sender) {
		this.chatID_sender = chatID_sender;
	}
}
