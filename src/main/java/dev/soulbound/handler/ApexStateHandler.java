package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.*;

public class ApexStateHandler {
    private static final int CHECK_INTERVAL = 100;
    private static final UUID APEX_SPEED_UUID = UUID.fromString("a3f2d8c1-7b4e-4f9a-b6c3-8e5d1f0a2b4c");
    private static final UUID APEX_ARMOR_UUID = UUID.fromString("b4e3c9d2-8c5f-4a0b-c7d4-9f6e2a1b3c5d");
    private static final UUID APEX_DAMAGE_UUID = UUID.fromString("d5f4b0e3-9d6a-4b1c-e8f5-0a7b3c2d4e6f");
    private static final String APEX_SPEED_NAME = "soulbound.apex.speed";
    private static final String APEX_ARMOR_NAME = "soulbound.apex.armor";
    private static final String APEX_DAMAGE_NAME = "soulbound.apex.damage";

    private final ConfigManager configManager;
    private int tickCounter;
    private final Map<UUID, Long> abilityCooldowns = new HashMap<>();
    private final Set<UUID> previouslyApex = new HashSet<>();
    private final Set<UUID> previouslyTranscendent = new HashSet<>();

    public ApexStateHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled()) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            processApexState(player);
        }
    }

    private void processApexState(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        float threshold = configManager.getApexThreshold();
        boolean wasApex = data.isApex();
        UUID uuid = player.getUuid();

        if (data.getDominant() != null && data.getDominantValue() >= threshold) {
            data.setApex(true);

            if (!previouslyApex.contains(uuid)) {
                previouslyApex.add(uuid);
                notifyApexReached(player, data);
            }

if (data.isTranscendent() && !previouslyTranscendent.contains(uuid)) {
                previouslyTranscendent.add(uuid);
                notifyTranscendentReached(player, data);
}

            applyApexEffects(player, data);
        } else {
            data.setApex(false);
            if (wasApex) {
                removeApexEffects(player);
                previouslyApex.remove(uuid);
                previouslyTranscendent.remove(uuid);
            }
        }
    }

    private void notifyApexReached(ServerPlayerEntity player, AlignmentData data) {
        if (!configManager.isSoulWhispersEnabled()) return;
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        player.sendMessage(
                Text.literal("☀ APEX STATE ACHIEVED ☀ ")
                        .formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal("Your " + dominant.getDisplayName() + " soul burns with power.")
                                .formatted(dominant.getFormatting())),
                false
        );
    }

    private void notifyTranscendentReached(ServerPlayerEntity player, AlignmentData data) {
        if (!configManager.isSoulWhispersEnabled()) return;
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        player.sendMessage(
                Text.literal("✦ TRANSCENDENCE ✦ ")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
                        .append(Text.literal("You have transcended mortal bounds. " + dominant.getLoreText())
                                .formatted(Formatting.DARK_PURPLE)),
                false
        );
    }

    private void applyApexEffects(ServerPlayerEntity player, AlignmentData data) {
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        float multiplier = configManager.getEffectStrengthMultiplier();
        boolean isTranscendent = data.isTranscendent();

        switch (dominant) {
            case DECAY -> applyDecayApex(player, multiplier, isTranscendent);
            case PRECISION -> applyPrecisionApex(player, multiplier, isTranscendent);
            case VOLATILITY -> applyVolatilityApex(player, multiplier, isTranscendent);
            case VOID -> applyVoidApex(player, multiplier, isTranscendent, data);
            case INSTINCT -> applyInstinctApex(player, multiplier, isTranscendent);
            case ORDER -> applyOrderApex(player, multiplier, isTranscendent, player.getServerWorld());
            case SAVAGERY -> applySavageryApex(player, multiplier, isTranscendent);
        }
    }

    private void applyDecayApex(ServerPlayerEntity player, float multiplier, boolean transcendent) {
        addArmorModifier(player, 4.0 * multiplier);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 0, true, false));

        if (transcendent) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 300, 2, true, false));
        }
    }

    private void applyPrecisionApex(ServerPlayerEntity player, float multiplier, boolean transcendent) {
        addSpeedModifier(player, 0.04 * multiplier);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, true, false));

        if (transcendent) {
            addDamageModifier(player, 2.0 * multiplier);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300, 0, true, false));
        }
    }

    private void applyVolatilityApex(ServerPlayerEntity player, float multiplier, boolean transcendent) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 300, 0, true, false));
        addDamageModifier(player, 1.5 * multiplier);

        if (transcendent) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 1, true, false));
        }
    }

    private void applyVoidApex(ServerPlayerEntity player, float multiplier, boolean transcendent, AlignmentData data) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 300, 0, true, false));
        addSpeedModifier(player, 0.03 * multiplier);

        UUID uuid = player.getUuid();
        long now = player.getServerWorld().getTime();
        Long lastAbility = abilityCooldowns.get(uuid);

