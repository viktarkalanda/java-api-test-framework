package config;

import exceptions.ConfigurationLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final ThreadLocal<ConfigManager> INSTANCE = ThreadLocal.withInitial(ConfigManager::new);
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        try {
            String env = System.getProperty("env", "test");
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(env + ".properties");
            properties.load(inputStream);
        } catch (IOException e) {
            throw new ConfigurationLoadException("Failed to load properties file.", e);
        }
    }

    public static synchronized ConfigManager getInstance() {
        return INSTANCE.get();
    }

    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public String getSupervisorLogin() {
        return properties.getProperty("supervisor.login");
    }

    public long getApiResponseTimeout() {
        return Long.parseLong(properties.getProperty("api.response.timeout", "5000"));
    }

    public static void clear() {
        INSTANCE.remove();
    }

}
