package com.example.hardesthide.game;

import com.example.hardesthide.config.HHSConfig;
import com.example.hardesthide.net.HHSNetworking;
import com.example.hardesthide.questions.HHSQuestion;
import com.example.hardesthide.questions.QuestionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HHSGameManager {
    private final HHSConfig config;
    private final Map<UUID, Role> roles = new HashMap<>();
    private final List<BlackoutRegion> blackoutRegions = new ArrayList<>();
    private final Map<String, Integer> questionCooldowns = new HashMap<>();

    private MatchPhase phase = MatchPhase.LOBBY;
    private UUID hider;
    private Vec3 hiderLockedPos;
    private int hidingTicksLeft;
    private int ticks;
    private int powerupTokens;
    private int huntersFrozenTicks;
    private int markerShuffleTicks;

    public HHSGameManager(HHSConfig config) {
        this.config = config;
    }

    public void assign(ServerPlayer player, Role role) {
        roles.put(player.getUUID(), role);
        if (role == Role.HIDER) hider = player.getUUID();
        player.sendSystemMessage(Component.literal("Assigned role: " + role));
        syncAll();
    }

    public Role roleOf(UUID uuid) {
        return roles.getOrDefault(uuid, Role.NONE);
    }

    public MatchPhase phase() {
        return phase;
    }

    public int tokens() {
        return powerupTokens;
    }

    public List<BlackoutRegion> blackoutRegions() {
        return Collections.unmodifiableList(blackoutRegions);
    }

    public void start(MinecraftServer server) {
        if (hider == null) {
            broadcast(server, "Choose a hider first: /hhs assign hider <player>");
            return;
        }
        blackoutRegions.clear();
        powerupTokens = 0;
        hidingTicksLeft = config.hidingPhaseSeconds() * 20;
        phase = MatchPhase.HIDING;
        broadcast(server, "Hiding phase started. Hider can press READY when hidden.");
        syncAll();
    }

    public void stop(MinecraftServer server) {
        phase = MatchPhase.ENDED;
        hiderLockedPos = null;
        broadcast(server, "Hardest Hide and Seek match ended.");
        syncAll();
    }

    public void ready(ServerPlayer player) {
        if (!player.getUUID().equals(hider)) {
            player.sendSystemMessage(Component.literal("Only the hider can ready."));
            return;
        }
        beginLive(player.server, player);
    }

    private void beginLive(MinecraftServer server, ServerPlayer hiderPlayer) {
        phase = MatchPhase.LIVE;
        hiderLockedPos = hiderPlayer.position();
        broadcast(server, "Hider is READY. Seekers released. The hider is now locked.");
        syncAll();
    }

    public void tick(MinecraftServer server) {
        ticks++;

        if (phase == MatchPhase.HIDING) {
            hidingTicksLeft--;
            if (hidingTicksLeft <= 0) {
                ServerPlayer hiderPlayer = server.getPlayerList().getPlayer(hider);
                if (hiderPlayer != null) beginLive(server, hiderPlayer);
            }
        }

        if (phase == MatchPhase.LIVE && hiderLockedPos != null && ticks % config.hiderLockCorrectionEveryTicks() == 0) {
            ServerPlayer hiderPlayer = server.getPlayerList().getPlayer(hider);
            if (hiderPlayer != null) {
                hiderPlayer.setDeltaMovement(Vec3.ZERO);
                hiderPlayer.teleportTo(hiderLockedPos.x, hiderLockedPos.y, hiderLockedPos.z);
            }
        }

        if (huntersFrozenTicks > 0) huntersFrozenTicks--;
        if (markerShuffleTicks > 0) markerShuffleTicks--;

        questionCooldowns.replaceAll((id, time) -> Math.max(0, time - 1));

        if (ticks % config.minimapSyncEveryTicks() == 0) syncAll(server);
    }

    public boolean ask(ServerPlayer hunter, String questionId) {
        if (phase != MatchPhase.LIVE && phase != MatchPhase.HIDING) {
            hunter.sendSystemMessage(Component.literal("No active match."));
            return false;
        }
        if (roleOf(hunter.getUUID()) != Role.HUNTER) {
            hunter.sendSystemMessage(Component.literal("Only hunters can ask questions."));
            return false;
        }
        HHSQuestion question = QuestionRegistry.get(questionId);
        if (question == null) {
            hunter.sendSystemMessage(Component.literal("Unknown question id: " + questionId));
            return false;
        }
        int cd = questionCooldowns.getOrDefault(question.id(), 0);
        if (cd > 0) {
            hunter.sendSystemMessage(Component.literal("Question cooldown: " + (cd / 20) + "s"));
            return false;
        }

        ServerPlayer hiderPlayer = hunter.server.getPlayerList().getPlayer(hider);
        if (hiderPlayer == null) return false;

        questionCooldowns.put(question.id(), question.cooldownTicks());
        HHSNetworking.sendPendingQuestion(hiderPlayer, hunter.getUUID(), question.id(), question.title());
        broadcast(hunter.server, hunter.getName().getString() + " asked: " + question.title() + " Hider must answer truthfully.");
        return true;
    }

    public void answer(ServerPlayer hiderPlayer, String answer) {
        if (!hiderPlayer.getUUID().equals(hider)) {
            hiderPlayer.sendSystemMessage(Component.literal("Only the hider can answer."));
            return;
        }
        // Prototype: the latest pending question can be passed from GUI later.
        // Command version asks answer format: /hhs answer east_west:west or within_100:yes
        String[] parts = answer.split(":", 2);
        if (parts.length != 2) {
            hiderPlayer.sendSystemMessage(Component.literal("Use /hhs answer <question_id>:<answer>, e.g. east_west:west"));
            return;
        }

        HHSQuestion q = QuestionRegistry.get(parts[0]);
        if (q == null) {
            hiderPlayer.sendSystemMessage(Component.literal("Unknown question."));
            return;
        }

        String a = parts[1].toLowerCase(Locale.ROOT);
        createRevealFromAnswer(hiderPlayer, q.id(), a);
        powerupTokens += q.category().tokenReward;
        broadcast(hiderPlayer.server, "Hider answered: " + a + ". +" + q.category().tokenReward + " powerup token(s).");
        syncAll();
    }

    private void createRevealFromAnswer(ServerPlayer hiderPlayer, String questionId, String answer) {
        BlockPos pos = hiderPlayer.blockPosition();
        int worldMin = -2000;
        int worldMax = 2000;

        switch (questionId) {
            case "east_west" -> {
                if (answer.equals("west")) {
                    blackoutRegions.add(BlackoutRegion.rectangle(pos.getX() + 1, worldMax, worldMin, worldMax, "Eliminated east half"));
                } else if (answer.equals("east")) {
                    blackoutRegions.add(BlackoutRegion.rectangle(worldMin, pos.getX() - 1, worldMin, worldMax, "Eliminated west half"));
                }
            }
            case "north_south" -> {
                if (answer.equals("north")) {
                    blackoutRegions.add(BlackoutRegion.rectangle(worldMin, worldMax, pos.getZ() + 1, worldMax, "Eliminated south half"));
                } else if (answer.equals("south")) {
                    blackoutRegions.add(BlackoutRegion.rectangle(worldMin, worldMax, worldMin, pos.getZ() - 1, "Eliminated north half"));
                }
            }
            case "within_100" -> {
                if (answer.equals("yes")) blackoutRegions.add(BlackoutRegion.circleOutside(pos.getX(), pos.getZ(), 100, "Outside 100 eliminated"));
                if (answer.equals("no")) blackoutRegions.add(BlackoutRegion.circleInside(pos.getX(), pos.getZ(), 100, "Inside 100 eliminated"));
            }
            case "within_250" -> {
                if (answer.equals("yes")) blackoutRegions.add(BlackoutRegion.circleOutside(pos.getX(), pos.getZ(), 250, "Outside 250 eliminated"));
                if (answer.equals("no")) blackoutRegions.add(BlackoutRegion.circleInside(pos.getX(), pos.getZ(), 250, "Inside 250 eliminated"));
            }
            default -> blackoutRegions.add(BlackoutRegion.circleInside(pos.getX(), pos.getZ(), 70, "Intel revealed zone"));
        }
    }

    public boolean spendTokens(int amount) {
        if (powerupTokens < amount) return false;
        powerupTokens -= amount;
        return true;
    }

    public void freezeHunters(int ticks) {
        huntersFrozenTicks = Math.max(huntersFrozenTicks, ticks);
    }

    public void shuffleMarkersTemporarily(int ticks) {
        markerShuffleTicks = Math.max(markerShuffleTicks, ticks);
    }

    public void addMatchTime(int seconds) {
        hidingTicksLeft += seconds * 20;
    }

    public void refreshHiderLockPosition(ServerPlayer player) {
        if (player.getUUID().equals(hider)) hiderLockedPos = player.position();
    }

    public void addRandomFakeBlackout(BlockPos near) {
        int r = 60;
        blackoutRegions.add(BlackoutRegion.circleInside(near.getX() + 120, near.getZ() - 80, r, "Fake blackout"));
    }

    public void syncAll() {
        // Called from command contexts where server may not be directly available.
    }

    public void syncAll(MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            HHSNetworking.sendState(p, this);
        }
    }

    private void broadcast(MinecraftServer server, String msg) {
        server.getPlayerList().broadcastSystemMessage(Component.literal("[HHS] " + msg), false);
    }
}
