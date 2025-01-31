package com.bridging.weight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class NotificationManager {
    private final Map<ServerPlayer, Double> lastNotifiedWeight = new HashMap<>();
    double interval = 25;

    // Sends a message to a player
    public void sendNotification(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }

    // Sends message to a player if their item weight has exceeded a threshold of 25
    public void sendWeightNotification(ServerPlayer player, double weight) {
        double threshold = Math.floor(weight / interval) * interval; // Round down to nearest interval of 25
        Double lastThreshold = lastNotifiedWeight.get(player);

        if (lastThreshold == null || threshold != lastThreshold) {
            String color = Colors.getColor(threshold);
            String message = Weight.chatPrefix + Colors.GRAY + "Your weight has reached " + color + threshold + Colors.GRAY + "!";
            sendNotification(player, message);
            lastNotifiedWeight.put(player, threshold);
        }
    }
}
