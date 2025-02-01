package com.bridging.weight.commands;

import com.bridging.weight.Colors;
import com.bridging.weight.ConfigManager;
import com.bridging.weight.Weight;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadCommand implements Command<CommandSourceStack> {
    private final ConfigManager configManager;

    // Constructor
    public ReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    // Overridden run method
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        try {
            ConfigManager.getInstance().reloadConfig();
            context.getSource().sendSuccess(() -> Component.literal(Weight.chatPrefix + Colors.GREEN + "Configuration reloaded."), true);
        } catch (Exception e) {
            context.getSource().sendSuccess(() -> Component.literal(Weight.chatPrefix + Colors.RED + "Failed to reload configuration."), true);
        }
        return 1; // success
    }

    // Register command
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, ConfigManager configuration) {
        dispatcher.register(Commands.literal("weightreload")
                .requires(source -> source.hasPermission(2))
                .executes(new ReloadCommand(configuration))
        );
    }
}