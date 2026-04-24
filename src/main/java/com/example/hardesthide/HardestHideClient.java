package com.example.hardesthide;

import com.example.hardesthide.client.gui.FullMapScreen;
import com.example.hardesthide.client.gui.PowerupScreen;
import com.example.hardesthide.client.gui.QuestionScreen;
import com.example.hardesthide.client.render.MinimapHud;
import com.example.hardesthide.client.render.WorldBlackoutRenderer;
import com.example.hardesthide.net.ClientGameState;
import com.example.hardesthide.net.HHSNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class HardestHideClient implements ClientModInitializer {
    public static KeyMapping OPEN_MAP;
    public static KeyMapping OPEN_QUESTIONS;
    public static KeyMapping OPEN_POWERUPS;

    @Override
    public void onInitializeClient() {
        HHSNetworking.registerClient();

        OPEN_MAP = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.hardesthide.open_map",
                GLFW.GLFW_KEY_M,
                "category.hardesthide"
        ));
        OPEN_QUESTIONS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.hardesthide.open_questions",
                GLFW.GLFW_KEY_G,
                "category.hardesthide"
        ));
        OPEN_POWERUPS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.hardesthide.open_powerups",
                GLFW.GLFW_KEY_P,
                "category.hardesthide"
        ));

        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath(HardestHideMod.MOD_ID, "minimap"),
                (context, tickCounter) -> MinimapHud.render(context, Minecraft.getInstance(), ClientGameState.INSTANCE));

        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx ->
                WorldBlackoutRenderer.render(ctx, ClientGameState.INSTANCE));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MAP.consumeClick()) client.setScreen(new FullMapScreen());
            while (OPEN_QUESTIONS.consumeClick()) client.setScreen(new QuestionScreen());
            while (OPEN_POWERUPS.consumeClick()) client.setScreen(new PowerupScreen());
        });
    }
}
