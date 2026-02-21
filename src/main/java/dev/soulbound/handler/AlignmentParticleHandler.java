package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class AlignmentParticleHandler {
    private final ConfigManager configManager;
    private int tickCounter;

    public AlignmentParticleHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled() || !configManager.isParticlesEnabled()) return;

        tickCounter++;
        if (tickCounter < configManager.getParticleInterval()) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            spawnAlignmentParticles(player);
        }
    }

    private void spawnAlignmentParticles(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        ServerWorld world = player.getServerWorld();
        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);

if (strength < 0.2f) return;

if (data.isFractured()) {
            spawnFractureParticles(world, player, data);
            return;
        }

if (data.isTranscendent()) {
            spawnTranscendentParticles(world, player, dominant);
            return;
        }

if (data.isApex()) {
            spawnApexParticles(world, player, dominant);
            return;
        }

if (strength > 0.4f) {
            spawnAmbientParticles(world, player, dominant, strength);
        }
    }

    private void spawnAmbientParticles(ServerWorld world, ServerPlayerEntity player,
                                        AlignmentType type, float strength) {
        ParticleEffect particle = getAlignmentParticle(type);
        int count = (int) (1 + strength * 2);

        world.spawnParticles(particle,
                player.getX(), player.getY() + 0.5, player.getZ(),
                count,
                0.3, 0.5, 0.3,
                0.01);
    }

    private void spawnApexParticles(ServerWorld world, ServerPlayerEntity player, AlignmentType type) {
        ParticleEffect primary = getAlignmentParticle(type);
        ParticleEffect secondary = getApexSecondaryParticle(type);

world.spawnParticles(primary,
                player.getX(), player.getY() + 1.0, player.getZ(),
                5,
                0.5, 0.8, 0.5,
                0.02);

if (secondary != null) {
            world.spawnParticles(secondary,
                    player.getX(), player.getY() + 0.2, player.getZ(),
                    2,
                    0.3, 0.3, 0.3,
                    0.01);
        }
    }

    private void spawnTranscendentParticles(ServerWorld world, ServerPlayerEntity player, AlignmentType type) {
        ParticleEffect primary = getAlignmentParticle(type);

double time = world.getTime() * 0.1;
        for (int i = 0; i < 4; i++) {
            double angle = time + (i * Math.PI / 2.0);
            double offsetX = Math.cos(angle) * 1.5;
            double offsetZ = Math.sin(angle) * 1.5;
            double offsetY = 0.5 + Math.sin(time * 2) * 0.3;

            world.spawnParticles(primary,
                    player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                    1, 0.05, 0.05, 0.05, 0.0);
        }

world.spawnParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 2.0, player.getZ(),
                2, 0.1, 0.5, 0.1, 0.01);

world.spawnParticles(ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 0.1, player.getZ(),
                3, 0.5, 0.1, 0.5, 0.5);
    }

    private void spawnFractureParticles(ServerWorld world, ServerPlayerEntity player, AlignmentData data) {
        float fractureLevel = data.getFractureLevel();
        int intensity = (int) (fractureLevel / 20.0f) + 1;

world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(), player.getY() + 0.5, player.getZ(),
                intensity,
                0.4, 0.6, 0.4,
                0.02);

if (fractureLevel > 40.0f) {
            world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 0.3, player.getZ(),
                    1, 0.3, 0.3, 0.3, 0.01);
        }

if (fractureLevel > 70.0f) {
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.01);
        }
    }

public void spawnTierUpBurst(ServerPlayerEntity player, AlignmentType type) {
        if (!configManager.isParticlesEnabled()) return;
        ServerWorld world = player.getServerWorld();
        ParticleEffect particle = getAlignmentParticle(type);

for (int i = 0; i < 16; i++) {
            double angle = (i / 16.0) * Math.PI * 2;
            double offsetX = Math.cos(angle) * 2.0;
            double offsetZ = Math.sin(angle) * 2.0;

            world.spawnParticles(particle,
                    player.getX() + offsetX, player.getY() + 1.0, player.getZ() + offsetZ,
                    3, 0.1, 0.3, 0.1, 0.05);
        }

world.spawnParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1.0, player.getZ(),
                15, 0.2, 1.5, 0.2, 0.1);

world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                20, 0.5, 1.0, 0.5, 0.3);
    }

    private ParticleEffect getAlignmentParticle(AlignmentType type) {
        return switch (type) {
            case DECAY -> ParticleTypes.SOUL;
            case PRECISION -> ParticleTypes.CRIT;
            case VOLATILITY -> ParticleTypes.FLAME;
            case VOID -> ParticleTypes.PORTAL;
            case INSTINCT -> ParticleTypes.ENCHANT;
            case ORDER -> ParticleTypes.COMPOSTER;
            case SAVAGERY -> ParticleTypes.ANGRY_VILLAGER;
        };
    }

    private ParticleEffect getApexSecondaryParticle(AlignmentType type) {
        return switch (type) {
            case DECAY -> ParticleTypes.SOUL_FIRE_FLAME;
            case PRECISION -> ParticleTypes.ENCHANTED_HIT;
            case VOLATILITY -> ParticleTypes.LAVA;
            case VOID -> ParticleTypes.REVERSE_PORTAL;
            case INSTINCT -> ParticleTypes.GLOW;
            case ORDER -> ParticleTypes.WAX_ON;
            case SAVAGERY -> ParticleTypes.SWEEP_ATTACK;
        };
    }
}
