package site.choice21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PluginConfig {
    private Map<String, String> servers = new HashMap<>();
    
    public static PluginConfig load(Path configPath) throws IOException {
        PluginConfig config = new PluginConfig();
        
        if (!Files.exists(configPath)) {
            return config;
        }
        
        String content = Files.readString(configPath);
        
        // Simple TOML parser for [servers] section
        boolean inServersSection = false;
        for (String line : content.split("\n")) {
            line = line.trim();
            
            if (line.startsWith("[servers]")) {
                inServersSection = true;
                continue;
            }
            
            if (line.startsWith("[")) {
                inServersSection = false;
                continue;
            }
            
            if (inServersSection && line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim().replace("\"", "").replace("'", "");
                    config.servers.put(key, value);
                }
            }
        }
        
        return config;
    }
    
    public Map<String, String> getServers() {
        return servers;
    }
}

