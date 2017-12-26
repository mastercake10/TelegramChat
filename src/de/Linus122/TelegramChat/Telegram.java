package de.Linus122.TelegramChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.Linus122.TelegramComponents.Chat;


public class Telegram {
	public static JsonObject authJson;
	public static boolean connected = false;
	
	
	static int lastUpdate = 0;
	public static boolean auth(){
		try{
			JsonObject obj = sendGet("https://api.telegram.org/bot" + Main.data.token + "/getMe");
			authJson = obj;
			connected = true;
			return true;
		}catch(Exception e){
			connected = false;
			System.out.print("[Telegram] Sorry, but could not connect to Telegram servers. The token could be wrong.");
			return false;
		}
	}
	public static void getupdate(){
		
		JsonObject up = null;
		try {
			
			up = sendGet("https://api.telegram.org/bot" + Main.data.token + "/getUpdates?offset=" + (lastUpdate + 1));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(up == null){
			auth();
		}
		if(up.has("result")){
			for (JsonElement ob : up.getAsJsonArray("result")) {
				if (ob.isJsonObject()) {
					JsonObject obj = (JsonObject) ob;
					if(obj.has("update_id")){
						lastUpdate = obj.get("update_id").getAsInt();
					}
					if (obj.has("message")) {
						JsonObject chat = obj.getAsJsonObject("message").getAsJsonObject("chat");
						if(chat.get("type").getAsString().equals("private")){
							int id = chat.get("id").getAsInt();
							if(!Main.data.ids.contains(id)) Main.data.ids.add(id);
							
							if(obj.getAsJsonObject("message").has("text")){
								String text = obj.getAsJsonObject("message").get("text").getAsString();
								for(char c : text.toCharArray()){
									if((int) c == 55357){
										Telegram.sendMsg(id, "Emoticons are not allowed, sorry!");
										return;
									}
								}
								if(text.length() == 0) return;
								if(text.equals("/start")){
									if(Main.data.firstUse){
										Main.data.firstUse = false;
										Chat chat2 = new Chat();
										chat2.chat_id = id;
										chat2.parse_mode = "Markdown";
										chat2.text = "Congratulations, your bot is working! Have fun with this Plugin. Feel free to donate via *PayPal* to me if you like TelegramChat! [PayPal Donation URL](https://goo.gl/I02XGH)";
										Telegram.sendMsg(chat2);
									}
									Telegram.sendMsg(id, "You can see the chat but you can't chat at the moment. Type /linktelegram ingame to chat!");
								}else
								if(Main.data.linkCodes.containsKey(text)){
									//LINK
									Main.link(Main.data.linkCodes.get(text), id);
									Main.data.linkCodes.remove(text);
								}else if(Main.data.linkedChats.containsKey(id)){
									Main.sendToMC(Main.data.linkedChats.get(id), text, id);
								}else{
									Telegram.sendMsg(id, "Sorry, please link your account with /linktelegram ingame to use the chat!");
								}
							}
							
						}else if(chat.get("type").getAsString().equals("group")){
							int id = chat.get("id").getAsInt();
							if(!Main.data.ids.contains(id))
								Main.data.ids.add(id);
						}
					}
					
				}
			}	
		}
	}
	
	public static void sendMsg(int id, String msg){
		Gson gson = new Gson();
		Chat chat = new Chat();
		chat.chat_id = id;
		chat.text = msg;
		post("sendMessage", gson.toJson(chat, Chat.class));
		
	}
	public static void sendMsg(Chat chat){
		Gson gson = new Gson();
		post("sendMessage", gson.toJson(chat, Chat.class));
		
	}
	public static void sendAll(final Chat chat){
		new Thread(new Runnable(){
			public void run(){
				Gson gson = new Gson();
				for(int id : Main.data.ids){
					chat.chat_id = id;
					post("sendMessage", gson.toJson(chat, Chat.class));
				}
			}
		}).start();
	}
	public static void post(String method, String json){
		try {
			String body = json;
			URL url = new URL("https://api.telegram.org/bot" + Main.data.token + "/" + method);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json; ; Charset=UTF-8");
			connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			writer.write(body);
			writer.close();
			wr.close();
			
			//OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			//writer.write(body);
			//writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			for (String line; (line = reader.readLine()) != null;) {
				
			}

			writer.close();
			reader.close();
		} catch (Exception e) {
			auth();
			System.out.print("[Telegram] Disconnected from Telegram, reconnect...");
		}
		
	}

	public static JsonObject sendGet(String url) throws IOException {
		String a = url;
		URL url2 = new URL(a);
		URLConnection conn = url2.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String all = "";
		String inputLine;
		while ((inputLine = br.readLine()) != null) {
			all += inputLine;
		}

		br.close();
		JsonParser parser = new JsonParser();
		return parser.parse(all).getAsJsonObject();

	}
}
