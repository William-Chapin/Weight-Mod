package com.bridging.weight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class PlayerManager {
    private List<ServerPlayer> players;
    private static final double DEFAULT_SPEED = 0.10000000149011612;
    private static final double DEFAULT_BREAKING_SPEED = 1.0;

    // Constructor
    public PlayerManager(List<ServerPlayer> players) {
        this.players = players;
    }

    // Reset attributes (speed, breaking speed, fall damage)
    public void resetAttributes(ServerPlayer player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(DEFAULT_SPEED);
        player.getAttribute(Attributes.BLOCK_BREAK_SPEED).setBaseValue(DEFAULT_BREAKING_SPEED);
        player.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER).setBaseValue(1);
        player.getAttribute(Attributes.GRAVITY).setBaseValue(0.08);
    }

    // Check if weather is impacting weight
    private boolean isWeather(ServerPlayer player) {
        boolean weatherSlowdown = Boolean.parseBoolean(ConfigManager.getInstance().getConfig("weatherSlowdown"));
        if (weatherSlowdown) {
            return player.level().isRaining() || player.level().isThundering();
        }
        return false;
    }

    // Check if nether is impacting weight
    private boolean isNether(ServerPlayer player) {
        boolean netherSlowdown = Boolean.parseBoolean(ConfigManager.getInstance().getConfig("netherSlowdown"));
        if (netherSlowdown) {
            return player.level().dimension().location().toString().contains("minecraft:the_nether");
        }
        return false;
    }

    private double calculateItemWeight(ItemStack item, ConfigManager configManager) {
        String itemName = item.getItem().toString();
        itemName = itemName.substring(itemName.indexOf(":") + 1);

        Optional<Double> weight = configManager.getWeight(itemName);
        // Has a configured weight
        if (weight.isPresent()) {
            return weight.get() * item.getCount();
        } else {
            // Get default weights
            double armorWeight = Double.parseDouble(configManager.getConfig("armorWeight"));
            double toolWeight = Double.parseDouble(configManager.getConfig("toolWeight"));
            double defaultWeight = Double.parseDouble(configManager.getConfig("defaultWeight"));

            // Check if item is armor
            Item itemType = item.getItem();
            boolean isArmor = (itemType instanceof ArmorItem);
            boolean isTool = (itemName.contains("sword") || itemName.contains("pickaxe") || (itemName.contains("axe") && !itemName.contains("wa")) || itemName.contains("shovel") || itemName.contains("hoe") || itemName.contains("shears") || itemName.contains("trident") || (itemName.contains("bow") && !itemName.equals("bowl")));
            if (isArmor) {
                return armorWeight * item.getCount();
            } else if (isTool) {
                return toolWeight * item.getCount();
            } else {
                return defaultWeight * item.getCount();
            }
        }
    }

    // Get the total weight of a player
    private double getWeight(ServerPlayer player) {
        double totalWeight = 1;
        ConfigManager configManager = ConfigManager.getInstance();
        boolean includeEquipped = Boolean.parseBoolean(configManager.getConfig("includeEquipped"));

        // Inventory items
        for (ItemStack item : player.getInventory().items) {
            if (item != ItemStack.EMPTY && !item.isEmpty()) {
                totalWeight += calculateItemWeight(item, configManager);
            }
        }

        // Armor and offhand items
        if (includeEquipped) {
            for (ItemStack armorItem : player.getInventory().armor) {
                totalWeight += calculateItemWeight(armorItem, configManager);
            }

            for (ItemStack offhandItem : player.getInventory().offhand) {
                totalWeight += calculateItemWeight(offhandItem, configManager);
            }
        }

        // Modify weight based on weather
        boolean weatherSlowdown = Boolean.parseBoolean(configManager.getConfig("weatherSlowdown"));
        double weatherWeight = Double.parseDouble(configManager.getConfig("weatherWeight"));
        if (weatherSlowdown && isWeather(player)) {
            totalWeight += weatherWeight;
        }

        // Modify weight based on nether
        boolean netherSlowdown = Boolean.parseBoolean(configManager.getConfig("netherSlowdown"));
        double netherWeight = Double.parseDouble(configManager.getConfig("netherWeight"));
        if (netherSlowdown && isNether(player)) {
            totalWeight += netherWeight;
        }

        // Final weight calculation
        double maxWeight = Double.parseDouble(configManager.getConfig("maxWeight"));
        double slownessMultiplier = Double.parseDouble(configManager.getConfig("slownessMultiplier"));
        totalWeight = (totalWeight - 1) * slownessMultiplier + 1;
        if (totalWeight > maxWeight) {
            totalWeight = maxWeight;
        }
        return totalWeight;
    }

    public void updateWeights() {
        ConfigManager configManager = ConfigManager.getInstance();
        boolean creativeSlowdown = Boolean.parseBoolean(configManager.getConfig("creativeSlowdown"));
        boolean breakingSpeed = Boolean.parseBoolean(configManager.getConfig("breakingSpeed"));
        boolean fallDamage = Boolean.parseBoolean(configManager.getConfig("fallDamage"));
        boolean actionBar = Boolean.parseBoolean(configManager.getConfig("actionBar"));
        double fallDamageWeight = Double.parseDouble(configManager.getConfig("fallDamageWeight"));

        for (ServerPlayer player : players) {
            double weight = getWeight(player);

            // Prevent slowdown if player is in creative/spectator mode and configured
            if ((player.isCreative() || player.isSpectator()) && !creativeSlowdown) {
                weight = 1;
            }

            // Set movement speed
            double speed = DEFAULT_SPEED / weight;
            player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

            // Set breaking speed
            if (breakingSpeed) {
                double breakingSpeedValue = DEFAULT_BREAKING_SPEED / weight;
                player.getAttribute(Attributes.BLOCK_BREAK_SPEED).setBaseValue(breakingSpeedValue);
            }

            // Set fall damage amount
            if (fallDamage) {
                double fallDamageIncrease = fallDamageWeight * (weight - 1); // 0.5 * (2 - 1) = 0.5
                player.getAttribute(Attributes.FALL_DAMAGE_MULTIPLIER).setBaseValue(fallDamageIncrease + 1);
            }

            // Action bar message
            if (actionBar && !(player.isCreative() || player.isSpectator() && !creativeSlowdown)) {
                String weatherEmoji = "§b☂";
                String netherEmoji = "§4☄";

                double percentOfSpeed = (weight - 1) * 100;
                percentOfSpeed = Math.round(percentOfSpeed * 100.0) / 100.0;

                String color = Colors.getColor(percentOfSpeed);

                String extraInfo = "";
                if (isWeather(player)) {
                    extraInfo += weatherEmoji;
                }
                if (isNether(player)) {
                    extraInfo += netherEmoji;
                }

                if (!extraInfo.isEmpty()) {
                    ActionBarUtil.sendActionBar(player, color + "-" + percentOfSpeed + "% §fSpeed (" + extraInfo + "§f)");
                } else {
                    ActionBarUtil.sendActionBar(player, color + "-" + percentOfSpeed + "% §fSpeed");
                }
            }

            // Send weight notifications if enabled
            boolean chatMessages = Boolean.parseBoolean(configManager.getConfig("chatMessages"));
            if (chatMessages && !(player.isCreative() || player.isSpectator() && !creativeSlowdown)) {
                NotificationManager notificationManager = new NotificationManager();
                notificationManager.sendWeightNotification(player, weight);
            }
        }
    }
}
