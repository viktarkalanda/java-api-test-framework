package config;

import exceptions.ConfigurationLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
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

    public String getAdminLogin() {
        return properties.getProperty("admin.login");
    }

    public static void clear() {
        INSTANCE.remove();
    }

}
