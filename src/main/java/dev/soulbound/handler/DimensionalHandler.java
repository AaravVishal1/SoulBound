package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DimensionalHandler {
    private static final int CHECK_INTERVAL = 200;

    private final ConfigManager configManager;
    private int tickCounter;
    private final Map<UUID, String> lastDimension = new HashMap<>();

    public DimensionalHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled() || !configManager.isDimensionalBonusEnabled()) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            processDimensionalEffects(player);
        }
    }

    private void processDimensionalEffects(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        RegistryKey<World> currentDimension = player.getServerWorld().getRegistryKey();
        String dimId = currentDimension.getValue().toString();

UUID uuid = player.getUuid();
        String prevDim = lastDimension.get(uuid);
        if (prevDim != null && !prevDim.equals(dimId)) {
            onDimensionChanged(player, data, dominant, currentDimension);
        }
        lastDimension.put(uuid, dimId);

AlignmentType.DimensionAffinity affinity = dominant.getDimensionAffinity();
        boolean isHome = isHomeDimension(affinity, currentDimension);
        boolean isHostile = isHostileDimension(affinity, currentDimension);

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);

        if (isHome && strength > 0.3f) {
            applyHomeBonus(player, dominant, strength);
        } else if (isHostile && strength > 0.2f) {
            applyHostilePenalty(player, dominant, strength);
        }
    }

    private void onDimensionChanged(ServerPlayerEntity player, AlignmentData data,
                                     AlignmentType dominant, RegistryKey<World> newDimension) {
        if (!configManager.isSoulWhispersEnabled()) return;

        AlignmentType.DimensionAffinity affinity = dominant.getDimensionAffinity();
        boolean isHome = isHomeDimension(affinity, newDimension);

        if (isHome) {
            player.sendMessage(
                    Text.literal("◆ Your " + dominant.getDisplayName() + " soul resonates with this dimension")
                            .formatted(dominant.getFormatting(), Formatting.ITALIC),
                    true
            );
        } else if (isHostileDimension(affinity, newDimension)) {
            player.sendMessage(
                    Text.literal("◇ Your " + dominant.getDisplayName() + " soul feels weakened here...")
                            .formatted(Formatting.GRAY, Formatting.ITALIC),
                    true
            );
        }
    }

    private void applyHomeBonus(ServerPlayerEntity player, AlignmentType dominant, float strength) {
        int duration = 250;

        switch (dominant) {
            case DECAY, VOLATILITY -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, duration, 0, true, false));
            }
            case VOID -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration, 0, true, false));
                if (strength > 0.5f) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, duration, 0, true, false));
                }
            }
            case ORDER, INSTINCT, PRECISION, SAVAGERY -> {
                if (strength > 0.5f) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, duration, 0, true, false));
                }
            }
        }
    }

    private void applyHostilePenalty(ServerPlayerEntity player, AlignmentType dominant, float strength) {
        if (strength > 0.5f && player.getRandom().nextFloat() < 0.1f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0, true, false));
        }
    }

    private boolean isHomeDimension(AlignmentType.DimensionAffinity affinity, RegistryKey<World> dimension) {
        return switch (affinity) {
            case OVERWORLD -> dimension == World.OVERWORLD;
            case NETHER -> dimension == World.NETHER;
            case END -> dimension == World.END;
        };
    }

    private boolean isHostileDimension(AlignmentType.DimensionAffinity affinity, RegistryKey<World> dimension) {
        return switch (affinity) {
            case OVERWORLD -> dimension == World.END;
            case NETHER -> dimension == World.OVERWORLD;
            case END -> dimension == World.NETHER;
        };
    }

    public void onPlayerDisconnect(UUID uuid) {
        lastDimension.remove(uuid);
    }
}
