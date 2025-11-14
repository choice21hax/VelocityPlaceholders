package site.choice21;

import java.lang.reflect.Method;

/**
 * PlaceholderAPI expansion for Paper/Spigot
 * Uses reflection to avoid compile-time dependency
 */
public class PlaceholderAPIExpansion {
    
    public static Object create(PaperPlaceholders plugin) {
        try {
            Class<?> implClass = Class.forName("site.choice21.PlaceholderAPIExpansionImpl");
            Object expansion = implClass.getConstructor(PaperPlaceholders.class).newInstance(plugin);
            return expansion;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean register(Object expansion) {
        try {
            Method registerMethod = expansion.getClass().getMethod("register");
            Object result = registerMethod.invoke(expansion);
            return result instanceof Boolean ? (Boolean) result : false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static void unregister(Object expansion) {
        try {
            Method unregisterMethod = expansion.getClass().getMethod("unregister");
            unregisterMethod.invoke(expansion);
        } catch (Exception e) {
            // Ignore
        }
    }
}
