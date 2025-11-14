# VelocityPlaceholders

A multi-platform Minecraft plugin that provides placeholders for server player counts. Works on both **Velocity** (proxy) and **Paper/Spigot** (backend servers) with a single JAR file.

## Features

- ✅ **Multi-Platform Support**: One JAR works on both Velocity and Paper/Spigot
- ✅ **PlaceholderAPI Integration**: Full PAPI support on Paper servers
- ✅ **Automatic Updates**: Player counts update every 5 seconds
- ✅ **Easy Configuration**: Simple TOML configuration file
- ✅ **Multiple Servers**: Track player counts for multiple servers simultaneously

## Installation

### For Velocity (Proxy Server)

1. Download the latest JAR from the releases
2. Place it in your `plugins/` folder
3. Restart your Velocity server
4. Configure servers in `plugins/VelocityPlaceholders/config.toml`

### For Paper/Spigot (Backend Server)

1. Download the latest JAR from the releases
2. Place it in your `plugins/` folder
3. **Optional but Recommended**: Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for full placeholder support
4. Restart your server
5. Configure servers in `plugins/VelocityPlaceholders/config.toml`

## Configuration

Edit `config.toml` in your plugin's data folder:

```toml
# OnlinePlayersPlugin Configuration
# Add servers you want to track player counts for
# Format: server_name = "server_id"
# The server_name is used in the placeholder: %online_<server_name>%

# On Velocity: server_id is the registered server name in velocity.toml
# On Paper: server_id can be:
#   - "current" or "this" to get the current server's player count
#   - "host:port" format (e.g., "localhost:25566") to ping another server
#   - A BungeeCord server name if using BungeeCord API

[servers]
modernprac = "modernprac"
survival = "survival"
battleroyale = "battleroyale"
```

### Velocity Configuration

On Velocity, the `server_id` must match a server name registered in your `velocity.toml`:

```toml
# velocity.toml
servers:
  modernprac:
    address: "127.0.0.1:25565"
  survival:
    address: "127.0.0.1:25566"
```

### Paper Configuration

On Paper, you have several options:

- **Current Server**: Use `"current"` or `"this"` to get the current server's player count
- **Direct Ping**: Use `"host:port"` format to ping another server directly
- **BungeeCord**: If using BungeeCord API, use the server name

Example:
```toml
[servers]
current = "current"           # Current server's player count
lobby = "localhost:25567"     # Ping another server
hub = "hub"                   # BungeeCord server name
```

## Usage

### PlaceholderAPI (Paper/Spigot)

If you have PlaceholderAPI installed, you can use these placeholders:

- `%onlineplayers_online_<server>%` - Get player count for a server

**Examples:**
- `%onlineplayers_online_modernprac%` - Returns player count for "modernprac" server
- `%onlineplayers_online_survival%` - Returns player count for "survival" server
- `%onlineplayers_online_battleroyale%` - Returns player count for "battleroyale" server

### Programmatic API

You can also access player counts programmatically:

#### On Velocity:
```java
VelocityPlaceholders plugin = // Get plugin instance
int count = plugin.getPlayerCount("modernprac");
String replaced = plugin.replacePlaceholders("Players: %online_modernprac%");
```

#### On Paper:
```java
OnlinePlayersPluginMain main = (OnlinePlayersPluginMain) getServer().getPluginManager().getPlugin("VelocityPlaceholders");
PaperPlaceholders plugin = main.getPlugin();
int count = plugin.getPlayerCount("modernprac");
String replaced = plugin.replacePlaceholders("Players: %online_modernprac%");
```

## Troubleshooting

### "Failed to ping server" Errors

If you see warnings like:
```
[WARN]: [VelocityPlaceholders] Failed to ping server modernprac: modernprac
```

**On Velocity:**
1. Make sure the server name in `config.toml` matches a server registered in `velocity.toml`
2. Ensure the backend server is running and accessible
3. Check that the server address in `velocity.toml` is correct

**On Paper:**
1. If using `host:port` format, verify the server is accessible from the network
2. Check firewall settings
3. Ensure the target server is online
4. For "current" server, this should always work

### Placeholders Not Working

**On Paper with PlaceholderAPI:**
1. Make sure PlaceholderAPI is installed and enabled
2. Reload PlaceholderAPI: `/papi reload`
3. Verify the placeholder format: `%onlineplayers_online_<server>%`
4. Check that the server name in the placeholder matches your config

**Without PlaceholderAPI:**
- Placeholders will only work via the programmatic API
- Install PlaceholderAPI for full support in chat, commands, and other plugins

## Building from Source

1. Clone this repository
2. Run `mvn clean package`
3. Find the JAR in `target/VelocityPlaceholders-1.1.0.jar`

## Requirements

- **Velocity**: Velocity 3.0.0 or higher
- **Paper**: Paper/Spigot 1.20+ (or adjust API version in `pom.xml`)
- **Java**: Java 17 or higher
- **PlaceholderAPI** (optional but recommended for Paper): Version 2.11.0+

## License

This plugin is provided as-is. Feel free to modify and use as needed.

## Support

For issues, questions, or contributions, please open an issue on the repository.

## Version History

- **1.1.0**: Added PlaceholderAPI support, improved error handling
- **1.0.0**: Initial release with Velocity and Paper support

