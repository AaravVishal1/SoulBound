package dev.soulbound.manager;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import dev.soulbound.registry.AlignmentRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class AlignmentManager {
    private final ConfigManager configManager;

    public AlignmentManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public AlignmentData getAlignmentData(ServerPlayerEntity player) {
        return ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
    }

    public void onMobKilled(LivingEntity entity, DamageSource source) {
        if (!configManager.isEnabled()) return;
        if (source.getAttacker() == null) return;
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        AlignmentType alignmentType = AlignmentRegistry.getAlignmentForEntity(entity);
        if (alignmentType == null) return;

        AlignmentData data = getAlignmentData(player);
        AlignmentType previousDominant = data.getDominant();
        AlignmentType.Tier previousTier = data.getDominantTier();

        float gainAmount = calculateGain(entity, alignmentType, data, player);
        if (gainAmount <= 0) return;

        data.addAlignment(alignmentType, gainAmount);

        if (configManager.isSecondaryAlignmentEnabled()) {
            AlignmentType secondaryType = AlignmentRegistry.getSecondaryAlignment(entity);
            if (secondaryType != null) {
                float secondaryGain = gainAmount * configManager.getSecondaryAlignmentRatio();
                data.addAlignment(secondaryType, secondaryGain);
            }
        }

        data.recordKillLocation(entity.getX(), entity.getY(), entity.getZ(),
                configManager.getSpatialGrinderRadius());

        data.setLastUpdateTimestamp(player.getServerWorld().getTime());
        data.recalculateFracture(configManager.getFractureThreshold());

        sendAlignmentFeedback(player, data, alignmentType, gainAmount, previousDominant, previousTier);
    }

    private float calculateGain(LivingEntity entity, AlignmentType type, AlignmentData data, ServerPlayerEntity player) {
        float baseGain = AlignmentRegistry.getWeightForEntity(entity);
        float gain = baseGain * configManager.getAlignmentGainRate();

if (AlignmentRegistry.isBoss(entity)) {
            gain *= configManager.getBossAlignmentMultiplier();
        }

if (AlignmentRegistry.isElite(entity)) {
            gain *= configManager.getEliteAlignmentMultiplier();
        }

if (player.getServer().isHardcore()) {
            gain *= configManager.getHardcoreMultiplier();
        }

if (configManager.isDimensionalBonusEnabled()) {
            gain *= AlignmentRegistry.getDimensionalBonus(type, player.getServerWorld().getRegistryKey());
        }

if (configManager.isMomentumEnabled()) {
            gain *= data.getMomentumMultiplier();
        }

if (data.getResonanceBonus() > 0) {
            gain *= (1.0f + data.getResonanceBonus());
        }

if (data.getDominant() != null && data.getDominant().getSynergies().contains(type)) {
            gain *= 1.15f;
        }

String typeKey = type.getId();
        long currentTime = player.getServerWorld().getTime();

        Map<String, Long> timestamps = data.getKillTimestamps();
        Map<String, Integer> counts = data.getKillCounts();

        Long lastKillTime = timestamps.get(typeKey);
        if (lastKillTime != null && (currentTime - lastKillTime) < configManager.getGrinderCooldownTicks()) {
            gain *= configManager.getGrinderPenaltyFactor();
        }

        int windowKills = counts.getOrDefault(typeKey, 0);
        if (lastKillTime != null && (currentTime - lastKillTime) > configManager.getKillWindowTicks()) {
            windowKills = 0;
        }

        if (windowKills >= configManager.getMaxKillsPerTypePerWindow()) {
            gain *= configManager.getGrinderPenaltyFactor();
        }

if (data.getSpatialKillCount() >= configManager.getSpatialKillThreshold()) {
            gain *= configManager.getSpatialPenaltyFactor();
        }

        timestamps.put(typeKey, currentTime);
        counts.put(typeKey, windowKills + 1);

        return gain;
    }

    private void sendAlignmentFeedback(ServerPlayerEntity player, AlignmentData data,
                                        AlignmentType gained, float amount,
                                        AlignmentType previousDominant,
                                        AlignmentType.Tier previousTier) {
        if (!configManager.isSoulWhispersEnabled()) return;

        AlignmentType.Tier currentTier = data.getDominantTier();
        AlignmentType currentDominant = data.getDominant();

if (currentTier != previousTier && currentTier.ordinal() > previousTier.ordinal() && currentDominant != null) {
            MutableText tierMsg = Text.literal("✦ Soul Tier Reached: ")
                    .formatted(Formatting.LIGHT_PURPLE)
                    .append(Text.literal(currentTier.name())
                            .formatted(Formatting.GOLD, Formatting.BOLD));
            player.sendMessage(tierMsg, true);
            return;
        }

if (currentDominant != null && previousDominant != null && currentDominant != previousDominant) {
            MutableText shiftMsg = Text.literal("⚡ Soul Shift: ")
                    .formatted(Formatting.YELLOW)
                    .append(Text.literal(previousDominant.getDisplayName())
                            .formatted(Formatting.GRAY))
                    .append(Text.literal(" → ").formatted(Formatting.WHITE))
                    .append(Text.literal(currentDominant.getDisplayName())
                            .formatted(currentDominant.getFormatting()));
            player.sendMessage(shiftMsg, true);
            return;
        }

if (data.getMomentumStreak() > 0 && data.getMomentumStreak() % 5 == 0) {
            MutableText momentumMsg = Text.literal("🔥 Momentum x" + data.getMomentumStreak())
                    .formatted(Formatting.RED);
            player.sendMessage(momentumMsg, true);
        }
    }

    public void decayAlignments(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return;

        AlignmentData data = getAlignmentData(player);
        long currentTime = player.getServerWorld().getTime();
        long lastUpdate = data.getLastUpdateTimestamp();

        if (lastUpdate > 0 && (currentTime - lastUpdate) >= configManager.getDecayIntervalTicks()) {
            data.decayAll(configManager.getAlignmentDecayRate());
            data.setLastUpdateTimestamp(currentTime);
        }
    }
}
