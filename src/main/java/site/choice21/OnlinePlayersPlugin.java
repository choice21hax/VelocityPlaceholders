package site.choice21;

import java.nio.file.Path;

/**
 * Base interface for the plugin that works on both Velocity and Paper
 */
public interface OnlinePlayersPlugin {
    /**
     * Get the player count for a specific server
     * @param serverName The server name (e.g., "modernprac")
     * @return The player count, or 0 if not found
     */
    int getPlayerCount(String serverName);
    
    /**
     * Get the logger instance
     */
    Object getLogger();
    
    /**
     * Get the data directory
     */
    Path getDataDirectory();
    
    /**
     * Replace all placeholders in a string
     * @param text The text containing placeholders like %online_modernprac%
     * @return The text with placeholders replaced
     */
    String replacePlaceholders(String text);
}

