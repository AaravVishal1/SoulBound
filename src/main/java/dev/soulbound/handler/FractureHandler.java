package dev.soulbound.handler;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class FractureHandler {
    private final ConfigManager configManager;
    private int tickCounter;
    private final Map<UUID, Integer> fractureEventEscalation = new HashMap<>();
    private final Map<UUID, Long> lastRealityTear = new HashMap<>();

    private static final String[] FRACTURE_WHISPERS = {
            "Your soul splinters at the seams...",
            "The void between alignments grows restless...",
            "Reality wavers around your fractured essence...",
            "Conflicting souls tear at your being...",
            "The world rejects your shattered spirit...",
            "Whispers from all alignments drown your thoughts...",
            "Your soul is a battlefield of opposing forces...",
            "The fracture deepens. Something watches through the cracks..."
    };

    public FractureHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void tick(MinecraftServer server) {
        if (!configManager.isEnabled()) return;

        tickCounter++;
        if (tickCounter < configManager.getFractureEffectInterval()) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            processFracture(player);
        }
    }

    private void processFracture(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        data.recalculateFracture(configManager.getFractureThreshold());

        if (!data.isFractured()) {
            fractureEventEscalation.remove(player.getUuid());
            return;
        }

        float fractureLevel = data.getFractureLevel();
        float severity = configManager.getFractureSeverityMultiplier();

if (fractureLevel > 1.0f) {
            applyMinorFractureEffects(player, data, fractureLevel, severity);
        }

if (fractureLevel > 20.0f) {
            applyModerateFractureEffects(player, data, fractureLevel, severity);
        }

if (fractureLevel > 50.0f) {
            applySevereFractureEffects(player, data, fractureLevel, severity);
        }

if (fractureLevel > 80.0f) {
            applyCriticalFractureEffects(player, data, fractureLevel, severity);
        }

int escalation = fractureEventEscalation.getOrDefault(player.getUuid(), 0);
        fractureEventEscalation.put(player.getUuid(), Math.min(escalation + 1, 20));
    }

    private void applyMinorFractureEffects(ServerPlayerEntity player, AlignmentData data,
                                            float fractureLevel, float severity) {
        if (configManager.isSoulWhispersEnabled() && player.getRandom().nextFloat() < 0.15f) {
            String whisper = FRACTURE_WHISPERS[player.getRandom().nextInt(FRACTURE_WHISPERS.length)];
            player.sendMessage(
                    Text.literal("☠ " + whisper).formatted(Formatting.DARK_RED, Formatting.ITALIC),
                    true
            );
        }

if (player.getRandom().nextFloat() < 0.2f * severity) {
            int duration = (int) (100 * (fractureLevel / 20.0f) * severity);
            applyRandomMinorDebuff(player, duration);
        }
    }

    private void applyModerateFractureEffects(ServerPlayerEntity player, AlignmentData data,
                                               float fractureLevel, float severity) {
        long timeOfDay = player.getServerWorld().getTimeOfDay() % 24000;
        boolean isNight = timeOfDay >= 13000 && timeOfDay <= 23000;

float nightMultiplier = isNight ? 1.5f : 1.0f;
        float strength = Math.min(fractureLevel / 50.0f, 1.0f) * severity * nightMultiplier;
        int duration = (int) (200 * strength);

        if (duration < 40) return;

        if (player.getRandom().nextFloat() < 0.4f) {
            applyRandomDebuff(player, duration);
        }

increasePhantomSensitivity(player);

if (isNight && player.getRandom().nextFloat() < 0.3f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0, true, false));
        }
    }

    private void applySevereFractureEffects(ServerPlayerEntity player, AlignmentData data,
                                             float fractureLevel, float severity) {
        float strength = Math.min(fractureLevel / 80.0f, 1.0f) * severity;

int duration = (int) (300 * strength);
        applyRandomDebuff(player, duration);

if (configManager.isFractureRealityTearEnabled()) {
            UUID uuid = player.getUuid();
            long now = player.getServerWorld().getTime();
            Long lastTear = lastRealityTear.get(uuid);

            if ((lastTear == null || (now - lastTear) > 1200) && player.getRandom().nextFloat() < 0.15f) {
                triggerRealityTear(player, data, strength);
                lastRealityTear.put(uuid, now);
            }
        }

if (player.getRandom().nextFloat() < 0.1f * severity) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 0, true, false));
        }
    }

    private void applyCriticalFractureEffects(ServerPlayerEntity player, AlignmentData data,
                                               float fractureLevel, float severity) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 300, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0, true, false));

