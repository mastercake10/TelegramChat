package de.Linus122.TelegramChat;

import de.Linus122.Telegram.Utils;
import de.Linus122.entity.User;
import de.Linus122.repository.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TgOffCmd implements CommandExecutor {

    private final UserRepository userRepository = UserRepository.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (!sender.hasPermission("telegram.linktelegram")) {
            sender.sendMessage(Utils.formatMSG("no-permissions")[0]);
            return true;
        }
        if (TelegramChat.getBackend() == null) {
            TelegramChat.initBackend();
        }
        if (TelegramChat.telegramHook.authJson == null) {
            sender.sendMessage(Utils.formatMSG("need-to-add-bot-first")[0]);
            return true;
        }
        final String playerId = ((Player) sender).getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(TelegramChat.getInstance(), () -> {
            final User user = userRepository.readByPlayerId(playerId)
                    .orElseGet(() -> {
                        final User newUser = new User();
                        newUser.setPlayerId(playerId);
                        return newUser;
                    });
            user.setChatEnabled(false);
            UserRepository.getInstance().update(user);

            Bukkit.getScheduler().runTask(TelegramChat.getInstance(), () -> {
                sender.sendMessage(Utils.formatMSG("telegram-chat-off")[0]);
            });
        });
        return true;
    }
}
