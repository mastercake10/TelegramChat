package de.Linus122.TelegramComponents;

public class Update {
	private int update_id;
	private Message message;
	private Message edited_message;
	private Message channel_post;
	private Message edited_channel_post;

	public int getUpdate_id() {
		return update_id;
	}

	public void setUpdate_id(int update_id) {
		this.update_id = update_id;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Message getEdited_message() {
		return edited_message;
	}

	public void setEdited_message(Message edited_message) {
		this.edited_message = edited_message;
	}

	public Message getChannel_post() {
		return channel_post;
	}

	public void setChannel_post(Message channel_post) {
		this.channel_post = channel_post;
	}

	public Message getEdited_channel_post() {
		return edited_channel_post;
	}

	public void setEdited_channel_post(Message edited_channel_post) {
		this.edited_channel_post = edited_channel_post;
	}

}
