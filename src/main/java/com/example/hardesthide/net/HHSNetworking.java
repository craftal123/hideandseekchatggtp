package com.example.hardesthide.net;

import com.example.hardesthide.HardestHideMod;
import com.example.hardesthide.game.BlackoutRegion;
import com.example.hardesthide.game.HHSGameManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class HHSNetworking {
    public static final ResourceLocation STATE_ID = ResourceLocation.fromNamespaceAndPath(HardestHideMod.MOD_ID, "state");
    public static final ResourceLocation QUESTION_ID = ResourceLocation.fromNamespaceAndPath(HardestHideMod.MOD_ID, "pending_question");

    public record StatePayload(String data) implements CustomPacketPayload {
        public static final Type<StatePayload> TYPE = new Type<>(STATE_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, StatePayload> CODEC =
                StreamCodec.of((buf, payload) -> buf.writeUtf(payload.data), buf -> new StatePayload(buf.readUtf(32767)));

        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record PendingQuestionPayload(String questionId, String title) implements CustomPacketPayload {
        public static final Type<PendingQuestionPayload> TYPE = new Type<>(QUESTION_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, PendingQuestionPayload> CODEC =
                StreamCodec.of((buf, payload) -> {
                    buf.writeUtf(payload.questionId);
                    buf.writeUtf(payload.title);
                }, buf -> new PendingQuestionPayload(buf.readUtf(256), buf.readUtf(256)));

        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void registerServer() {
        // Client-to-server GUI packets can be added here.
    }

    public static void registerClient() {
        // To keep this scaffold compiler-light, state receiving can be wired here:
        // ClientPlayNetworking.registerGlobalReceiver(StatePayload.TYPE, (payload, context) -> { ... });
        // The rendering classes already use ClientGameState.INSTANCE.
    }

    public static void sendState(ServerPlayer player, HHSGameManager game) {
        StringBuilder data = new StringBuilder();
        data.append(game.phase()).append("|").append(game.roleOf(player.getUUID())).append("|").append(game.tokens()).append("|");
        for (ServerPlayer p : player.server.getPlayerList().getPlayers()) {
            BlockPos pos = p.blockPosition();
            data.append("P,").append(p.getUUID()).append(",").append(pos.getX()).append(",").append(pos.getY()).append(",").append(pos.getZ()).append(";");
        }
        data.append("|");
        for (BlackoutRegion r : game.blackoutRegions()) {
            data.append("B,").append(r.type()).append(",").append(r.centerX()).append(",").append(r.centerZ()).append(",").append(r.radius())
                    .append(",").append(r.minX()).append(",").append(r.maxX()).append(",").append(r.minZ()).append(",").append(r.maxZ())
                    .append(",").append(r.label().replace(",", " ")).append(";");
        }
        // ServerPlayNetworking.send(player, new StatePayload(data.toString()));
    }

    public static void sendPendingQuestion(ServerPlayer hider, UUID hunterId, String questionId, String title) {
        // ServerPlayNetworking.send(hider, new PendingQuestionPayload(questionId, title));
        hider.sendSystemMessage(net.minecraft.network.chat.Component.literal("QUESTION: " + title + " | Answer with /hhs answer " + questionId + ":<answer>"));
    }

    private HHSNetworking() {}
}
