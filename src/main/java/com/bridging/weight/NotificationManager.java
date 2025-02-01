package com.bridging.weight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationManager {
    private static NotificationManager instance;
    private final List<PlayerThreshold> playerThresholds = new ArrayList<>();

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    // Sends a message to a player
    public void sendNotification(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }

    // Sends message to a player if their item weight increases
    public void sendWeightNotification(ServerPlayer player, double weight) {
        double interval = Double.parseDouble(ConfigManager.getInstance().getConfig("chatInterval"));
        double threshold = Math.floor((weight - 1) * 100 / interval) * interval;
        int thresholdInt = (int) threshold;
        UUID playerUUID = player.getUUID();
        // Check if player already has a threshold
        for (PlayerThreshold pt : playerThresholds) {
            if (pt.getPlayerUUID().equals(playerUUID)) {
                double oldThreshold = pt.getThreshold();
                if (threshold <= oldThreshold) {
                    pt.setThreshold(threshold);
                    return;
                }
                if (threshold > oldThreshold && threshold != 0) {
                    pt.setThreshold(threshold);
                    String message = Weight.chatPrefix + Colors.GRAY + "You are now " + Colors.getColor(threshold) + thresholdInt + "%" + Colors.GRAY + " slower!";
                    sendNotification(player, message);
                    return;
                }
                return;
            }
        }
        // Add threshold and send a notification
        if (threshold != 0) {
            PlayerThreshold playerThreshold = new PlayerThreshold(playerUUID, threshold);
            playerThresholds.add(playerThreshold);
            String message = Weight.chatPrefix + Colors.GRAY + "You are now " + Colors.getColor(threshold) + thresholdInt + "%" + Colors.GRAY + " slower!";
            sendNotification(player, message);
        }
    }

    // Player threshold class
    private static class PlayerThreshold {
        private final UUID playerUUID;
        private double threshold;
        // Constructor
        public PlayerThreshold(UUID playerUUID, double threshold) {
            this.playerUUID = playerUUID;
            this.threshold = threshold;
        }
        // Get a player's UUID
        public UUID getPlayerUUID() {
            return playerUUID;
        }
        // Get a player's threshold
        public double getThreshold() {
            return threshold;
        }
        // Set a player's threshold
        public void setThreshold(double threshold) {
            this.threshold = threshold;
        }
    }
}