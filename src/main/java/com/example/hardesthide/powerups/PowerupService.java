package com.example.hardesthide.powerups;

import com.example.hardesthide.HardestHideMod;
import com.example.hardesthide.game.HHSGameManager;
import com.example.hardesthide.game.Role;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public final class PowerupService {
    private static final Random RANDOM = new Random();

    public static boolean usePowerup(HHSGameManager game, ServerPlayer player, PowerupType type) {
        if (game.roleOf(player.getUUID()) != Role.HIDER) {
            player.sendSystemMessage(Component.literal("Only the hider can use powerups."));
            return false;
        }
        if (!game.spendTokens(type.cost)) {
            player.sendSystemMessage(Component.literal("Not enough powerup tokens. Need " + type.cost + "."));
            return false;
        }

        ServerLevel level = player.serverLevel();

        switch (type) {
            case SMOKE_BURST -> {
                level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.4f, 0.7f);
                player.sendSystemMessage(Component.literal("Smoke burst used."));
            }
            case TEMP_INVISIBILITY -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, HardestHideMod.CONFIG.invisibilityTicks(), 0, false, false, true));
            }
            case FAKE_FOOTSTEPS -> {
                BlockPos fake = player.blockPosition().offset(RANDOM.nextInt(60) - 30, 0, RANDOM.nextInt(60) - 30);
                level.playSound(null, fake, SoundEvents.GRASS_STEP, SoundSource.PLAYERS, 2.0f, 0.8f);
                level.playSound(null, fake.offset(3, 0, -2), SoundEvents.STONE_STEP, SoundSource.PLAYERS, 2.0f, 1.1f);
            }
            case DECOY_MOB -> {
                Chicken chicken = EntityType.CHICKEN.create(level);
                if (chicken != null) {
                    chicken.moveTo(player.getX() + 4, player.getY(), player.getZ() + 4);
                    chicken.setCustomName(Component.literal("???"));
                    level.addFreshEntity(chicken);
                }
            }
            case FREEZE_HUNTERS -> game.freezeHunters(HardestHideMod.CONFIG.freezeHuntersTicks());
            case SHUFFLE_MARKERS -> game.shuffleMarkersTemporarily(20 * 12);
            case ADD_TIME -> game.addMatchTime(30);
            case SHORT_TELEPORT -> {
                int d = HardestHideMod.CONFIG.shortTeleportMaxDistance();
                player.teleportTo(player.getX() + RANDOM.nextInt(d * 2 + 1) - d, player.getY(), player.getZ() + RANDOM.nextInt(d * 2 + 1) - d);
                game.refreshHiderLockPosition(player);
            }
            case BLACKOUT_SECTION -> game.addRandomFakeBlackout(player.blockPosition());
        }
        game.syncAll();
        return true;
    }

    private PowerupService() {}
}
