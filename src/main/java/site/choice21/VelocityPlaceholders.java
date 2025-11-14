package site.choice21;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Plugin(
    id = "velocityplaceholders",
    name = "VelocityPlaceholders",
    version = "1.0.0",
    description = "Provides placeholders for server player counts",
    authors = {"choice21"}
)
public class VelocityPlaceholders {
    
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private PluginConfig config;
    private final Map<String, Integer> serverPlayerCounts = new HashMap<>();
    private PlaceholderManager placeholderManager;
    
    @Inject
    public VelocityPlaceholders(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.placeholderManager = new PlaceholderManager(this);
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocityPlaceholders is starting up...");
        
        // Load configuration
        loadConfig();
        
        // Start updating player counts periodically
        startPlayerCountUpdater();
        
        logger.info("VelocityPlaceholders has been enabled!");
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("VelocityPlaceholders is shutting down...");
    }
    
    private void loadConfig() {
        File configFile = dataDirectory.resolve("config.toml").toFile();
        
        if (!configFile.exists()) {
            // Create default config
            try {
                dataDirectory.toFile().mkdirs();
                try (InputStream in = getClass().getResourceAsStream("/config.toml")) {
                    if (in != null) {
                        Files.copy(in, configFile.toPath());
                    } else {
                        // Create a default config if resource doesn't exist
                        createDefaultConfig(configFile);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to create default config file", e);
                return;
            }
        }
        
        try {
            config = PluginConfig.load(configFile.toPath());
            logger.info("Loaded configuration with {} servers", config.getServers().size());
        } catch (IOException e) {
            logger.error("Failed to load config file", e);
            config = new PluginConfig();
        }
    }
    
    private void createDefaultConfig(File configFile) throws IOException {
        String defaultConfig = "# VelocityPlaceholders Configuration\n" +
                "# Add servers you want to track player counts for\n" +
                "# Format: server_name = \"server_id\"\n" +
                "\n" +
                "[servers]\n" +
                "modernprac = \"modernprac\"\n";
        Files.write(configFile.toPath(), defaultConfig.getBytes());
    }
    
    private void startPlayerCountUpdater() {
        server.getScheduler().buildTask(this, this::updatePlayerCounts)
                .repeat(5, TimeUnit.SECONDS)
                .schedule();
    }
    
    private void updatePlayerCounts() {
        if (config == null || config.getServers().isEmpty()) {
            return;
        }
        
        for (Map.Entry<String, String> entry : config.getServers().entrySet()) {
            String serverName = entry.getKey();
            String serverId = entry.getValue();
            
            server.getServer(serverId).ifPresentOrElse(
                registeredServer -> {
                    CompletableFuture<ServerPing> pingFuture = registeredServer.ping();
                    pingFuture.whenComplete((ping, throwable) -> {
                        if (throwable == null && ping != null) {
                            int playerCount = ping.getPlayers()
                                    .map(ServerPing.Players::getOnline)
                                    .orElse(0);
                            serverPlayerCounts.put(serverName, playerCount);
                        } else {
                            serverPlayerCounts.put(serverName, 0);
                        }
                    });
                },
                () -> {
                    logger.warn("Server '{}' (id: {}) not found!", serverName, serverId);
                    serverPlayerCounts.put(serverName, 0);
                }
            );
        }
    }
    
    /**
     * Get the player count for a specific server
     * @param serverName The server name (e.g., "modernprac")
     * @return The player count, or 0 if not found
     */
    public int getPlayerCount(String serverName) {
        return serverPlayerCounts.getOrDefault(serverName, 0);
    }
    
    /**
     * Get the placeholder value for a server
     * @param placeholder The placeholder string (e.g., "online_modernprac")
     * @return The player count as string, or "0" if not found
     */
    public String getPlaceholder(String placeholder) {
        if (placeholder.startsWith("online_")) {
            String serverName = placeholder.substring("online_".length());
            return String.valueOf(getPlayerCount(serverName));
        }
        return "0";
    }
    
    /**
     * Get the placeholder manager for replacing placeholders in strings
     * @return The PlaceholderManager instance
     */
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }
    
    /**
     * Replace all placeholders in a string
     * Convenience method that delegates to PlaceholderManager
     * @param text The text containing placeholders like %online_modernprac%
     * @return The text with placeholders replaced
     */
    public String replacePlaceholders(String text) {
        return placeholderManager.replacePlaceholders(text);
    }
}

