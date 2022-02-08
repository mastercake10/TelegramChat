package de.Linus122.Handlers;

import de.Linus122.Telegram.Utils;
import de.Linus122.TelegramChat.TelegramChat;
import de.Linus122.TelegramComponents.ChatMessageToTelegram;
import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VanishHandler implements Listener
{

    @EventHandler
    public void onVanish(PlayerHideEvent e) {
        Player p = e.getPlayer();
        if(e.isCancelled()) return;

        if (!TelegramChat.getInstance().getConfig().getBoolean("enable-joinquitmessages"))
            return;
        if (TelegramChat.telegramHook.connected) {
            ChatMessageToTelegram chat = new ChatMessageToTelegram();
            chat.parse_mode = "Markdown";
            chat.text = Utils.formatMSG("quit-message", e.getPlayer().getName())[0];
            TelegramChat.telegramHook.sendAll(chat);
        }
    }

    @EventHandler
    public void onShow(PlayerShowEvent e) {
        Player p = e.getPlayer();
        if(e.isCancelled()) return;

        if (!TelegramChat.getInstance().getConfig().getBoolean("enable-joinquitmessages"))
            return;
        if (TelegramChat.telegramHook.connected) {
            ChatMessageToTelegram chat = new ChatMessageToTelegram();
            chat.parse_mode = "Markdown";
            chat.text = Utils.formatMSG("join-message", e.getPlayer().getName())[0];
            TelegramChat.telegramHook.sendAll(chat);
        }
    }
}
