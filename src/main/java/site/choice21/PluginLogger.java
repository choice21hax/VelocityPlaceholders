package site.choice21;

/**
 * Simple logger interface to abstract platform-specific loggers
 */
public interface PluginLogger {
    void info(String message);
    void warn(String message);
    void error(String message);
    void error(String message, Throwable throwable);
}

