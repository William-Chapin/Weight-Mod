package com.bridging.weight;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;

public class EventsManager {


    // Constructor
    public EventsManager(){
        initEvents();
    }

    // initialize events
    public void initEvents(){
        // Every tick
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            PlayerManager playerManager = new PlayerManager(server.getPlayerList().getPlayers());
            playerManager.updateWeights();
        });

        // On disconnect, reset attributes
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            PlayerManager playerManager = new PlayerManager(server.getPlayerList().getPlayers());
            playerManager.resetAttributes(player);
        });
    }
}
