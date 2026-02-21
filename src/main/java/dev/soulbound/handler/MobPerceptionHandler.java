package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import dev.soulbound.manager.AlignmentManager;
import dev.soulbound.registry.AlignmentRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class MobPerceptionHandler {
    private final ConfigManager configManager;
    private final AlignmentManager alignmentManager;

    public MobPerceptionHandler(ConfigManager configManager, AlignmentManager alignmentManager) {
        this.configManager = configManager;
        this.alignmentManager = alignmentManager;
    }

    public float getAggroRadiusMultiplier(MobEntity mob, ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return 1.0f;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return 1.0f;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float multiplier = configManager.getEffectStrengthMultiplier();
        float synergyBonus = data.getSynergyBonus();
        float conflictPenalty = data.getConflictPenalty();

        if (data.isFractured()) {
            float fractureSeverity = Math.min(data.getFractureLevel() / 50.0f, 1.0f);
            return 1.0f + (0.2f * fractureSeverity * multiplier);
        }

        float base = switch (dominant) {
            case ORDER -> 1.0f - (0.15f * strength * multiplier);
            case DECAY -> 1.0f - (0.08f * strength * multiplier);
            case SAVAGERY -> 1.0f + (0.1f * strength * multiplier);
            case PRECISION -> 1.0f + (0.05f * strength * multiplier);
            case INSTINCT -> 1.0f - (0.05f * strength * multiplier);
            case VOID -> 1.0f - (0.12f * strength * multiplier);
            case VOLATILITY -> 1.0f + (0.03f * strength * multiplier);
        };

        base -= synergyBonus * 0.05f;
        base += conflictPenalty * 0.08f;
        return Math.max(0.3f, base);
    }

    public int getAttackDelayTicks(MobEntity mob, ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return 0;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return 0;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float multiplier = configManager.getEffectStrengthMultiplier();
        AlignmentType.Tier tier = data.getDominantTier();
        int tierBonus = tier.ordinal() * 5;

        return switch (dominant) {
            case DECAY -> isZombieType(mob) ? (int) (40 * strength * multiplier) + tierBonus : 0;
            case SAVAGERY -> isIllagerType(mob) ? (int) (30 * strength * multiplier) + tierBonus : 0;
            case INSTINCT -> isArthropod(mob) ? (int) (25 * strength * multiplier) + tierBonus : 0;
            case VOLATILITY -> isExplosive(mob) ? (int) (35 * strength * multiplier) + tierBonus : 0;
            default -> 0;
        };
    }

    public boolean shouldAvoidTarget(MobEntity mob, ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return false;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return false;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        AlignmentType.Tier tier = data.getDominantTier();

float tierMultiplier = 1.0f + (tier.ordinal() * 0.15f);

        if (dominant == AlignmentType.PRECISION && isSkeletonType(mob)) {
            return strength > 0.4f && mob.getRandom().nextFloat() < 0.35f * strength * tierMultiplier;
        }

        if (dominant == AlignmentType.DECAY && isZombieType(mob)) {
            return strength > 0.5f && mob.getRandom().nextFloat() < 0.25f * strength * tierMultiplier;
        }

        if (dominant == AlignmentType.INSTINCT && isArthropod(mob)) {
            return strength > 0.5f && mob.getRandom().nextFloat() < 0.2f * strength * tierMultiplier;
        }

        if (dominant == AlignmentType.VOLATILITY && isExplosive(mob)) {
            return strength > 0.6f && mob.getRandom().nextFloat() < 0.2f * strength * tierMultiplier;
        }

if (data.isApex()) {
            AlignmentType mobAlignment = AlignmentRegistry.getAlignmentForEntity(mob);
            if (mobAlignment == dominant) {
                return mob.getRandom().nextFloat() < 0.4f * tierMultiplier;
            }
}

        return false;
    }

    public boolean shouldPassiveMobFlee(LivingEntity mob, ServerPlayerEntity player) {
        if (!configManager.isEnabled() || !configManager.isPassiveMobFearEnabled()) return false;
        if (!AlignmentRegistry.isPassiveFearTarget(mob)) return false;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return false;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);

if (dominant == AlignmentType.SAVAGERY) {
            return strength > 0.3f;
}
        if (dominant == AlignmentType.DECAY) {
            return strength > 0.5f;
        }

if (data.isFractured() && data.getFractureLevel() > 20.0f) {
            return true;
}

        return false;
    }

    public boolean shouldCreeperDelay(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return false;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant != AlignmentType.VOLATILITY) return false;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        return strength > 0.25f;
    }

    public int getCreeperFuseExtension(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return 0;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        if (data.getDominant() != AlignmentType.VOLATILITY) return 0;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float multiplier = configManager.getEffectStrengthMultiplier();
        int tierBonus = data.getDominantTier().ordinal() * 3;
        return (int) (15 * strength * multiplier) + tierBonus;
    }

    public boolean shouldEndermanBeNeutral(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return false;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant != AlignmentType.VOID) return false;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        return strength > 0.25f;
    }

    public boolean shouldSpiderAvoidDuringDay(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return false;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant != AlignmentType.INSTINCT) return false;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        return strength > 0.25f;
    }

    public float getTradeModifier(ServerPlayerEntity player) {
        if (!configManager.isEnabled()) return 0.0f;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return 0.0f;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float multiplier = configManager.getEffectStrengthMultiplier();
        float tierBonus = data.getDominantTier().ordinal() * 0.02f;

        return switch (dominant) {
            case ORDER -> -(0.1f * strength * multiplier + tierBonus);
            case DECAY -> 0.12f * strength * multiplier + tierBonus;
            case SAVAGERY -> 0.15f * strength * multiplier + tierBonus;
            case VOID -> -(0.05f * strength * multiplier);
            default -> 0.0f;
        };
    }

    public float getCombatDamageModifier(ServerPlayerEntity attacker, LivingEntity target) {
        if (!configManager.isEnabled()) return 1.0f;

        AlignmentData data = ((AlignmentDataAccessor) attacker).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return 1.0f;

        AlignmentType targetAlignment = AlignmentRegistry.getAlignmentForEntity(target);
        if (targetAlignment == null) return 1.0f;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float modifier = 1.0f;

if (dominant.getOpposing().contains(targetAlignment)) {
            modifier += 0.15f * strength;
}

if (dominant.getSynergies().contains(targetAlignment)) {
            modifier -= 0.1f * strength;
}

if (data.isTranscendent()) {
            modifier += 0.1f;
}

        return Math.max(0.5f, modifier);
    }

    private boolean isZombieType(MobEntity mob) {
        return mob instanceof net.minecraft.entity.mob.ZombieEntity;
    }

    private boolean isSkeletonType(MobEntity mob) {
        return mob instanceof net.minecraft.entity.mob.AbstractSkeletonEntity;
    }

    private boolean isIllagerType(MobEntity mob) {
        return mob instanceof net.minecraft.entity.mob.IllagerEntity
                || mob instanceof net.minecraft.entity.mob.RavagerEntity;
    }

    private boolean isArthropod(MobEntity mob) {
        return mob instanceof net.minecraft.entity.mob.SpiderEntity
                || mob instanceof net.minecraft.entity.mob.SilverfishEntity;
    }

    private boolean isExplosive(MobEntity mob) {
        return mob instanceof net.minecraft.entity.mob.CreeperEntity
                || mob instanceof net.minecraft.entity.mob.GhastEntity;
    }
}