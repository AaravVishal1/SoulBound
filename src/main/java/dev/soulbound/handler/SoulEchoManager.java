package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class SoulEchoManager {
    private final ConfigManager configManager;
    private final List<SoulEcho> activeEchoes = Collections.synchronizedList(new ArrayList<>());
    private int tickCounter;

    public SoulEchoManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void createEcho(ServerPlayerEntity player) {
        if (!configManager.isEnabled() || !configManager.isSoulEchoEnabled()) return;

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();
        if (dominant == null) return;

        float strength = Math.min(data.getDominantValue() / configManager.getApexThreshold(), 1.0f);
        if (strength < 0.2f) return;

        long expiry = player.getServerWorld().getTime() + configManager.getSoulEchoDurationTicks();
        float radius = configManager.getSoulEchoRadius();

        SoulEcho echo = new SoulEcho(
                player.getServerWorld().getRegistryKey().getValue().toString(),
                player.getPos(),
                dominant,
                strength,
                expiry,
                radius,
                player.getUuid()
        );

        activeEchoes.add(echo);

data.setSoulEchoActive(true);
        data.setSoulEchoExpiry(expiry);

        if (configManager.isSoulWhispersEnabled()) {
            Box notifyBox = new Box(
                    player.getX() - 32, player.getY() - 16, player.getZ() - 32,
                    player.getX() + 32, player.getY() + 16, player.getZ() + 32
            );
            List<ServerPlayerEntity> nearbyPlayers = player.getServerWorld().getEntitiesByClass(
                    ServerPlayerEntity.class, notifyBox, p -> p != player);
            for (ServerPlayerEntity nearby : nearbyPlayers) {
                nearby.sendMessage(
                        Text.literal("◈ A soul echo resonates nearby... (" + dominant.getDisplayName() + ")")
                                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
                        true
                );
            }
        }
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled() || !configManager.isSoulEchoEnabled()) return;

        tickCounter++;
        if (tickCounter < 40) return;
        tickCounter = 0;

        Iterator<SoulEcho> iterator = activeEchoes.iterator();
        while (iterator.hasNext()) {
            SoulEcho echo = iterator.next();

ServerWorld world = null;
            for (ServerWorld w : server.getWorlds()) {
                if (w.getRegistryKey().getValue().toString().equals(echo.worldId)) {
                    world = w;
                    break;
                }
            }

            if (world == null) {
                iterator.remove();
                continue;
            }

if (world.getTime() >= echo.expiry) {
                iterator.remove();
                continue;
}

applyEchoEffects(world, echo);

if (configManager.isParticlesEnabled()) {
                spawnEchoParticles(world, echo);
}
        }
    }

    private void applyEchoEffects(ServerWorld world, SoulEcho echo) {
        Box echoBox = new Box(
                echo.position.x - echo.radius, echo.position.y - echo.radius, echo.position.z - echo.radius,
                echo.position.x + echo.radius, echo.position.y + echo.radius, echo.position.z + echo.radius
        );

        List<ServerPlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                ServerPlayerEntity.class, echoBox, ServerPlayerEntity::isAlive);

        for (ServerPlayerEntity player : nearbyPlayers) {
            float distance = (float) player.getPos().distanceTo(echo.position);
            float falloff = 1.0f - (distance / echo.radius);
            if (falloff <= 0) continue;

            int duration = (int) (100 * echo.strength * falloff);
            if (duration < 20) continue;

            switch (echo.alignmentType) {
                case DECAY -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 0, true, false));
                }
                case PRECISION -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false));
                }
                case VOLATILITY -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, duration, 0, true, false));
                }
                case VOID -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration, 0, true, false));
                }
                case INSTINCT -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, duration, 0, true, false));
                }
                case ORDER -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, duration, 0, true, false));
                }
                case SAVAGERY -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, 0, true, false));
                }
            }
        }
    }

    private void spawnEchoParticles(ServerWorld world, SoulEcho echo) {
        var particleType = switch (echo.alignmentType) {
            case DECAY -> ParticleTypes.SOUL;
            case PRECISION -> ParticleTypes.CRIT;
            case VOLATILITY -> ParticleTypes.FLAME;
            case VOID -> ParticleTypes.PORTAL;
            case INSTINCT -> ParticleTypes.ENCHANT;
            case ORDER -> ParticleTypes.HEART;
            case SAVAGERY -> ParticleTypes.ANGRY_VILLAGER;
        };

        for (int i = 0; i < 3; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * echo.radius * 2;
            double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * echo.radius * 2;

            world.spawnParticles(particleType,
                    echo.position.x + offsetX,
                    echo.position.y + 1.0 + offsetY,
                    echo.position.z + offsetZ,
                    1, 0, 0.05, 0, 0.02);
        }
    }

    public int getActiveEchoCount() {
        return activeEchoes.size();
    }

    public List<SoulEcho> getActiveEchoes() {
        return Collections.unmodifiableList(activeEchoes);
    }

    public record SoulEcho(
            String worldId,
            Vec3d position,
            AlignmentType alignmentType,
            float strength,
            long expiry,
            float radius,
            UUID ownerUuid
    ) {}
}