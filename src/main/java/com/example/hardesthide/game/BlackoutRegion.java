package com.example.hardesthide.game;

import net.minecraft.core.BlockPos;

public record BlackoutRegion(
        Type type,
        int centerX,
        int centerZ,
        int radius,
        int minX,
        int maxX,
        int minZ,
        int maxZ,
        String label
) {
    public enum Type {
        RECTANGLE,
        CIRCLE_INSIDE,
        CIRCLE_OUTSIDE
    }

    public boolean contains(BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        if (type == Type.RECTANGLE) {
            return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
        }
        long dx = x - centerX;
        long dz = z - centerZ;
        boolean inside = dx * dx + dz * dz <= (long) radius * radius;
        return type == Type.CIRCLE_INSIDE ? inside : !inside;
    }

    public static BlackoutRegion rectangle(int minX, int maxX, int minZ, int maxZ, String label) {
        return new BlackoutRegion(Type.RECTANGLE, 0, 0, 0, minX, maxX, minZ, maxZ, label);
    }

    public static BlackoutRegion circleInside(int centerX, int centerZ, int radius, String label) {
        return new BlackoutRegion(Type.CIRCLE_INSIDE, centerX, centerZ, radius, 0, 0, 0, 0, label);
    }

    public static BlackoutRegion circleOutside(int centerX, int centerZ, int radius, String label) {
        return new BlackoutRegion(Type.CIRCLE_OUTSIDE, centerX, centerZ, radius, 0, 0, 0, 0, label);
    }
}
