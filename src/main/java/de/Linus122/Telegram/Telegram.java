package de.Linus122.Telegram;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.Linus122.TelegramChat.TelegramChat;
import de.Linus122.TelegramComponents.Chat;
import de.Linus122.TelegramComponents.ChatMessageToMc;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import de.Linus122.TelegramComponents.Update;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Telegram {
	public JsonObject authJson;
	public boolean connected = false;

	static int lastUpdate = 0;
	public String token;

	private List<TelegramActionListener> listeners = new ArrayList<TelegramActionListener>();

	private final String API_URL_GETME = "https://api.telegram.org/bot%s/getMe";
	private final String API_URL_GETUPDATES = "https://api.telegram.org/bot%s/getUpdates?offset=%d";
	private final String API_URL_GENERAL = "https://api.telegram.org/bot%s/%s";

	private Gson gson = new Gson();

	private Set<Long> sentWarnings = ConcurrentHashMap.newKeySet();

	public void addListener(TelegramActionListener actionListener) {
		listeners.add(actionListener);
	}

	public boolean auth(String token) {
		this.token = token;
		return reconnect();
	}

	public boolean reconnect() {
		try {
			JsonObject obj = sendGet(String.format(API_URL_GETME, token));
			authJson = obj;
			System.out.print("[Telegram] Established a connection with the telegram servers.");
			connected = true;
			return true;
		} catch (Exception e) {
			connected = false;
			System.out.print("[Telegram] Sorry, but could not connect to Telegram servers. The token could be wrong.");
			return false;
		}
	}

	public boolean getUpdate() {
		JsonObject up;
		try {
			up = sendGet(String.format(API_URL_GETUPDATES, TelegramChat.getBackend().getToken(), lastUpdate + 1));
		} catch (IOException e) {
			return false;
		}
		if (up == null) {
			return false;
		}
		if (up.has("result")) {
			for (JsonElement ob : up.getAsJsonArray("result")) {
				if (ob.isJsonObject()) {
					Update update = gson.fromJson(ob, Update.class);
		
					if(lastUpdate == update.getUpdate_id()) return true;
					lastUpdate = update.getUpdate_id();

					if (update.getMessage() != null) {
						Chat chat = update.getMessage().getChat();

						if (chat.isPrivate()) {
							// skip

						} else if (!chat.isPrivate()) {
							// group chat
							long id = chat.getId();
							if (!TelegramChat.getBackend().chat_ids.contains(id) && TelegramChat.getBackend().chat_ids.size() < 1)
								TelegramChat.getBackend().chat_ids.add(id);
							
							if (update.getMessage().getText() != null) {
								String text = update.getMessage().getText();
								handleUserMessage(text, update);
							}
						}
					}

				}
			}
		}
		return true;
	}
	
	public void handleUserMessage(String text, Update update) {
		Chat chat = update.getMessage().getChat();
		long chatId = update.getMessage().getFrom().getId();
		if (TelegramChat.getBackend().getLinkCodes().containsKey(text)) {
			// LINK
			TelegramChat.link(TelegramChat.getBackend().getUUIDFromLinkCode(text), chatId);
			TelegramChat.getBackend().removeLinkCode(text);
		} else if (TelegramChat.getBackend().containsChatId(chatId)) {
			ChatMessageToMc chatMsg = new ChatMessageToMc(
					TelegramChat.getBackend().getUUIDFromUserID(chatId), text, chat.getId());
			
			for (TelegramActionListener actionListener : listeners) {
				actionListener.onSendToMinecraft(chatMsg);
			}
			
			if(!chatMsg.isCancelled()){
				TelegramChat.sendToMC(chatMsg);
			}
		} else {
			if (!sentWarnings.contains(chat.getId())) {
				this.sendMsg(chat.getId(), Utils.formatMSG("need-to-link")[0]);
				sentWarnings.add(chat.getId());
			}
		}
	}

	public void sendMsg(long id, String msg) {
		ChatMessageToTelegram chat = new ChatMessageToTelegram();
		chat.chat_id = id;
		chat.text = msg;
		sendMsg(chat);
	}

	public void sendMsg(ChatMessageToTelegram chat) {
		for (TelegramActionListener actionListener : listeners) {
			actionListener.onSendToTelegram(chat);
		}
		Gson gson = new Gson();
		if(!chat.isCancelled()){
			post("sendMessage", gson.toJson(chat, ChatMessageToTelegram.class));	
		}
	}

	public void sendAll(final ChatMessageToTelegram chat) {
		Bukkit.getScheduler().runTaskAsynchronously(TelegramChat.getInstance(), () -> {
			for (long id : TelegramChat.getBackend().chat_ids) {
				chat.chat_id = id;
				sendMsg(chat);
			}
		});
	}

	public void post(String method, String json) {
		try {
			String body = json;
			URL url = new URL(String.format(API_URL_GENERAL, TelegramChat.getBackend().getToken(), method));
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

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			writer.close();
			reader.close();
		} catch (Exception e) {
			reconnect();
			System.out.print("[Telegram] Disconnected from Telegram, reconnect...");
		}

	}

	public JsonObject sendGet(String url) throws IOException {
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