if (player.getRandom().nextFloat() < 0.25f * severity) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 0, true, false));
        }

player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 1, true, false));

if (configManager.isSoulWhispersEnabled() && player.getRandom().nextFloat() < 0.05f) {
            player.sendMessage(
                    Text.literal("⚠ YOUR SOUL IS FRACTURING BEYOND REPAIR ⚠")
                            .formatted(Formatting.DARK_RED, Formatting.BOLD),
                    false
            );
        }
    }

    private void triggerRealityTear(ServerPlayerEntity player, AlignmentData data, float strength) {
        ServerWorld world = player.getServerWorld();
        int escalation = fractureEventEscalation.getOrDefault(player.getUuid(), 0);

int mobCount = 1 + (escalation / 5);
        mobCount = Math.min(mobCount, 4);

        for (int i = 0; i < mobCount; i++) {
            double offsetX = (player.getRandom().nextDouble() - 0.5) * 10.0;
            double offsetZ = (player.getRandom().nextDouble() - 0.5) * 10.0;
            double spawnX = player.getX() + offsetX;
            double spawnZ = player.getZ() + offsetZ;
            int spawnY = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, (int) spawnX, (int) spawnZ);

            BlockPos spawnPos = new BlockPos((int) spawnX, spawnY, (int) spawnZ);
            if (!world.isAir(spawnPos)) continue;

EntityType<?> mobType = getRandomFractureMob(player.getRandom(), escalation);
            var entity = mobType.create(world);
            if (entity != null) {
                entity.setPosition(spawnX, spawnY, spawnZ);
                world.spawnEntity(entity);
            }
        }

        if (configManager.isSoulWhispersEnabled()) {
            player.sendMessage(
                    Text.literal("⚡ Reality tears open around you!")
                            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD),
                    true
            );
        }
    }

    private EntityType<?> getRandomFractureMob(net.minecraft.util.math.random.Random random, int escalation) {
        if (escalation >= 15) {
            return switch (random.nextInt(4)) {
                case 0 -> EntityType.WITHER_SKELETON;
                case 1 -> EntityType.BLAZE;
                case 2 -> EntityType.ENDERMAN;
                default -> EntityType.WITCH;
            };
        } else if (escalation >= 8) {
            return switch (random.nextInt(4)) {
                case 0 -> EntityType.SKELETON;
                case 1 -> EntityType.SPIDER;
                case 2 -> EntityType.CREEPER;
                default -> EntityType.ZOMBIE;
            };
        } else {
            return switch (random.nextInt(3)) {
                case 0 -> EntityType.ZOMBIE;
                case 1 -> EntityType.SKELETON;
                default -> EntityType.SILVERFISH;
            };
        }
    }

    private void applyRandomMinorDebuff(ServerPlayerEntity player, int duration) {
        int roll = player.getRandom().nextInt(3);
        StatusEffectInstance effect = switch (roll) {
            case 0 -> new StatusEffectInstance(StatusEffects.HUNGER, duration, 0, true, false);
            case 1 -> new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false);
            default -> new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, 0, true, false);
        };
        player.addStatusEffect(effect);
    }

    private void applyRandomDebuff(ServerPlayerEntity player, int duration) {
        int roll = player.getRandom().nextInt(6);
        StatusEffectInstance effect = switch (roll) {
            case 0 -> new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 0, true, false);
            case 1 -> new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, 0, true, false);
            case 2 -> new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false);
            case 3 -> new StatusEffectInstance(StatusEffects.HUNGER, duration, 1, true, false);
            case 4 -> new StatusEffectInstance(StatusEffects.DARKNESS, duration, 0, true, false);
            default -> new StatusEffectInstance(StatusEffects.NAUSEA, Math.min(duration, 100), 0, true, false);
        };
        player.addStatusEffect(effect);
    }

    private void increasePhantomSensitivity(ServerPlayerEntity player) {
        int timeSinceRest = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        if (timeSinceRest > 24000 && timeSinceRest < 72000) {
            player.getStatHandler().setStat(player, Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST),
                    timeSinceRest + 200);
        }
    }

    public void onPlayerDisconnect(UUID uuid) {
        fractureEventEscalation.remove(uuid);
        lastRealityTear.remove(uuid);
    }

    public static boolean isFractured(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        return data.isFractured();
    }

    public static float getFractureLevel(ServerPlayerEntity player) {
        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        return data.getFractureLevel();
    }
}
