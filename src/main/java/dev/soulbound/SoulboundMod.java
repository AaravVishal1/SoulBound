package dev.soulbound;

import dev.soulbound.command.SoulCommand;
import dev.soulbound.config.ConfigManager;
import dev.soulbound.handler.*;
import dev.soulbound.manager.AlignmentManager;
import dev.soulbound.registry.AlignmentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoulboundMod implements ModInitializer {
    public static final String MOD_ID = "soulbound";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ConfigManager configManager;
    private static AlignmentManager alignmentManager;
    private static MobPerceptionHandler mobPerceptionHandler;
    private static ApexStateHandler apexStateHandler;
    private static FractureHandler fractureHandler;
    private static AlignmentDecayHandler decayHandler;
    private static PlayerEventHandler playerEventHandler;
    private static SoulResonanceHandler resonanceHandler;
    private static SoulEchoManager soulEchoManager;
    private static DimensionalHandler dimensionalHandler;
    private static AlignmentParticleHandler particleHandler;

    @Override
    public void onInitialize() {
        LOGGER.info("Soulbound initializing — Soul Alignment system loading");

        configManager = new ConfigManager();
        configManager.load();

        AlignmentRegistry.initialize();

        alignmentManager = new AlignmentManager(configManager);
        apexStateHandler = new ApexStateHandler(configManager);
        fractureHandler = new FractureHandler(configManager);
        mobPerceptionHandler = new MobPerceptionHandler(configManager, alignmentManager);
        decayHandler = new AlignmentDecayHandler(configManager, alignmentManager);
        playerEventHandler = new PlayerEventHandler();
        resonanceHandler = new SoulResonanceHandler(configManager);
        soulEchoManager = new SoulEchoManager(configManager);
        dimensionalHandler = new DimensionalHandler(configManager);
        particleHandler = new AlignmentParticleHandler(configManager);

        registerEvents();
    }

    private void registerEvents() {
        playerEventHandler.register();

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            alignmentManager.onMobKilled(entity, damageSource);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            decayHandler.tick(server);
            resonanceHandler.tick(server);
            soulEchoManager.tick(server);
            dimensionalHandler.tick(server);
            particleHandler.tick(server);
            apexStateHandler.tick(server);
            fractureHandler.tick(server);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            new SoulCommand(configManager).register(dispatcher);
        });
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static AlignmentManager getAlignmentManager() {
        return alignmentManager;
    }

    public static MobPerceptionHandler getMobPerceptionHandler() {
        return mobPerceptionHandler;
    }

    public static ApexStateHandler getApexStateHandler() {
        return apexStateHandler;
    }

    public static FractureHandler getFractureHandler() {
        return fractureHandler;
    }

    public static DimensionalHandler getDimensionalHandler() {
        return dimensionalHandler;
    }

    public static AlignmentParticleHandler getParticleHandler() {
        return particleHandler;
    }

    public static SoulResonanceHandler getResonanceHandler() {
        return resonanceHandler;
    }

    public static SoulEchoManager getSoulEchoManager() {
        return soulEchoManager;
    }
}
