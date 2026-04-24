package com.example.hardesthide.game;

import com.example.hardesthide.HardestHideMod;
import com.example.hardesthide.powerups.PowerupService;
import com.example.hardesthide.powerups.PowerupType;
import com.example.hardesthide.questions.QuestionRegistry;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class HHSCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("hhs")
                .then(literal("assign")
                        .then(literal("hider")
                                .then(argument("player", EntityArgument.player())
                                        .requires(src -> src.hasPermission(2))
                                        .executes(ctx -> {
                                            ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                                            HardestHideMod.GAME.assign(p, Role.HIDER);
                                            return 1;
                                        })))
                        .then(literal("hunter")
                                .then(argument("player", EntityArgument.player())
                                        .requires(src -> src.hasPermission(2))
                                        .executes(ctx -> {
                                            ServerPlayer p = EntityArgument.getPlayer(ctx, "player");
                                            HardestHideMod.GAME.assign(p, Role.HUNTER);
                                            return 1;
                                        }))))
                .then(literal("start").requires(src -> src.hasPermission(2)).executes(ctx -> {
                    HardestHideMod.GAME.start(ctx.getSource().getServer());
                    return 1;
                }))
                .then(literal("stop").requires(src -> src.hasPermission(2)).executes(ctx -> {
                    HardestHideMod.GAME.stop(ctx.getSource().getServer());
                    return 1;
                }))
                .then(literal("ready").executes(ctx -> {
                    HardestHideMod.GAME.ready(ctx.getSource().getPlayerOrException());
                    return 1;
                }))
                .then(literal("tokens").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("Hider tokens: " + HardestHideMod.GAME.tokens()), false);
                    return 1;
                }))
                .then(literal("questions").executes(ctx -> {
                    QuestionRegistry.all().forEach(q -> ctx.getSource().sendSuccess(() ->
                            Component.literal(q.id() + " - " + q.title() + " [" + q.category() + "]"), false));
                    return 1;
                }))
                .then(literal("ask").then(argument("question_id", StringArgumentType.word()).executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    return HardestHideMod.GAME.ask(player, StringArgumentType.getString(ctx, "question_id")) ? 1 : 0;
                })))
                .then(literal("answer").then(argument("answer", StringArgumentType.greedyString()).executes(ctx -> {
                    HardestHideMod.GAME.answer(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "answer"));
                    return 1;
                })))
                .then(literal("powerup").then(argument("powerup_id", StringArgumentType.word()).executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PowerupType type = PowerupType.byId(StringArgumentType.getString(ctx, "powerup_id"));
                    if (type == null) {
                        player.sendSystemMessage(Component.literal("Unknown powerup."));
                        return 0;
                    }
                    return PowerupService.usePowerup(HardestHideMod.GAME, player, type) ? 1 : 0;
                })))
        ));
    }

    private HHSCommands() {}
}
