package com.example.hardesthide.client.gui;

import com.example.hardesthide.net.ClientGameState;
import com.example.hardesthide.powerups.PowerupType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class PowerupScreen extends HHSBaseScreen {
    public PowerupScreen() {
        super(Component.literal("Powerups"));
    }

    @Override
    protected void init() {
        int x = width / 2 - 150;
        int y = 58;
        int i = 0;
        for (PowerupType type : PowerupType.values()) {
            PowerupType t = type;
            addRenderableWidget(btn(x, y + i * 25, 300, 22, t.title + " | Cost " + t.cost, b -> {
                if (minecraft != null && minecraft.player != null) {
                    minecraft.player.connection.sendCommand("hhs powerup " + t.name());
                }
                onClose();
            }));
            i++;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        panel(g, width / 2 - 180, 28, 360, height - 56);
        g.drawCenteredString(font, "HIDER POWERUPS", width / 2, 38, 0xFFFFFF);
        g.drawCenteredString(font, "Current tokens: " + ClientGameState.INSTANCE.tokens, width / 2, 48, 0xFFD166);
        super.render(g, mouseX, mouseY, delta);
    }
}
