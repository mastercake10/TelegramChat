package de.Linus122.TelegramComponents;

public class Chat {
	private long id;
	private String type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * isPrivate
	 * @return true for private, false for group chats
	 */
	public boolean isPrivate(){
		return type.equals("private");
	}
	

}
