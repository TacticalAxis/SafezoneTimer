package io.github.tacticalaxis.safezonetimer.util;

import io.github.tacticalaxis.safezonetimer.SafezoneTimer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationManager {

    private ConfigurationManager() {}

    static final ConfigurationManager instance = new ConfigurationManager();

    public static ConfigurationManager getInstance() {
        return instance;
    }

    private static final String mainConfigName = "config.yml";

    private final ArrayList<String> ymlFiles = new ArrayList<>();
    private final HashMap<String, FileConfiguration> configs = new HashMap<>();

    FileConfiguration mainConfiguration;
    File mainFile;

    // main config setup
    public void setupConfiguration() {
        configTest();
        mainFile = new File(SafezoneTimer.getInstance().getDataFolder(), mainConfigName);
        mainConfiguration = YamlConfiguration.loadConfiguration(mainFile);
        ymlFiles.add(mainConfigName);
        configs.put(mainConfigName, mainConfiguration);
    }

    // test if config exists, if not, create files
    private static void configTest() {
        SafezoneTimer main = SafezoneTimer.getInstance();
        try {
            if (!main.getDataFolder().exists()) {
                boolean success = main.getDataFolder().mkdirs();
                if (!success) {
                    System.out.println("Configuration files could not be created!");
                    Bukkit.shutdown();
                }
            }
            File file = new File(main.getDataFolder(), ConfigurationManager.mainConfigName);
            if (!file.exists()) {
                main.getLogger().info(ConfigurationManager.mainConfigName + " not found, creating!");
                main.saveResource(ConfigurationManager.mainConfigName, true);
            } else {
                main.getLogger().info(ConfigurationManager.mainConfigName + " found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MAIN CONFIGURATION
    public FileConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public void saveMainConfiguration() {
        try {
            mainConfiguration.save(mainFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Could not save " + mainConfigName + "!");
        }
    }

    public void reloadMainConfig() {
        mainConfiguration = YamlConfiguration.loadConfiguration(mainFile);
    }

    public void reloadConfigurations() {
        for (String ymlFile : this.ymlFiles) {
            try {
                this.configs.get(ymlFile).load(new File(SafezoneTimer.getInstance().getDataFolder(), ymlFile));
            } catch (Exception ignored) {
            }
        }
        setupConfiguration();
    }

    public List<String> getSafezones() {
        if (getMainConfiguration().getStringList("safezones") != null) {
            return getMainConfiguration().getStringList("safezones");
        } else {
            return null;
        }
    }
}