package dev.soulbound.registry;

import dev.soulbound.alignment.AlignmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.mob.*;

import java.util.*;

public class AlignmentRegistry {
    private static final Map<EntityType<?>, AlignmentType> ENTITY_ALIGNMENT_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Float> ENTITY_WEIGHT_MAP = new HashMap<>();
    private static final Map<EntityType<?>, Integer> ENTITY_TIER_REQ = new HashMap<>();
    private static final Set<EntityType<?>> ELITE_MOBS = new HashSet<>();
    private static final Set<EntityType<?>> PASSIVE_FEAR_TARGETS = new HashSet<>();
    private static final Map<EntityType<?>, AlignmentType> SECONDARY_ALIGNMENT_MAP = new HashMap<>();

    public static void initialize() {
        registerDecayMobs();
        registerPrecisionMobs();
        registerVolatilityMobs();
        registerVoidMobs();
        registerInstinctMobs();
        registerOrderMobs();
        registerSavageryMobs();
        registerPassiveMobFear();
        registerEliteMobs();
    }

    private static void registerDecayMobs() {
        register(EntityType.ZOMBIE, AlignmentType.DECAY, 1.0f);
        register(EntityType.ZOMBIE_VILLAGER, AlignmentType.DECAY, 1.2f);
        register(EntityType.HUSK, AlignmentType.DECAY, 1.4f);
        register(EntityType.DROWNED, AlignmentType.DECAY, 1.4f);
        register(EntityType.ZOMBIFIED_PIGLIN, AlignmentType.DECAY, 1.6f);
        register(EntityType.ZOGLIN, AlignmentType.DECAY, 2.0f);
        register(EntityType.PHANTOM, AlignmentType.DECAY, 1.8f);
        registerSecondary(EntityType.PHANTOM, AlignmentType.VOID);
    }

    private static void registerPrecisionMobs() {
        register(EntityType.SKELETON, AlignmentType.PRECISION, 1.0f);
        register(EntityType.STRAY, AlignmentType.PRECISION, 1.4f);
        register(EntityType.WITHER_SKELETON, AlignmentType.PRECISION, 2.0f);
        registerSecondary(EntityType.WITHER_SKELETON, AlignmentType.DECAY);
    }

    private static void registerVolatilityMobs() {
        register(EntityType.CREEPER, AlignmentType.VOLATILITY, 1.5f);
        register(EntityType.GHAST, AlignmentType.VOLATILITY, 2.2f);
        register(EntityType.BLAZE, AlignmentType.VOLATILITY, 1.8f);
        register(EntityType.MAGMA_CUBE, AlignmentType.VOLATILITY, 1.2f);
        registerSecondary(EntityType.GHAST, AlignmentType.VOID);
    }

    private static void registerVoidMobs() {
        register(EntityType.ENDERMAN, AlignmentType.VOID, 1.5f);
        register(EntityType.ENDERMITE, AlignmentType.VOID, 0.8f);
        register(EntityType.SHULKER, AlignmentType.VOID, 2.0f);
        registerSecondary(EntityType.SHULKER, AlignmentType.PRECISION);
    }

    private static void registerInstinctMobs() {
        register(EntityType.SPIDER, AlignmentType.INSTINCT, 1.0f);
        register(EntityType.CAVE_SPIDER, AlignmentType.INSTINCT, 1.4f);
        register(EntityType.SILVERFISH, AlignmentType.INSTINCT, 0.8f);
        register(EntityType.BEE, AlignmentType.INSTINCT, 0.6f);
        register(EntityType.WOLF, AlignmentType.INSTINCT, 0.8f);
        register(EntityType.GUARDIAN, AlignmentType.INSTINCT, 1.6f);
        register(EntityType.ELDER_GUARDIAN, AlignmentType.INSTINCT, 2.5f);
        registerSecondary(EntityType.CAVE_SPIDER, AlignmentType.DECAY);
    }

    private static void registerOrderMobs() {
        register(EntityType.VILLAGER, AlignmentType.ORDER, 2.0f);
        register(EntityType.IRON_GOLEM, AlignmentType.ORDER, 2.5f);
        register(EntityType.SNOW_GOLEM, AlignmentType.ORDER, 1.0f);
        register(EntityType.WANDERING_TRADER, AlignmentType.ORDER, 1.5f);
    }

