package fun.rtos.modchecker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.RequestPacket;
import fun.rtos.modchecker.utils.ModCheckerConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ModCheckerCommand {
    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("mod-checker")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ModCheckerCommand::listMods)
                .then(
                    Commands.literal("reload")
                        .executes(ModCheckerCommand::reload)
                )
                .then(
                    Commands.literal("mode")
                        .executes(ModCheckerCommand::getMode)
                        .then(
                            Commands.argument("mode", StringArgumentType.word())
                                .executes(ModCheckerCommand::setMode)
                                .suggests(
                                    (context, builder) ->
                                        SharedSuggestionProvider.suggest(
                                            Arrays.stream(ModCheckerConfig.Mode.values()).map(Object::toString).toList(),
                                            builder
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("add")
                        .then(
                            Commands.argument("mod", StringArgumentType.word())
                                .executes(ModCheckerCommand::addMod)
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("mod", StringArgumentType.word())
                                .executes(ModCheckerCommand::removeMod)
                        )
                )
                .then(
                    Commands.literal("list")
                        .executes(ModCheckerCommand::listMods)
                )
        );
    }

    public static int reload(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ModChecker.loadConfig();
        ModCheckerCommand.flush(context);
        source.sendSuccess(
            () -> Component
                .literal("Reloaded config")
                .withStyle(ChatFormatting.DARK_GREEN),
            false);
        return 1;
    }

    public static int getMode(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(
            () -> Component
                .literal("Current mode:")
                .withStyle(ChatFormatting.DARK_GREEN)
                .append(
                    Component
                        .literal(ModChecker.CONFIG.getMode().toString())
                        .withStyle(ChatFormatting.AQUA)
                ),
            false);
        return 1;
    }

    public static int setMode(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String mode = StringArgumentType.getString(context, "mode");
        ModCheckerConfig.Mode newMode = ModCheckerConfig.Mode.fromString(mode);
        ModChecker.CONFIG.setMode(newMode);
        ModChecker.saveConfig(source.getServer(), false, true);
        ModCheckerCommand.flush(context);
        source.sendSuccess(
            () -> Component
                .literal("New mode:")
                .withStyle(ChatFormatting.DARK_GREEN)
                .append(
                    Component
                        .literal(newMode.toString())
                        .withStyle(ChatFormatting.AQUA)
                ),
            false);
        return 1;
    }

    public static int addMod(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String mod = StringArgumentType.getString(context, "mod");
        ModChecker.CONFIG.addMod(mod);
        ModChecker.saveConfig(source.getServer(), false, true);
        ModCheckerCommand.flush(context);
        source.sendSuccess(
            () -> Component
                .literal("Added mod:")
                .withStyle(ChatFormatting.DARK_GREEN)
                .append(
                    Component
                        .literal(mod)
                        .withStyle(ChatFormatting.AQUA)
                ),
            false);
        return 1;
    }

    public static int removeMod(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String mod = StringArgumentType.getString(context, "mod");
        ModChecker.CONFIG.removeMod(mod);
        ModChecker.saveConfig(source.getServer(), false, true);
        ModCheckerCommand.flush(context);
        source.sendSuccess(
            () -> Component
                .literal("Removed mod:")
                .withStyle(ChatFormatting.DARK_GREEN)
                .append(
                    Component
                        .literal(mod)
                        .withStyle(ChatFormatting.AQUA)
                ),
            false);
        return 1;
    }

    public static int listMods(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MutableComponent mode = Component
            .literal("Current mode:")
            .withStyle(ChatFormatting.DARK_GREEN)
            .append(
                Component
                    .literal(ModChecker.CONFIG.getMode().toString())
                    .withStyle(ChatFormatting.AQUA)
            );
        source.sendSuccess(() -> mode, false);
        MutableComponent mods = Component.literal("Current mods:").withStyle(ChatFormatting.DARK_GREEN);
        source.sendSuccess(() -> mods, false);
        for (String mod : ModChecker.CONFIG.getMods()) {
            MutableComponent component = Component.literal(mod).withStyle(ChatFormatting.GOLD);
            source.sendSuccess(() -> component, false);
        }
        return 1;
    }

    private static void flush(@NotNull CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, new RequestPacket());
        }
    }
}
