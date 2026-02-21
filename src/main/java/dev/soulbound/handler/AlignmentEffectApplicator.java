package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class AlignmentEffectApplicator {
    private static final UUID SPEED_UUID = UUID.fromString("d4f5a6b7-1c2d-3e4f-a5b6-c7d8e9f0a1b2");
    private static final UUID ARMOR_UUID = UUID.fromString("e5f6b7c8-2d3e-4f5a-b6c7-d8e9f0a1b2c3");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("f6a7c8d9-3e4f-5a6b-c7d8-e9f0a1b2c3d4");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("a7b8d9e0-4f5a-6b7c-d8e9-f0a1b2c3d4e5");
    private static final UUID MAX_HEALTH_UUID = UUID.fromString("b8c9e0f1-5a6b-7c8d-e9f0-a1b2c3d4e5f6");
    private static final UUID KNOCKBACK_RESIST_UUID = UUID.fromString("c9d0f1a2-6b7c-8d9e-f0a1-b2c3d4e5f6a7");

    private static final String SPEED_NAME = "soulbound.passive.speed";
    private static final String ARMOR_NAME = "soulbound.passive.armor";
    private static final String ATTACK_SPEED_NAME = "soulbound.passive.attack_speed";
    private static final String ATTACK_DAMAGE_NAME = "soulbound.passive.attack_damage";
    private static final String MAX_HEALTH_NAME = "soulbound.passive.max_health";
    private static final String KNOCKBACK_NAME = "soulbound.passive.knockback_resist";

    public static void applyPassiveEffects(ServerPlayerEntity player, ConfigManager configManager) {
        if (!configManager.isEnabled()) return;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) {
            removeAllModifiers(player);
            return;
        }

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        float multiplier = configManager.getEffectStrengthMultiplier();
        AlignmentType.Tier tier = data.getDominantTier();
        float synergyBonus = data.getSynergyBonus();
        float conflictPenalty = data.getConflictPenalty();

        float effectiveStrength = strength * multiplier * (1.0f + synergyBonus) * (1.0f - conflictPenalty);

        removeAllModifiers(player);

        switch (dominant) {
            case DECAY -> applyDecayEffects(player, effectiveStrength, tier);
            case PRECISION -> applyPrecisionEffects(player, effectiveStrength, tier);
            case VOLATILITY -> applyVolatilityEffects(player, effectiveStrength, tier);
            case VOID -> applyVoidEffects(player, effectiveStrength, tier);
            case INSTINCT -> applyInstinctEffects(player, effectiveStrength, tier);
            case ORDER -> applyOrderEffects(player, effectiveStrength, tier);
            case SAVAGERY -> applySavageryEffects(player, effectiveStrength, tier);
        }
    }

    private static void applyDecayEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ARMOR, ARMOR_UUID, ARMOR_NAME,
                    1.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, KNOCKBACK_RESIST_UUID, KNOCKBACK_NAME,
                    0.15 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH_UUID, MAX_HEALTH_NAME,
                    2.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 140, 0, true, false));
        }

        if (tier == AlignmentType.Tier.TRANSCENDENT) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 140, 1, true, false));
        }
    }

    private static void applyPrecisionEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_UUID, SPEED_NAME,
                    0.01 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED, ATTACK_SPEED_UUID, ATTACK_SPEED_NAME,
                    0.3 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME,
                    1.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            if (player.isUsingItem()) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 140, 0, true, false));
            }
        }
    }

    private static void applyVolatilityEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 140, 0, true, false));
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ARMOR, ARMOR_UUID, ARMOR_NAME,
                    1.5 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME,
                    1.5 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0, true, false));
        }
    }

    private static void applyVoidEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            double speedBonus = tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal() ? 0.025 : 0.015;
            addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_UUID, SPEED_NAME,
                    speedBonus * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 140, 0, true, false));
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            if (player.getServerWorld().getRegistryKey() == net.minecraft.world.World.END) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, true, false));
            }
        }

        if (tier == AlignmentType.Tier.TRANSCENDENT) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 400, 0, true, false));
        }
    }

    private static void applyInstinctEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        long timeOfDay = player.getServerWorld().getTimeOfDay() % 24000;
        boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal() && isNight) {
            addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_UUID, SPEED_NAME,
                    0.02 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal() && isNight) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, true, false));
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal() && isNight) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME,
                    1.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal() && isNight) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 140, 1, true, false));
        }
    }

    private static void applyOrderEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 140, 0, true, false));
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH_UUID, MAX_HEALTH_NAME,
                    2.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 140, 0, true, false));
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ARMOR, ARMOR_UUID, ARMOR_NAME,
                    3.0 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier == AlignmentType.Tier.TRANSCENDENT) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 140, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 140, 0, true, false));
        }
    }

    private static void applySavageryEffects(ServerPlayerEntity player, float strength, AlignmentType.Tier tier) {
        if (tier.ordinal() >= AlignmentType.Tier.FLEDGLING.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, ATTACK_DAMAGE_NAME,
                    0.5 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.ATTUNED.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED, ATTACK_SPEED_UUID, ATTACK_SPEED_NAME,
                    0.2 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.RESONANT.ordinal()) {
            addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_UUID, SPEED_NAME,
                    0.015 * strength, EntityAttributeModifier.Operation.ADDITION);
        }

        if (tier.ordinal() >= AlignmentType.Tier.APEX.ordinal()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 1, true, false));
        }

        if (tier == AlignmentType.Tier.TRANSCENDENT) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 140, 1, true, false));
        }
    }

    private static void addModifier(ServerPlayerEntity player,
                                     net.minecraft.entity.attribute.EntityAttribute attribute,
                                     UUID uuid, String name, double amount,
                                     EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null) return;

        EntityAttributeModifier existing = instance.getModifier(uuid);
        if (existing != null) {
            instance.removeModifier(uuid);
        }
        instance.addTemporaryModifier(new EntityAttributeModifier(uuid, name, amount, operation));
    }

    private static void removeAllModifiers(ServerPlayerEntity player) {
        removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_UUID);
        removeModifier(player, EntityAttributes.GENERIC_ARMOR, ARMOR_UUID);
        removeModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED, ATTACK_SPEED_UUID);
        removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH_UUID);
        removeModifier(player, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, KNOCKBACK_RESIST_UUID);
    }

    private static void removeModifier(ServerPlayerEntity player,
                                        net.minecraft.entity.attribute.EntityAttribute attribute,
                                        UUID uuid) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }
}
