package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.List;

public class SoulResonanceHandler {
    private final ConfigManager configManager;
    private int tickCounter;

    public SoulResonanceHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled() || !configManager.isResonanceEnabled()) return;

        tickCounter++;
        if (tickCounter < configManager.getResonanceCheckInterval()) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            processResonance(player, server);
        }
    }

    private void processResonance(ServerPlayerEntity player, MinecraftServer server) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();

        if (dominant == null) {
            data.setResonanceBonus(0);
            return;
        }

        double radius = configManager.getResonanceRadius();
        Box searchBox = player.getBoundingBox().expand(radius);

        List<ServerPlayerEntity> nearbyPlayers = player.getServerWorld().getEntitiesByClass(
                ServerPlayerEntity.class, searchBox,
                other -> other != player && other.isAlive()
        );

        int resonantCount = 0;
        int synergyCount = 0;

        for (ServerPlayerEntity other : nearbyPlayers) {
            AlignmentData otherData = ((AlignmentDataAccessor) other).soulbound$getAlignmentData();
            AlignmentType otherDominant = otherData.getDominant();

            if (otherDominant == null) continue;

            if (otherDominant == dominant) {
                resonantCount++;
            } else if (dominant.getSynergies().contains(otherDominant)) {
                synergyCount++;
            }
        }

        float previousBonus = data.getResonanceBonus();
        float bonus = Math.min(
                resonantCount * configManager.getResonanceBonusPerPlayer()
                        + synergyCount * configManager.getResonanceBonusPerPlayer() * 0.5f,
                configManager.getMaxResonanceBonus()
        );

        data.setResonanceBonus(bonus);
        data.setLastResonanceCheck(player.getServerWorld().getTime());

if (configManager.isSoulWhispersEnabled()) {
            if (previousBonus == 0 && bonus > 0) {
                player.sendMessage(
                        Text.literal("✧ Soul Resonance detected — kindred spirits nearby")
                                .formatted(Formatting.AQUA, Formatting.ITALIC),
                        true
                );
            } else if (previousBonus > 0 && bonus == 0) {
                player.sendMessage(
                        Text.literal("✧ Soul Resonance fades...")
                                .formatted(Formatting.GRAY, Formatting.ITALIC),
                        true
                );
            }
}
    }
}
