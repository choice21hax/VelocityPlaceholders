package site.choice21;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for Paper/Spigot
 * This class is loaded when the plugin runs on Paper/Spigot
 */
public class OnlinePlayersPluginMain extends JavaPlugin {
    private PaperPlaceholders plugin;
    
    @Override
    public void onEnable() {
        plugin = new PaperPlaceholders(this);
        plugin.onEnable();
    }
    
    @Override
    public void onDisable() {
        if (plugin != null) {
            plugin.onDisable();
        }
    }
    
    public PaperPlaceholders getPlugin() {
        return plugin;
    }
}

