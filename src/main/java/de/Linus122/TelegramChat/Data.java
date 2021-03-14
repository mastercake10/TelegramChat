package de.Linus122.TelegramChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

public class Data {
	private String token = "";
	
	// Player name // ChatID
	private HashMap<Integer, UUID> linkedChats = new HashMap<Integer, UUID>();
	
	// Player name // RandomInt
	private HashMap<String, UUID> linkCodes = new HashMap<String, UUID>();
	
	public List<Integer> ids = new ArrayList<Integer>();
	
	private boolean firstUse = true;
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public HashMap<Integer, UUID> getLinkedChats() {
		return linkedChats;
	}

	public void setLinkedChats(HashMap<Integer, UUID> linkedChats) {
		this.linkedChats = linkedChats;
	}

	public HashMap<String, UUID> getLinkCodes() {
		return linkCodes;
	}

	public void setLinkCodes(HashMap<String, UUID> linkCodes) {
		this.linkCodes = linkCodes;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public boolean isFirstUse() {
		return firstUse;
	}

	public void setFirstUse(boolean firstUse) {
		this.firstUse = firstUse;
	}

	public void addChatPlayerLink(int chatID, UUID player) {
		linkedChats.put(chatID, player);
	}

	public void addLinkCode(String code, UUID player) {
		linkCodes.put(code, player);
	}

	public UUID getUUIDFromLinkCode(String code) {
		return linkCodes.get(code);
	}

	public void removeLinkCode(String code) {
		linkCodes.remove(code);
	}

	public UUID getUUIDFromChatID(int chatID) {
		return linkedChats.get(chatID);
	}

}
