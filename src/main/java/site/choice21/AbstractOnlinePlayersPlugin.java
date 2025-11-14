package site.choice21;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class with shared functionality for both Velocity and Paper
 */
public abstract class AbstractOnlinePlayersPlugin implements OnlinePlayersPlugin {
    protected PluginConfig config;
    protected final Map<String, Integer> serverPlayerCounts = new HashMap<>();
    protected PlaceholderManager placeholderManager;
    
    public AbstractOnlinePlayersPlugin() {
        this.placeholderManager = new PlaceholderManager(this);
    }
    
    protected void loadConfig() {
        File configFile = getDataDirectory().resolve("config.toml").toFile();
        
        if (!configFile.exists()) {
            // Create default config
            try {
                getDataDirectory().toFile().mkdirs();
                try (InputStream in = getClass().getResourceAsStream("/config.toml")) {
                    if (in != null) {
                        Files.copy(in, configFile.toPath());
                    } else {
                        // Create a default config if resource doesn't exist
                        createDefaultConfig(configFile);
                    }
                }
            } catch (IOException e) {
                getPluginLogger().error("Failed to create default config file", e);
                return;
            }
        }
        
        try {
            config = PluginConfig.load(configFile.toPath());
            getPluginLogger().info("Loaded configuration with " + config.getServers().size() + " servers");
        } catch (IOException e) {
            getPluginLogger().error("Failed to load config file", e);
            config = new PluginConfig();
        }
    }
    
    protected void createDefaultConfig(File configFile) throws IOException {
        String defaultConfig = "# OnlinePlayersPlugin Configuration\n" +
                "# Add servers you want to track player counts for\n" +
                "# Format: server_name = \"server_id\"\n" +
                "# On Velocity: server_id is the registered server name\n" +
                "# On Paper: server_id is the server address (host:port) or BungeeCord server name\n" +
                "\n" +
                "[servers]\n" +
                "modernprac = \"modernprac\"\n";
        Files.write(configFile.toPath(), defaultConfig.getBytes());
    }
    
    @Override
    public int getPlayerCount(String serverName) {
        return serverPlayerCounts.getOrDefault(serverName, 0);
    }
    
    @Override
    public String replacePlaceholders(String text) {
        return placeholderManager.replacePlaceholders(text);
    }
    
    /**
     * Abstract method to get logger - implemented by platform-specific classes
     */
    protected abstract PluginLogger getPluginLogger();
    
    /**
     * Abstract method to get data directory - implemented by platform-specific classes
     */
    public abstract Path getDataDirectory();
}