if (player.getHealth() < player.getMaxHealth() * 0.3f) {
            if (lastAbility == null || (now - lastAbility) > 400) {
                performVoidBlink(player);
                abilityCooldowns.put(uuid, now);
            }
}

        if (transcendent) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 300, 0, true, false));
        }
    }

    private void performVoidBlink(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        for (int attempt = 0; attempt < 5; attempt++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 16.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 16.0;
            double targetX = player.getX() + offsetX;
            double targetZ = player.getZ() + offsetZ;
            int targetY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, (int) targetX, (int) targetZ);

            if (targetY > world.getBottomY()) {
                player.teleport(targetX, targetY, targetZ);
                break;
            }
        }
    }

    private void applyInstinctApex(ServerPlayerEntity player, float multiplier, boolean transcendent) {
        long timeOfDay = player.getServerWorld().getTimeOfDay() % 24000;
        boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

        if (isNight) {
            addSpeedModifier(player, 0.06 * multiplier);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 300, 1, true, false));
        } else {
            addSpeedModifier(player, 0.02 * multiplier);
        }

        if (transcendent) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 300, 2, true, false));
            if (isNight) {
                addDamageModifier(player, 2.0 * multiplier);
            }
        }
    }

    private void applyOrderApex(ServerPlayerEntity player, float multiplier, boolean transcendent, ServerWorld world) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 300, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 0, true, false));
        addArmorModifier(player, 3.0 * multiplier);

        if (transcendent) {
            Box aura = player.getBoundingBox().expand(12.0);
            List<ServerPlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                    ServerPlayerEntity.class, aura, p -> p != player);
            for (ServerPlayerEntity ally : nearbyPlayers) {
                ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 0, true, false));
                ally.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 120, 0, true, false));
            }
        }
    }

    private void applySavageryApex(ServerPlayerEntity player, float multiplier, boolean transcendent) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, 1, true, false));
        addSpeedModifier(player, 0.03 * multiplier);
        addDamageModifier(player, 1.0 * multiplier);

        if (transcendent) {
            float healthRatio = player.getHealth() / player.getMaxHealth();
            if (healthRatio < 0.5f) {
                int amplifier = healthRatio < 0.25f ? 3 : 2;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 300, amplifier, true, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 2, true, false));
            }
        }
    }

    private void addSpeedModifier(ServerPlayerEntity player, double amount) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attr == null) return;
        attr.removeModifier(APEX_SPEED_UUID);
        attr.addTemporaryModifier(new EntityAttributeModifier(
                APEX_SPEED_UUID, APEX_SPEED_NAME, amount, EntityAttributeModifier.Operation.ADDITION));
    }

    private void addArmorModifier(ServerPlayerEntity player, double amount) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (attr == null) return;
        attr.removeModifier(APEX_ARMOR_UUID);
        attr.addTemporaryModifier(new EntityAttributeModifier(
                APEX_ARMOR_UUID, APEX_ARMOR_NAME, amount, EntityAttributeModifier.Operation.ADDITION));
    }

    private void addDamageModifier(ServerPlayerEntity player, double amount) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attr == null) return;
        attr.removeModifier(APEX_DAMAGE_UUID);
        attr.addTemporaryModifier(new EntityAttributeModifier(
                APEX_DAMAGE_UUID, APEX_DAMAGE_NAME, amount, EntityAttributeModifier.Operation.ADDITION));
    }

    private void removeApexEffects(ServerPlayerEntity player) {
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(APEX_SPEED_UUID);

        EntityAttributeInstance armorAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttr != null) armorAttr.removeModifier(APEX_ARMOR_UUID);

        EntityAttributeInstance damageAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (damageAttr != null) damageAttr.removeModifier(APEX_DAMAGE_UUID);
    }

    public void onPlayerDisconnect(UUID uuid) {
        abilityCooldowns.remove(uuid);
        previouslyApex.remove(uuid);
        previouslyTranscendent.remove(uuid);
    }

    public static boolean isApex(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        return data.isApex();
    }

    public static AlignmentType getApexType(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        return data.isApex() ? data.getDominant() : null;
    }
}
