package de.Linus122.TelegramChat;

import org.bukkit.configuration.file.FileConfiguration;

public class Utils {
	public static String escape(String str) {
		return str.replace("_", "\\_");
	}

	public static FileConfiguration cfg;

	final static String MESSAGE_SECTION = "messages";

	public static String[] formatMSG(String suffixKey) {
		return formatMSG(suffixKey, "");
	}

	public static String[] formatMSG(String suffixKey, Object... args) {
		String key = MESSAGE_SECTION + "." + suffixKey;
		if (!cfg.contains(key))
			return new String[] {
					"Message not found in config.yml. Please check your config if the following key is present:", key };
		String rawMessage = cfg.getString(key);
		if (args != null && args.length > 0)
			rawMessage = String.format(rawMessage, args);
		rawMessage = rawMessage.replace("&", "§");

		return rawMessage.split("\\n");

	}
}
