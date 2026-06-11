package com.itqa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages application-level configuration from config.properties.
 */
public class ConfigManager {

    private static final Properties props = new Properties();
    private static ConfigManager instance;

    static {
        try {
            String configPath = System.getProperty("config.file",
                    "src/test/resources/config.properties");
            props.load(new FileInputStream(configPath));
        } catch (IOException e) {
            // Fall back to defaults
        }
    }

    private ConfigManager() {}

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public static String get(String key) {
        return props.getProperty(key, getDefault(key));
    }

    private static String getDefault(String key) {
        return switch (key) {
            case "base.url"       -> "http://localhost:8080";
            case "admin.username" -> "admin";
            case "admin.password" -> "admin123";
            case "user.username"  -> "testuser";
            case "user.password"  -> "test123";
            case "browser"        -> "chrome";
            case "implicit.wait"  -> "10";
            case "explicit.wait"  -> "15";
            case "headless"       -> "false";
            default               -> "";
        };
    }

    public static String getBaseUrl()       { return get("base.url"); }
    public static String getAdminUsername() { return get("admin.username"); }
    public static String getAdminPassword() { return get("admin.password"); }
    public static String getUserUsername()  { return get("user.username"); }
    public static String getUserPassword()  { return get("user.password"); }
    public static int    getImplicitWait()  { return Integer.parseInt(get("implicit.wait")); }
    public static int    getExplicitWait()  { return Integer.parseInt(get("explicit.wait")); }
    public static boolean isHeadless()      { return Boolean.parseBoolean(get("headless")); }
}
