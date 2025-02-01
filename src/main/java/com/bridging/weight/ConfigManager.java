package com.bridging.weight;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.reflect.Type;

public class ConfigManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CONFIG_FILE_PATH = "mods/weight/configuration.properties";
    private static final String WEIGHT_FILE_PATH = "mods/weight/weights.json";
    private static final List<String> REQUIRED_PROPERTIES = Arrays.asList("slownessMultiplier", "includeEquipped", "maxWeight", "armorWeight", "toolWeight", "defaultWeight", "actionBar", "weatherSlowdown", "weatherWeight", "netherSlowdown", "netherWeight", "breakingSpeed", "fallDamage", "fallDamageWeight", "creativeSlowdown", "chatMessages", "endSlowdown", "endWeight", "chatInterval");

    private static ConfigManager instance;

    private Map<String, Double> itemWeights = new HashMap<>();
    private List<Weights> weights;
    private final Properties properties = new Properties();


    // Constructor
    private ConfigManager() {
        try {
            initializeFiles();
            loadConfig();
            loadWeights();
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error(Weight.messagePrefix + "Failed to load configuration: " + e.getMessage() + ".");
        }
    }

    // Get instance
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    // Initialize configuration files
    private void initializeFiles() throws IOException {
        createFileIfNotExists(CONFIG_FILE_PATH, "assets/weight/default_configuration.properties");
        createFileIfNotExists(WEIGHT_FILE_PATH, "assets/weight/default_weight_config.json");
    }

    // Creates configurations if they don't exist
    private void createFileIfNotExists(String filePath, String defaultFilePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.notExists(path)) {
            try (InputStream defaultStream = getClass().getClassLoader().getResourceAsStream(defaultFilePath)) {
                if (defaultFilePath == null) {
                    throw new FileNotFoundException(Weight.messagePrefix + "Default file not found.");
                }
                Files.createDirectories(path.getParent());
                assert defaultStream != null;
                Files.copy(defaultStream, path);
                LOGGER.info(Weight.messagePrefix + "Generated a default file.");
            }
        }
    }

    // Update configuration files
    public void reloadConfig() {
        try {
            loadConfig();
            loadWeights();
        } catch (IOException e) {
            LOGGER.error(Weight.messagePrefix + "Failed to reload configuration file.");
        } catch (IllegalStateException e) {
            LOGGER.error(Weight.messagePrefix + "Invalid configuration file.");
        }
    }

    // Load configuration file
    public void loadConfig() throws IOException {
        try (InputStream configStream = Files.newInputStream(Paths.get(CONFIG_FILE_PATH))) {
            properties.load(configStream);
        }
        validateConfig();
    }

    // Load weight file
    public void loadWeights() {
        try (InputStream weightStream = Files.newInputStream(Paths.get(WEIGHT_FILE_PATH))) {
            InputStreamReader reader = new InputStreamReader(weightStream);
            Type listType = new TypeToken<WeightsWrapper>() {
            }.getType();
            WeightsWrapper wrapper = new Gson().fromJson(reader, listType);
            weights = wrapper.itemWeights;
            for (Weights weight : weights) {
                itemWeights.put(weight.item, weight.percent);
            }
        } catch (JsonSyntaxException | IOException e) {
            LOGGER.error(Weight.messagePrefix + "Failed to load weights configuration: " + e.getMessage() + ".");
        }
        validateWeights();
    }

    // Ensure configuration file is valid
    public void validateConfig() {
        // Required properties
        for (String property : REQUIRED_PROPERTIES) {
            if (!this.properties.containsKey(property)) {
                LOGGER.warn(Weight.messagePrefix + "Configuration is missing required property: " + property + ".");
                throw new IllegalStateException(Weight.messagePrefix + "Configuration is missing required property: " + property + ".");
            }
        }
    }

    // Ensure weights file is valid
    public void validateWeights() {
        if (weights == null || weights.isEmpty()) {
            throw new IllegalStateException(Weight.messagePrefix + "Weights configuration is empty or invalid.");
        }
        for (Weights weight : weights) {
            if (weight.item == null || weight.item.isEmpty()) {
                throw new IllegalStateException(Weight.messagePrefix + "Weights configuration has an item without a name.");
            }
            if (weight.percent < 0.0) {
                throw new IllegalStateException(Weight.messagePrefix + "Weights configuration has an item with an invalid weight.");
            }
        }
    }

    // Get configuration values
    public String getConfig(String key) {
        return properties.getProperty(key);
    }

    // Get weight values for a certain item
    public Optional<Double> getWeight(String itemName) {
        return Optional.ofNullable(itemWeights.get(itemName));
    }

    // Weights class
    public static class Weights {
        public String item;
        public double percent;
    }

    // WeightsWrapper class
    public static class WeightsWrapper {
        List<Weights> itemWeights;
    }
}