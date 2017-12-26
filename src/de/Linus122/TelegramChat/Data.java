package de.Linus122.TelegramChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Data {
	public String token = "";
	//Player name // ChatID
	public HashMap<Integer, UUID> linkedChats = new HashMap<Integer, UUID>();
	//Player name // RandomInt
	public HashMap<String, UUID> linkCodes = new HashMap<String, UUID>();
	public static List<Integer> ids = new ArrayList<Integer>();
	boolean firstUse = true;
}
