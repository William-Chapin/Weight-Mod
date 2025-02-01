package com.bridging.weight;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;

public class ActionBarUtil {
    public static void sendActionBar(ServerPlayer player, String message) {
        Component text = Component.literal(message);
        ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(text);
        player.connection.send(packet);
    }

    // Clears a player's action bar
    public static void clearActionBar(ServerPlayer player) {
        sendActionBar(player, "");
    }
}