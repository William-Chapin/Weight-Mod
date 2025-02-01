package com.bridging.weight;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

public class EventsManager {

    // Constructor
    public EventsManager() {
        initEvents();
    }

    // Initialize events
    public void initEvents() {
        // Every tick update the players weights
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            PlayerManager playerManager = new PlayerManager(server.getPlayerList().getPlayers());
            playerManager.updateWeights();
        });
        // On disconnect, reset attributes and also clear action bar
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            PlayerManager playerManager = new PlayerManager(server.getPlayerList().getPlayers());
            playerManager.resetAttributes(player);
            ActionBarUtil.clearActionBar(player);
        });
    }
}