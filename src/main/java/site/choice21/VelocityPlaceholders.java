package site.choice21;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Plugin(
    id = "velocityplaceholders",
    name = "VelocityPlaceholders",
    version = "1.1.0",
    description = "Provides placeholders for server player counts",
    authors = {"choice21"}
)
public class VelocityPlaceholders extends AbstractOnlinePlayersPlugin {
    
    private final ProxyServer server;
    private final org.slf4j.Logger slf4jLogger;
    private final Path dataDirectory;
    private final VelocityLogger logger;
    
    @Inject
    public VelocityPlaceholders(ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.slf4jLogger = logger;
        this.dataDirectory = dataDirectory;
        this.logger = new VelocityLogger(logger);
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
                            if (throwable != null) {
                                logger.warn("Failed to ping server '" + serverName + "' (id: " + serverId + "): " + throwable.getMessage());
                            } else {
                                logger.warn("Failed to ping server '" + serverName + "' (id: " + serverId + "): No response");
                            }
                            serverPlayerCounts.put(serverName, 0);
                        }
                    });
                },
                () -> {
                    logger.warn("Server '" + serverName + "' (id: " + serverId + ") not found!");
                    serverPlayerCounts.put(serverName, 0);
                }
            );
        }
    }
    
    @Override
    protected PluginLogger getPluginLogger() {
        return logger;
    }
    
    @Override
    public Path getDataDirectory() {
        return dataDirectory;
    }
    
    @Override
    public Object getLogger() {
        return slf4jLogger;
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
}
