package pl.niker.regions.util;

import org.bukkit.ChatColor;

public class TextUtil {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
