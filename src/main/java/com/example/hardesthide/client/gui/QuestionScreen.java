package com.example.hardesthide.client.gui;

import com.example.hardesthide.questions.HHSQuestion;
import com.example.hardesthide.questions.QuestionCategory;
import com.example.hardesthide.questions.QuestionRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestionScreen extends HHSBaseScreen {
    private QuestionCategory selected = QuestionCategory.DIRECTION;

    public QuestionScreen() {
        super(Component.literal("Ask Question"));
    }

    @Override
    protected void init() {
        int left = width / 2 - 180;
        int y = 54;
        int i = 0;
        for (QuestionCategory cat : QuestionCategory.values()) {
            final QuestionCategory c = cat;
            addRenderableWidget(btn(left, y + i * 25, 120, 22, cat.name() + " +" + cat.tokenReward, b -> {
                selected = c;
                rebuildWidgets();
            }));
            i++;
        }

        int qx = left + 135;
        int qy = 54;
        List<HHSQuestion> questions = new ArrayList<>(QuestionRegistry.all());
        questions.sort(Comparator.comparing(HHSQuestion::id));
        int row = 0;
        for (HHSQuestion q : questions) {
            if (q.category() != selected) continue;
            addRenderableWidget(btn(qx, qy + row * 25, 230, 22, q.title(), b -> {
                if (minecraft != null && minecraft.player != null) {
                    minecraft.player.connection.sendCommand("hhs ask " + q.id());
                }
                onClose();
            }));
            row++;
        }
    }

    private void rebuildWidgets() {
        clearWidgets();
        init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        panel(g, width / 2 - 200, 28, 400, height - 56);
        g.drawCenteredString(font, "QUESTION BOARD", width / 2, 38, 0xFFFFFF);
        g.drawString(font, "Selected: " + selected + " | Reward: +" + selected.tokenReward + " token(s)", width / 2 - 40, 78, 0xCCCCCC, false);
        super.render(g, mouseX, mouseY, delta);
    }
}
