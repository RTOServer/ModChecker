package fun.rtos.modchecker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                            Commands.argument("mod", StringArgumentType.greedyString())
                                .executes(ModCheckerCommand::addMod)
                        )
                )
                .then(
                    Commands.literal("remove")
                        .then(
                            Commands.argument("mod", StringArgumentType.word())
                                .executes(ModCheckerCommand::removeMod)
                                .suggests(
                                    (context, builder) ->
                                        SharedSuggestionProvider.suggest(ModChecker.CONFIG.getMods(), builder)
                                )
                        )
                )
                .then(
                    Commands.literal("list")
                        .executes(ModCheckerCommand::listMods)
                        .then(
                            Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(ModCheckerCommand::listMods)
                        )
                )
        );
    }

    public static int reload(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ModChecker.CONFIG.loadConfig();
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
        source.sendSuccess(ModCheckerCommand::getMode, false);
        return 1;
    }

    public static int setMode(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String mode = StringArgumentType.getString(context, "mode");
        ModCheckerConfig.Mode newMode = ModCheckerConfig.Mode.fromString(mode);
        ModChecker.CONFIG.setMode(newMode);
        ModChecker.CONFIG.saveConfig();
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
        if (!mod.contains(",")) ModChecker.CONFIG.addMod(mod);
        else ModChecker.CONFIG.addMods(new ArrayList<>(Arrays.asList(mod.split(","))));
        ModChecker.CONFIG.saveConfig();
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
        ModChecker.CONFIG.saveConfig();
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
        int page = 1;
        try {
            page = IntegerArgumentType.getInteger(context, "page");
        } catch (Exception ignored) {
        }
        source.sendSuccess(ModCheckerCommand::getMode, false);
        List<MutableComponent> mods = listMods(page);
        for (MutableComponent component : mods) {
            source.sendSuccess(() -> component, false);
        }
        return 1;
    }

    private static @NotNull MutableComponent getMode() {
        return Component
            .literal("Current mode:")
            .withStyle(ChatFormatting.DARK_GREEN)
            .append(
                Component
                    .literal(ModChecker.CONFIG.getMode().toString())
                    .withStyle(ChatFormatting.AQUA)
            );
    }

    private static final int PAGE_SIZE = 7;

    private static @NotNull List<MutableComponent> listMods(int page) {
        List<String> mods = ModChecker.CONFIG.getMods();
        int maxPage = mods.size() / PAGE_SIZE + 1;
        if (page > maxPage) page = maxPage;
        List<MutableComponent> result = new ArrayList<>();
        result.add(Component.literal("Current mods:").withStyle(ChatFormatting.DARK_GREEN));
        for (int i = (page - 1) * PAGE_SIZE; i < page * PAGE_SIZE; i++) {
            if (i >= mods.size()) break;
            MutableComponent component = Component.literal(mods.get(i)).withStyle(ChatFormatting.GOLD);
            result.add(component);
        }
        result.add(
            Component.literal("")
                .append(
                    page == 1 ?
                        Component.literal("<<<").withStyle(ChatFormatting.GRAY) :
                        Component.literal("<<<").withStyle(
                            Style.EMPTY.applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/mod-checker list %s".formatted(page - 1)
                                ))
                        )
                )
                .append(Component.literal(" Page %d/%d ".formatted(page, maxPage)).withStyle(ChatFormatting.AQUA))
                .append(
                    page == maxPage ?
                        Component.literal(">>>").withStyle(ChatFormatting.GRAY) :
                        Component.literal(">>>").withStyle(
                            Style.EMPTY.applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/mod-checker list %s".formatted(page + 1)
                                ))
                        )
                )
        );
        return result;
    }

    private static void flush(@NotNull CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, new RequestPacket());
        }
    }
}
