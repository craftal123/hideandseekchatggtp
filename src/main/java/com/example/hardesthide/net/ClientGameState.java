package com.example.hardesthide.net;

import com.example.hardesthide.game.BlackoutRegion;
import com.example.hardesthide.game.MatchPhase;
import com.example.hardesthide.game.Role;
import net.minecraft.core.BlockPos;

import java.util.*;

public class ClientGameState {
    public static final ClientGameState INSTANCE = new ClientGameState();

    public MatchPhase phase = MatchPhase.LOBBY;
    public Role myRole = Role.NONE;
    public int tokens = 0;
    public final Map<UUID, BlockPos> playerPositions = new HashMap<>();
    public final List<BlackoutRegion> blackoutRegions = new ArrayList<>();
    public String pendingQuestionId = "";
    public String pendingQuestionTitle = "";

    public void clear() {
        phase = MatchPhase.LOBBY;
        myRole = Role.NONE;
        tokens = 0;
        playerPositions.clear();
        blackoutRegions.clear();
    }
}
