package site.choice21;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI expansion implementation for Paper/Spigot
 * This class is only loaded if PlaceholderAPI is present
 */
public class PlaceholderAPIExpansionImpl extends PlaceholderExpansion {
    
    private final PaperPlaceholders plugin;
    
    public PlaceholderAPIExpansionImpl(PaperPlaceholders plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "onlineplayers";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "choice21";
    }
    
    @Override
    public @NotNull String getVersion() {
        return "1.1.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        // Handle %onlineplayers_online_<server>%
        if (params.startsWith("online_")) {
            String serverName = params.substring("online_".length());
            return String.valueOf(plugin.getPlayerCount(serverName));
        }
        
        return null;
    }
}

