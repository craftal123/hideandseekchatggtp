package com.example.hardesthide.config;

public record HHSConfig(
        int hidingPhaseSeconds,
        int minimapSyncEveryTicks,
        int hiderLockCorrectionEveryTicks,
        int basePowerupCooldownTicks,
        int smokeRadius,
        int invisibilityTicks,
        int freezeHuntersTicks,
        int shortTeleportMaxDistance
) {
    public static HHSConfig defaults() {
        return new HHSConfig(
                90,
                5,
                2,
                20 * 20,
                8,
                20 * 8,
                20 * 4,
                28
        );
    }
}
