package site.choice21;

import java.util.logging.Logger;

/**
 * Wrapper for Paper's Java logger
 */
public class PaperLogger implements PluginLogger {
    private final Logger logger;
    
    public PaperLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    public void info(String message) {
        logger.info(message);
    }
    
    @Override
    public void warn(String message) {
        logger.warning(message);
    }
    
    @Override
    public void error(String message) {
        logger.severe(message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        logger.log(java.util.logging.Level.SEVERE, message, throwable);
    }
}