    private static void registerSavageryMobs() {
        register(EntityType.PILLAGER, AlignmentType.SAVAGERY, 1.5f);
        register(EntityType.VINDICATOR, AlignmentType.SAVAGERY, 1.8f);
        register(EntityType.EVOKER, AlignmentType.SAVAGERY, 2.2f);
        register(EntityType.RAVAGER, AlignmentType.SAVAGERY, 2.8f);
        register(EntityType.WITCH, AlignmentType.SAVAGERY, 1.5f);
        register(EntityType.VEX, AlignmentType.SAVAGERY, 1.0f);
        register(EntityType.ILLUSIONER, AlignmentType.SAVAGERY, 2.0f);
        register(EntityType.HOGLIN, AlignmentType.SAVAGERY, 1.6f);
        register(EntityType.PIGLIN, AlignmentType.SAVAGERY, 1.3f);
        register(EntityType.PIGLIN_BRUTE, AlignmentType.SAVAGERY, 2.2f);
        registerSecondary(EntityType.EVOKER, AlignmentType.VOID);
        registerSecondary(EntityType.RAVAGER, AlignmentType.INSTINCT);
    }

    private static void registerPassiveMobFear() {
        PASSIVE_FEAR_TARGETS.add(EntityType.COW);
        PASSIVE_FEAR_TARGETS.add(EntityType.SHEEP);
        PASSIVE_FEAR_TARGETS.add(EntityType.PIG);
        PASSIVE_FEAR_TARGETS.add(EntityType.CHICKEN);
        PASSIVE_FEAR_TARGETS.add(EntityType.RABBIT);
        PASSIVE_FEAR_TARGETS.add(EntityType.HORSE);
        PASSIVE_FEAR_TARGETS.add(EntityType.DONKEY);
        PASSIVE_FEAR_TARGETS.add(EntityType.LLAMA);
        PASSIVE_FEAR_TARGETS.add(EntityType.CAT);
        PASSIVE_FEAR_TARGETS.add(EntityType.FOX);
    }

    private static void registerEliteMobs() {
        ELITE_MOBS.add(EntityType.WITHER_SKELETON);
        ELITE_MOBS.add(EntityType.ELDER_GUARDIAN);
        ELITE_MOBS.add(EntityType.EVOKER);
        ELITE_MOBS.add(EntityType.RAVAGER);
        ELITE_MOBS.add(EntityType.PIGLIN_BRUTE);
        ELITE_MOBS.add(EntityType.ZOGLIN);
    }

    private static void register(EntityType<?> entityType, AlignmentType alignmentType, float weight) {
        ENTITY_ALIGNMENT_MAP.put(entityType, alignmentType);
        ENTITY_WEIGHT_MAP.put(entityType, weight);
    }

    private static void registerSecondary(EntityType<?> entityType, AlignmentType secondaryType) {
        SECONDARY_ALIGNMENT_MAP.put(entityType, secondaryType);
    }

    public static AlignmentType getAlignmentForEntity(LivingEntity entity) {
        return ENTITY_ALIGNMENT_MAP.get(entity.getType());
    }

    public static AlignmentType getSecondaryAlignment(LivingEntity entity) {
        return SECONDARY_ALIGNMENT_MAP.get(entity.getType());
    }

    public static float getWeightForEntity(LivingEntity entity) {
        Float weight = ENTITY_WEIGHT_MAP.get(entity.getType());
        return weight != null ? weight : 1.0f;
    }

    public static boolean isBoss(LivingEntity entity) {
        return entity instanceof EnderDragonEntity || entity instanceof WitherEntity;
    }

    public static boolean isElite(LivingEntity entity) {
        return ELITE_MOBS.contains(entity.getType());
    }

    public static boolean isPassiveFearTarget(LivingEntity entity) {
        return PASSIVE_FEAR_TARGETS.contains(entity.getType());
    }

    public static float getSecondaryWeight(LivingEntity entity) {
        return getWeightForEntity(entity) * 0.35f;
    }

    public static float getDimensionalBonus(AlignmentType type, net.minecraft.registry.RegistryKey<net.minecraft.world.World> dimension) {
        AlignmentType.DimensionAffinity affinity = type.getDimensionAffinity();
        if (dimension == net.minecraft.world.World.OVERWORLD && affinity == AlignmentType.DimensionAffinity.OVERWORLD) return 1.25f;
        if (dimension == net.minecraft.world.World.NETHER && affinity == AlignmentType.DimensionAffinity.NETHER) return 1.35f;
        if (dimension == net.minecraft.world.World.END && affinity == AlignmentType.DimensionAffinity.END) return 1.5f;
        return 1.0f;
    }
}
