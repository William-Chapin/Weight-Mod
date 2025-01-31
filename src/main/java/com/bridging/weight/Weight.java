package com.bridging.weight;

import com.bridging.weight.commands.ReloadCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Weight implements ModInitializer {
    public final String messagePrefix = "[Weight 1.0] ";

    // Initialize the mod
    @Override
    public void onInitialize() {
        System.out.println(messagePrefix + "Mod has been initialized.");

        // Initialize the events manager
        EventsManager eventsManager = new EventsManager();
        ConfigManager configManager = ConfigManager.getInstance();

        // Register reload command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ReloadCommand.register(dispatcher, configManager);
        });
    }
}
