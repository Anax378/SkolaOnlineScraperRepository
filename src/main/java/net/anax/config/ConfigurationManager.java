package net.anax.config;
public class ConfigurationManager {
    private static ConfigurationManager INSTANCE;
    private Configuration configuration;
    private ConfigurationManager(){}
    public static ConfigurationManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConfigurationManager();
        }
        return INSTANCE;
    }

    public Configuration getConfiguration(){
        if(configuration == null){
            loadConfiguration();
            return configuration;
        }
        return configuration;
    }

    private void loadConfiguration(){
        configuration = new Configuration();
        configuration.user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0";
    }

}
