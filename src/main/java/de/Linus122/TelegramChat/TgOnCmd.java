package de.Linus122.TelegramChat;

import de.Linus122.Telegram.Utils;
import de.Linus122.entity.User;
import de.Linus122.repository.UserRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TgOnCmd implements CommandExecutor {

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
        final Optional<User> user = userRepository.readByPlayerId(((Player) sender).getUniqueId().toString());
        if (!user.isPresent()) {
            final User newUser = new User();
            newUser.setChatEnabled(true);
            newUser.setPlayerId(((Player) sender).getUniqueId().toString());
            sender.sendMessage(Utils.formatMSG("telegram-chat-on")[0]);
            UserRepository.getInstance().update(newUser);
            return true;
        }
        final User u = user.get();
        u.setChatEnabled(true);
        userRepository.update(u);
        sender.sendMessage(Utils.formatMSG("telegram-chat-on")[0]);
        return true;
    }
}
