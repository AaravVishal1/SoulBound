package dev.soulbound.handler;

import dev.soulbound.SoulboundMod;
import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEventHandler {
    private static final int PASSIVE_EFFECT_INTERVAL = 100;
    private static final int VOID_TELEPORT_COOLDOWN = 600;
    private static final int WHISPER_CHECK_INTERVAL = 2400;

    private final Map<UUID, Long> voidTeleportCooldowns = new HashMap<>();
    private final Map<UUID, Long> lastWhisper = new HashMap<>();
    private int passiveTickCounter;
    private int whisperTickCounter;

    private static final String[] ALIGNMENT_WHISPERS_DECAY = {
            "The soil hungers beneath your feet...",
            "You feel the pulse of undeath in your veins.",
            "Shadows gather where you walk."
    };
    private static final String[] ALIGNMENT_WHISPERS_PRECISION = {
            "Your aim steadies. Every breath counts.",
            "You see through the darkness with unnatural clarity.",
            "Each arrow is a conversation with death."
    };
    private static final String[] ALIGNMENT_WHISPERS_VOLATILITY = {
            "Fire is not your enemy. It is your instrument.",
            "You feel the pressure building... waiting...",
            "The heat no longer burns. It answers to you."
    };
    private static final String[] ALIGNMENT_WHISPERS_VOID = {
            "Space bends around your presence...",
            "The void whispers coordinates to nowhere.",
            "Reality is thinner here, because of you."
    };
    private static final String[] ALIGNMENT_WHISPERS_INSTINCT = {
            "The night is your hunting ground now.",
            "You feel the web of life tremble.",
            "Instinct replaces thought. Trust it."
    };
    private static final String[] ALIGNMENT_WHISPERS_ORDER = {
            "You bring peace where you walk.",
            "The villagers sleep soundly tonight, because of you.",
            "Civilization's torch burns brighter near your soul."
    };
    private static final String[] ALIGNMENT_WHISPERS_SAVAGERY = {
            "Your blood sings for battle.",
            "The line between hunter and beast blurs...",
            "Strength is the only truth."
    };

    public void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::onEntityDamaged);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ConfigManager config = SoulboundMod.getConfigManager();

            passiveTickCounter++;
            if (passiveTickCounter >= PASSIVE_EFFECT_INTERVAL) {
                passiveTickCounter = 0;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    AlignmentEffectApplicator.applyPassiveEffects(player, config);
                }
            }

            whisperTickCounter++;
            if (whisperTickCounter >= WHISPER_CHECK_INTERVAL && config.isSoulWhispersEnabled()) {
                whisperTickCounter = 0;
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    sendRandomWhisper(player);
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.getPlayer().getUuid();
            voidTeleportCooldowns.remove(uuid);
            lastWhisper.remove(uuid);

            SoulboundMod.getApexStateHandler().onPlayerDisconnect(uuid);
            SoulboundMod.getFractureHandler().onPlayerDisconnect(uuid);
            SoulboundMod.getDimensionalHandler().onPlayerDisconnect(uuid);
        });
    }

    private boolean onEntityDamaged(LivingEntity entity, DamageSource source, float amount) {
        ConfigManager config = SoulboundMod.getConfigManager();
        if (!config.isEnabled()) return true;

if (entity instanceof ServerPlayerEntity victim && source.getAttacker() instanceof ServerPlayerEntity attacker) {
            return handlePvPCombat(victim, attacker, amount, config);
}

if (entity instanceof ServerPlayerEntity player) {
            AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
            AlignmentType dominant = data.getDominant();
            if (dominant == AlignmentType.VOID) {
                return handleVoidDodge(player, data, amount);
            }

if (dominant == AlignmentType.SAVAGERY && data.isApex()) {
                float healthRatio = player.getHealth() / player.getMaxHealth();
                if (healthRatio < 0.3f && player.getRandom().nextFloat() < 0.2f) {
                    player.heal(2.0f);
                }
}
}

if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
            float modifier = SoulboundMod.getMobPerceptionHandler().getCombatDamageModifier(attacker, entity);
            if (modifier != 1.0f) {
            }
}

        return true;
    }

    private boolean handlePvPCombat(ServerPlayerEntity victim, ServerPlayerEntity attacker,
                                     float amount, ConfigManager config) {
        if (!config.isPvpAlignmentModifiersEnabled()) return true;

        AlignmentData attackerData = ((AlignmentDataAccessor) attacker).soulbound$getAlignmentData();
        AlignmentData victimData = ((AlignmentDataAccessor) victim).soulbound$getAlignmentData();

        AlignmentType attackerAlignment = attackerData.getDominant();
        AlignmentType victimAlignment = victimData.getDominant();

        if (attackerAlignment == null || victimAlignment == null) return true;

if (attackerAlignment.getOpposing().contains(victimAlignment)) {
            if (config.isSoulWhispersEnabled()) {
                attacker.sendMessage(
                        Text.literal("⚔ Opposing soul detected — damage amplified!")
                                .formatted(Formatting.RED, Formatting.ITALIC),
                        true
                );
            }
}

if (attackerAlignment.getSynergies().contains(victimAlignment)) {
            if (config.isSoulWhispersEnabled()) {
                attacker.sendMessage(
                        Text.literal("✧ Kindred soul — violence feels... wrong")
                                .formatted(Formatting.AQUA, Formatting.ITALIC),
                        true
                );
            }
}

        return true;
                                     }

    private boolean handleVoidDodge(ServerPlayerEntity player, AlignmentData data, float amount) {
        float strength = Math.min(data.getDominantValue() / SoulboundMod.getConfigManager().getApexThreshold(), 1.0f);
        if (strength < 0.35f) return true;

        UUID uuid = player.getUuid();
        long currentTime = player.getServerWorld().getTime();
        Long lastTeleport = voidTeleportCooldowns.get(uuid);
        if (lastTeleport != null && (currentTime - lastTeleport) < VOID_TELEPORT_COOLDOWN) {
            return true;
        }

        float dodgeChance = 0.15f * strength * SoulboundMod.getConfigManager().getEffectStrengthMultiplier();
        AlignmentType.Tier tier = data.getDominantTier();
        dodgeChance += tier.ordinal() * 0.02f;

        if (player.getRandom().nextFloat() > dodgeChance) return true;

        ServerWorld world = player.getServerWorld();
        for (int attempt = 0; attempt < 5; attempt++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 8.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 8.0;
            double targetX = player.getX() + offsetX;
            double targetZ = player.getZ() + offsetZ;
            int targetY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) targetX, (int) targetZ);

            if (targetY > world.getBottomY()) {
                player.teleport(targetX, targetY, targetZ);
                voidTeleportCooldowns.put(uuid, currentTime);

                if (SoulboundMod.getConfigManager().isSoulWhispersEnabled()) {
                    player.sendMessage(
                            Text.literal("≋ Void Blink ≋").formatted(Formatting.DARK_PURPLE),
                            true
                    );
                }
                break;
            }
        }

        return true;
    }

    private void sendRandomWhisper(ServerPlayerEntity player) {
        ConfigManager config = SoulboundMod.getConfigManager();
        if (!config.isSoulWhispersEnabled()) return;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        float strength = Math.min(data.getDominantValue() / config.getApexThreshold(), 1.0f);
        if (strength < 0.3f) return;

if (player.getRandom().nextFloat() > 0.3f * strength) return;

        UUID uuid = player.getUuid();
        long now = player.getServerWorld().getTime();
        Long last = lastWhisper.get(uuid);
        if (last != null && (now - last) < config.getWhisperCooldownTicks()) return;
        lastWhisper.put(uuid, now);

        String[] whispers = getWhispersForType(dominant);
        if (whispers.length == 0) return;

        String whisper = whispers[player.getRandom().nextInt(whispers.length)];
        player.sendMessage(
                Text.literal("§o" + whisper)
                        .formatted(dominant.getFormatting(), Formatting.ITALIC),
                true
        );
    }

    private String[] getWhispersForType(AlignmentType type) {
        return switch (type) {
            case DECAY -> ALIGNMENT_WHISPERS_DECAY;
            case PRECISION -> ALIGNMENT_WHISPERS_PRECISION;
            case VOLATILITY -> ALIGNMENT_WHISPERS_VOLATILITY;
            case VOID -> ALIGNMENT_WHISPERS_VOID;
            case INSTINCT -> ALIGNMENT_WHISPERS_INSTINCT;
            case ORDER -> ALIGNMENT_WHISPERS_ORDER;
            case SAVAGERY -> ALIGNMENT_WHISPERS_SAVAGERY;
        };
    }
}
