package site.choice21;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManager {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%online_([^%]+)%");
    private final OnlinePlayersPlugin plugin;
    
    public PlaceholderManager(OnlinePlayersPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Replace all %online_<server>% placeholders in a string
     * @param text The text containing placeholders
     * @return The text with placeholders replaced
     */
    public String replacePlaceholders(String text) {
        if (text == null) {
            return null;
        }
        
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String serverName = matcher.group(1);
            String replacement = String.valueOf(plugin.getPlayerCount(serverName));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Check if a string contains any online placeholders
     * @param text The text to check
     * @return true if placeholders are found
     */
    public boolean containsPlaceholders(String text) {
        return text != null && PLACEHOLDER_PATTERN.matcher(text).find();
    }
}

