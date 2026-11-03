package co.za.nuka.config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

//import static sun.awt.FontConfiguration.loadProperties;

public final class ConfigManager {


    private static final String DEFAULT_ENV = "dev";
    private static final List<String> SUPPORT_ENVS = Arrays.asList("dev","prod");
    private static final String DEFAULT_BASE_URL = "";

    private static final ConfigManager INSTANCE = new ConfigManager();

    private final String env;
    private final Properties properties;

    public ConfigManager() {
        this.env = resolveEnv();
        this.properties = loadProperties(this.env);
    }

    public static ConfigManager getInstance(){ return INSTANCE;}

    private String resolveEnv(){
        String requested = System.getProperty("env");
        if( requested == null  || requested.trim().isEmpty()){

            requested = System.getProperty("Karate.env");
        }

        if( requested == null || requested.trim().isEmpty()){

            System.out.println("[ConfigManager] No 'env' system property set. Defaulting to   '" + DEFAULT_ENV + "'.");
            return DEFAULT_ENV;

        }

        String normalized = requested.trim().toLowerCase();

        if(!SUPPORT_ENVS.contains(normalized)){
            System.out.println("[ConfigManager] WARNING: Unsupported environment  '" + requested +"' Falling back to '" +DEFAULT_ENV +"' . SUPPORTED: " + SUPPORT_ENVS);

            return DEFAULT_ENV;
        }

        return normalized;

    }

    private Properties loadProperties(String environment){
        Properties props = new Properties();

        String resource = "cnfig/application-" + environment + ".properties";

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)){
            if(in == null){
                System.out.println();
                props.setProperty("base.url", DEFAULT_BASE_URL);
                props.setProperty("connect.timeout","10000");
                props.setProperty("read.timeout","30000");
                props.setProperty("ssl.enabled","true");

                return props;
            }

            props.load(in);
            System.out.println("[] loaded configuration for env '" + environment + "' -> base.url " + props.getProperty("base.url"));
        }
        catch (Exception e){
            throw new RuntimeException("Failed to load configuration for env  '" + environment +"'", e);

        }

        return props;
    }

    public String getEnv() {
        return env;
    }

    public String getBaseUrl(){
        return properties.getProperty("base.url",DEFAULT_BASE_URL);
    }

    public int getConnectionTimeOut(){
        return Integer.parseInt(properties.getProperty("connect.timeout","10000"));

    }

    public int getReadTimeout(){
        return Integer.parseInt(properties.getProperty("read.timeout","30000"));
    }

    public boolean isSslEnabled(){
        return Boolean.parseBoolean(properties.getProperty("ssl_enlaned","true"));
    }
    public String get(String key){
        return properties.getProperty(key);
    }

    public String get(String key,String defaultValue){
        String sysProp = System.getProperty(key);

        if(sysProp != null && !sysProp.trim().isEmpty()){
            return sysProp.trim();
        }

        String fileValue = properties.getProperty(key);
        return (fileValue != null && !fileValue.trim().isEmpty()) ? fileValue.trim() : defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue){
        String value = get(key, null);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}
