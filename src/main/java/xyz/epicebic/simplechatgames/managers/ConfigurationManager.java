package xyz.epicebic.simplechatgames.managers;

public class ConfigurationManager {

    private static final ConfigurationManager INSTANCE = new ConfigurationManager();

    private ConfigurationManager() {

    }

    public void init() {
        // TODO
    }

    public void loadConfigs() {
        if (oldConfigsExist()) {
            // TODO
        }
    }

    public void updateOldConfigs() {
        // TODO
    }

    public boolean oldConfigsExist() {
        // TODO
        return false;
    }

    public static ConfigurationManager getInstance() {
        return INSTANCE;
    }
}
