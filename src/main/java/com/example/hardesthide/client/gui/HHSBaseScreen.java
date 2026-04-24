package com.example.hardesthide.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public abstract class HHSBaseScreen extends Screen {
    protected HHSBaseScreen(Component title) {
        super(title);
    }

    protected void panel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, ARGB.color(230, 10, 13, 20));
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, ARGB.color(210, 20, 25, 35));
    }

    protected Button btn(int x, int y, int w, int h, String text, Button.OnPress press) {
        return Button.builder(Component.literal(text), press).bounds(x, y, w, h).build();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
