package com.example.hardesthide.client.render;

import com.example.hardesthide.net.ClientGameState;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

/**
 * Extension point for dramatic in-world blackout.
 *
 * Recommended final implementation:
 * - Render translucent vertical quads over eliminated areas.
 * - Add low black fog particles near boundaries.
 * - Fade alpha over 8-12 ticks after each reveal.
 * - Cull by camera distance and chunk.
 *
 * The minimap blackout already uses the same synced region model.
 */
public final class WorldBlackoutRenderer {
    public static void render(WorldRenderContext ctx, ClientGameState state) {
        // Keep empty in prototype to avoid version-fragile low-level BufferBuilder code.
        // Add mesh rendering here after the gameplay loop is verified.
    }

    private WorldBlackoutRenderer() {}
}
