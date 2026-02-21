package dev.soulbound.handler;

import dev.soulbound.config.ConfigManager;
import dev.soulbound.manager.AlignmentManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class AlignmentDecayHandler {
    private static final int DECAY_CHECK_INTERVAL = 1200;
    private final ConfigManager configManager;
    private final AlignmentManager alignmentManager;
    private int tickCounter;

    public AlignmentDecayHandler(ConfigManager configManager, AlignmentManager alignmentManager) {
        this.configManager = configManager;
        this.alignmentManager = alignmentManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled()) return;

        tickCounter++;
        if (tickCounter < DECAY_CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            alignmentManager.decayAlignments(player);
        }
    }
}
