package pl.niker.regions.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("(&#([0-9a-fA-F]{6}))");

    public static String format(String text) {
        if (text == null) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder(text.length());

        while (matcher.find()) {
            String hex = matcher.group(2);
            if (hex != null && hex.length() == 6) {
                matcher.appendReplacement(sb, net.md_5.bungee.api.ChatColor.of("#" + hex).toString());
            } else {
                matcher.appendReplacement(sb, matcher.group(1));
            }
        }
        matcher.appendTail(sb);

        String processed = ChatColor.translateAlternateColorCodes('&', sb.toString());

        StringBuilder finalSb = new StringBuilder(processed.length());
        for (int i = 0; i < processed.length(); i++) {
            char c = processed.charAt(i);
            if (i + 1 < processed.length()) {
                char next = processed.charAt(i + 1);
                if (c == '>' && next == '>') {
                    finalSb.append('»');
                    i++;
                    continue;
                } else if (c == '<' && next == '<') {
                    finalSb.append('«');
                    i++;
                    continue;
                } else if (c == '-' && next == '>') {
                    finalSb.append('→');
                    i++;
                    continue;
                } else if (c == '<' && next == '-') {
                    finalSb.append('←');
                    i++;
                    continue;
                } else if (c == '*' && next == '*') {
                    finalSb.append('•');
                    i++;
                    continue;
                }
            }
            finalSb.append(c);
        }

        return finalSb.toString();
    }
}
