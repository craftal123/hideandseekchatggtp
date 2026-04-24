package com.example.hardesthide;

import com.example.hardesthide.config.HHSConfig;
import com.example.hardesthide.game.HHSCommands;
import com.example.hardesthide.game.HHSGameManager;
import com.example.hardesthide.net.HHSNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardestHideMod implements ModInitializer {
    public static final String MOD_ID = "hardesthide";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final HHSConfig CONFIG = HHSConfig.defaults();
    public static final HHSGameManager GAME = new HHSGameManager(CONFIG);

    @Override
    public void onInitialize() {
        HHSNetworking.registerServer();
        HHSCommands.register();
        ServerTickEvents.END_SERVER_TICK.register(server -> GAME.tick(server));
        LOGGER.info("Hardest Hide and Seek loaded.");
    }
}
