package de.Linus122.handler;

import de.Linus122.Telegram.Utils;
import de.Linus122.TelegramChat.TelegramChat;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import de.Linus122.entity.User;
import de.Linus122.repository.UserRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.mrbrikster.chatty.api.events.ChattyMessageEvent;

public class ChattyEventsHandler implements Listener {

    private final TelegramChat telegramChat;

    public ChattyEventsHandler(TelegramChat telegramChat) {
        this.telegramChat = telegramChat;
    }

    @EventHandler
    public void handleChattyMessageEvent(ChattyMessageEvent e) {
        if (!telegramChat.getConfig().getBoolean("enable-chatmessages"))
            return;
        if (!TelegramChat.telegramHook.connected) {
            return;
        }
        if (!e.getChat().getName().equalsIgnoreCase("global")) {
            return;
        }
        final boolean isChatEnabled = UserRepository.getInstance()
                .readByPlayerId(e.getPlayer().getUniqueId().toString())
                .map(User::isChatEnabled)
                .orElse(Boolean.TRUE);
        if (!isChatEnabled) {
            return;
        }
        ChatMessageToTelegram chat = new ChatMessageToTelegram();
        chat.parse_mode = "Markdown";
        chat.text = Utils
                .escape(Utils.formatMSG("general-message-to-telegram", e.getPlayer().getName(), e.getMessage())[0])
                .replaceAll("§.", "");
        TelegramChat.telegramHook.sendAll(chat);
    }



}
