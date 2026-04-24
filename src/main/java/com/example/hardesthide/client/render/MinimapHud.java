package com.example.hardesthide.client.render;

import com.example.hardesthide.game.BlackoutRegion;
import com.example.hardesthide.net.ClientGameState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;

import java.util.Map;
import java.util.UUID;

public final class MinimapHud {
    public static void render(GuiGraphics g, Minecraft client, ClientGameState state) {
        if (client.player == null) return;

        int x = 8;
        int y = 8;
        int size = 112;
        int center = size / 2;
        int scale = 4; // blocks per minimap pixel

        g.fill(x - 2, y - 2, x + size + 2, y + size + 14, ARGB.color(180, 6, 8, 12));
        g.fill(x, y, x + size, y + size, ARGB.color(170, 22, 28, 34));
        g.drawString(client.font, "HHS MAP", x + 4, y + size + 3, 0xE6E6E6, false);

        BlockPos me = client.player.blockPosition();

        // Blackout regions as dramatic transparent marks.
        for (BlackoutRegion r : state.blackoutRegions) {
            if (r.type() == BlackoutRegion.Type.RECTANGLE) {
                int minPx = center + (r.minX() - me.getX()) / scale;
                int maxPx = center + (r.maxX() - me.getX()) / scale;
                int minPz = center + (r.minZ() - me.getZ()) / scale;
                int maxPz = center + (r.maxZ() - me.getZ()) / scale;
                g.fill(clamp(x + minPx, x, x + size), clamp(y + minPz, y, y + size),
                       clamp(x + maxPx, x, x + size), clamp(y + maxPz, y, y + size),
                       ARGB.color(140, 0, 0, 0));
            } else {
                int cx = x + center + (r.centerX() - me.getX()) / scale;
                int cz = y + center + (r.centerZ() - me.getZ()) / scale;
                int rad = Math.max(2, r.radius() / scale);
                g.fill(clamp(cx - rad, x, x + size), clamp(cz - rad, y, y + size),
                       clamp(cx + rad, x, x + size), clamp(cz + rad, y, y + size),
                       ARGB.color(95, 0, 0, 0));
            }
        }

        // Other player markers.
        for (Map.Entry<UUID, BlockPos> e : state.playerPositions.entrySet()) {
            BlockPos p = e.getValue();
            int px = x + center + (p.getX() - me.getX()) / scale;
            int pz = y + center + (p.getZ() - me.getZ()) / scale;
            if (px >= x && px <= x + size && pz >= y && pz <= y + size) {
                g.fill(px - 2, pz - 2, px + 3, pz + 3, ARGB.color(255, 255, 210, 65));
            }
        }

        // Self marker.
        g.fill(x + center - 2, y + center - 2, x + center + 3, y + center + 3, ARGB.color(255, 85, 255, 125));
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private MinimapHud() {}
}
