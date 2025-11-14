package site.choice21;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for Paper/Spigot
 * This class is loaded when the plugin runs on Paper/Spigot
 */
public class OnlinePlayersPluginMain extends JavaPlugin {
    private PaperPlaceholders plugin;
    private Object papiExpansion;
    
    @Override
    public void onEnable() {
        plugin = new PaperPlaceholders(this);
        plugin.onEnable();
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                papiExpansion = PlaceholderAPIExpansion.create(plugin);
                if (papiExpansion != null && PlaceholderAPIExpansion.register(papiExpansion)) {
                    getLogger().info("PlaceholderAPI expansion registered successfully!");
                } else {
                    getLogger().warning("Failed to register PlaceholderAPI expansion!");
                }
            } catch (Exception e) {
                getLogger().warning("Failed to load PlaceholderAPI expansion: " + e.getMessage());
                getLogger().info("PlaceholderAPI not found. Placeholders will only work via API.");
            }
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholders will only work via API.");
        }
    }
    
    @Override
    public void onDisable() {
        if (papiExpansion != null) {
            try {
                PlaceholderAPIExpansion.unregister(papiExpansion);
            } catch (Exception e) {
                // Ignore
            }
        }
        if (plugin != null) {
            plugin.onDisable();
        }
    }
    
    public PaperPlaceholders getPlugin() {
        return plugin;
    }
}

