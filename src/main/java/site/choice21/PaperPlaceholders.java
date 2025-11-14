package site.choice21;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Paper/Spigot implementation of the plugin
 */
public class PaperPlaceholders extends AbstractOnlinePlayersPlugin {
    
    private final JavaPlugin plugin;
    private final PaperLogger logger;
    private final Path dataDirectory;
    private BukkitTask updateTask;
    
    public PaperPlaceholders(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = new PaperLogger(plugin.getLogger());
        this.dataDirectory = plugin.getDataFolder().toPath();
    }
    
    public void onEnable() {
        logger.info("PaperPlaceholders is starting up...");
        
        // Load configuration
        loadConfig();
        
        // Start updating player counts periodically
        startPlayerCountUpdater();
        
        logger.info("PaperPlaceholders has been enabled!");
    }
    
    public void onDisable() {
        logger.info("PaperPlaceholders is shutting down...");
        if (updateTask != null) {
            updateTask.cancel();
        }
    }
    
    private void startPlayerCountUpdater() {
        // Run every 5 seconds (100 ticks)
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updatePlayerCounts, 0L, 100L);
    }
    
    private void updatePlayerCounts() {
        if (config == null || config.getServers().isEmpty()) {
            return;
        }
        
        for (Map.Entry<String, String> entry : config.getServers().entrySet()) {
            String serverName = entry.getKey();
            String serverId = entry.getValue();
            
            // Check if it's the current server
            if (serverId.equalsIgnoreCase("current") || serverId.equalsIgnoreCase("this")) {
                serverPlayerCounts.put(serverName, Bukkit.getOnlinePlayers().size());
                continue;
            }
            
            // Try to ping the server
            pingServer(serverName, serverId);
        }
    }
    
    private void pingServer(String serverName, String serverId) {
        // Parse server address (format: host:port)
        String[] parts = serverId.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 25565;
        
        InetSocketAddress address = new InetSocketAddress(host, port);
        
        // Use async task to ping server
        CompletableFuture.supplyAsync(() -> {
            try {
                return pingMinecraftServer(address);
            } catch (Exception e) {
                logger.warn("Failed to ping server " + serverId + ": " + e.getMessage());
                return 0;
            }
        }).thenAccept(count -> {
            serverPlayerCounts.put(serverName, count);
        });
    }
    
    private int pingMinecraftServer(InetSocketAddress address) throws IOException {
        // Simple Minecraft server ping implementation
        // This is a basic implementation - you might want to use a library like MCStatus
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(address, 3000); // 3 second timeout
            socket.setSoTimeout(3000);
            
            java.io.DataOutputStream out = new java.io.DataOutputStream(socket.getOutputStream());
            java.io.DataInputStream in = new java.io.DataInputStream(socket.getInputStream());
            
            // Send handshake packet
            out.writeByte(0x00); // Packet ID
            writeVarInt(out, 4); // Protocol version
            writeString(out, address.getHostString());
            out.writeShort(address.getPort());
            writeVarInt(out, 1); // Next state: status
            
            // Send status request
            out.writeByte(0x00);
            
            // Read response
            readVarInt(in); // Packet length
            int packetId = readVarInt(in);
            if (packetId != 0x00) {
                return 0;
            }
            
            String jsonResponse = readString(in);
            // Parse JSON to get player count
            // Simple JSON parsing for "players":{"online":X}
            int onlineIndex = jsonResponse.indexOf("\"online\":");
            if (onlineIndex != -1) {
                int start = onlineIndex + 9;
                int end = jsonResponse.indexOf(',', start);
                if (end == -1) end = jsonResponse.indexOf('}', start);
                if (end != -1) {
                    String countStr = jsonResponse.substring(start, end).trim();
                    return Integer.parseInt(countStr);
                }
            }
            return 0;
        }
    }
    
    private void writeVarInt(java.io.DataOutputStream out, int value) throws IOException {
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                out.writeByte(value);
                return;
            }
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }
    
    private int readVarInt(java.io.DataInputStream in) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;
        
        while (true) {
            currentByte = in.readByte();
            value |= (currentByte & 0x7F) << (position * 7);
            
            if ((currentByte & 0x80) == 0) {
                break;
            }
            
            position++;
            
            if (position >= 5) {
                throw new IOException("VarInt too long");
            }
        }
        
        return value;
    }
    
    private void writeString(java.io.DataOutputStream out, String string) throws IOException {
        byte[] bytes = string.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    
    private String readString(java.io.DataInputStream in) throws IOException {
        int length = readVarInt(in);
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
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
        return plugin.getLogger();
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

