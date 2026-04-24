package com.example.hardesthide.client.gui;

import com.example.hardesthide.net.ClientGameState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class FullMapScreen extends HHSBaseScreen {
    public FullMapScreen() {
        super(Component.literal("Hardest Hide and Seek Map"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height - 42;
        addRenderableWidget(btn(cx - 70, y, 140, 24, "READY /hhs ready", b -> {
            if (minecraft != null && minecraft.player != null) minecraft.player.connection.sendCommand("hhs ready");
            onClose();
        }));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        int w = Math.min(width - 80, 420);
        int h = Math.min(height - 90, 280);
        int x = (width - w) / 2;
        int y = 32;
        panel(g, x, y, w, h);
        g.drawCenteredString(font, "FULL TACTICAL MAP", width / 2, y + 12, 0xFFFFFF);
        g.drawCenteredString(font, "Blackout regions: " + ClientGameState.INSTANCE.blackoutRegions.size(), width / 2, y + 34, 0xAAAAAA);
        g.drawCenteredString(font, "Terrain rasterizer placeholder - ready for custom textures/resource pack.", width / 2, y + 58, 0x777777);
        super.render(g, mouseX, mouseY, delta);
    }
}
