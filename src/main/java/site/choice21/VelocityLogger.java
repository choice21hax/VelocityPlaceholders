package site.choice21;

import org.slf4j.Logger;

/**
 * Wrapper for Velocity's SLF4J logger
 */
public class VelocityLogger implements PluginLogger {
    private final Logger logger;
    
    public VelocityLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void info(String message) {
        logger.info(message);
    }
    
    @Override
    public void warn(String message) {
        logger.warn(message);
    }
    
    @Override
    public void error(String message) {
        logger.error(message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}

