package dev.soulbound.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.soulbound.SoulboundMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private SoulboundConfig config;

    public void load() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                config = GSON.fromJson(json, SoulboundConfig.class);
                SoulboundMod.LOGGER.info("Configuration loaded from {}", configPath);
            } catch (IOException e) {
                SoulboundMod.LOGGER.error("Failed to load config, using defaults", e);
                config = new SoulboundConfig();
            }
        } else {
            config = new SoulboundConfig();
            save();
            SoulboundMod.LOGGER.info("Default configuration created at {}", configPath);
        }
    }

    public void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(config));
        } catch (IOException e) {
            SoulboundMod.LOGGER.error("Failed to save config", e);
        }
    }

    private Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("soulbound.json");
    }

    public SoulboundConfig getConfig() { return config; }
    public boolean isEnabled() { return config.globalEnabled; }
    public float getAlignmentGainRate() { return config.alignmentGainRate; }
    public float getAlignmentDecayRate() { return config.alignmentDecayRate; }
    public float getApexThreshold() { return config.apexThreshold; }
    public float getTranscendentThreshold() { return config.transcendentThreshold; }
    public float getFractureThreshold() { return config.fractureThreshold; }
    public float getEffectStrengthMultiplier() { return config.effectStrengthMultiplier; }
    public float getGrinderPenaltyFactor() { return config.grinderPenaltyFactor; }
    public float getHardcoreMultiplier() { return config.hardcoreMultiplier; }
    public float getBossAlignmentMultiplier() { return config.bossAlignmentMultiplier; }
    public float getEliteAlignmentMultiplier() { return config.eliteAlignmentMultiplier; }
    public int getGrinderCooldownTicks() { return config.grinderCooldownTicks; }
    public int getMaxKillsPerTypePerWindow() { return config.maxKillsPerTypePerWindow; }
    public int getKillWindowTicks() { return config.killWindowTicks; }
    public double getSpatialGrinderRadius() { return config.spatialGrinderRadius; }
    public int getSpatialKillThreshold() { return config.spatialKillThreshold; }
    public float getSpatialPenaltyFactor() { return config.spatialPenaltyFactor; }
    public int getDecayIntervalTicks() { return config.decayIntervalTicks; }
    public float getFractureDecayRate() { return config.fractureDecayRate; }
    public boolean isMomentumEnabled() { return config.momentumEnabled; }
    public float getMaxMomentumMultiplier() { return config.maxMomentumMultiplier; }
    public float getMomentumGainPerKill() { return config.momentumGainPerKill; }
    public int getMomentumDecayPerCycle() { return config.momentumDecayPerCycle; }
    public boolean isOpposingDecayEnabled() { return config.opposingDecayEnabled; }
    public float getOpposingDecayFactor() { return config.opposingDecayFactor; }
    public boolean isDimensionalBonusEnabled() { return config.dimensionalBonusEnabled; }
    public float getDimensionalOverworldBonus() { return config.dimensionalOverworldBonus; }
    public float getDimensionalNetherBonus() { return config.dimensionalNetherBonus; }
    public float getDimensionalEndBonus() { return config.dimensionalEndBonus; }
    public boolean isResonanceEnabled() { return config.resonanceEnabled; }
    public double getResonanceRadius() { return config.resonanceRadius; }
    public float getResonanceBonusPerPlayer() { return config.resonanceBonusPerPlayer; }
    public float getMaxResonanceBonus() { return config.maxResonanceBonus; }
    public int getResonanceCheckInterval() { return config.resonanceCheckInterval; }
    public boolean isSoulEchoEnabled() { return config.soulEchoEnabled; }
    public int getSoulEchoDurationTicks() { return config.soulEchoDurationTicks; }
    public float getSoulEchoRadius() { return config.soulEchoRadius; }
    public float getFractureSeverityMultiplier() { return config.fractureSeverityMultiplier; }
    public boolean isFractureRealityTearEnabled() { return config.fractureRealityTearEnabled; }
    public int getFractureEffectInterval() { return config.fractureEffectInterval; }
    public boolean isParticlesEnabled() { return config.particlesEnabled; }
    public int getParticleInterval() { return config.particleInterval; }
    public boolean isSoulWhispersEnabled() { return config.soulWhispersEnabled; }
    public int getWhisperCooldownTicks() { return config.whisperCooldownTicks; }
    public boolean isPvpAlignmentModifiersEnabled() { return config.pvpAlignmentModifiersEnabled; }
    public float getOpposingPvpDamageBonus() { return config.opposingPvpDamageBonus; }
    public float getSynergyPvpDamageReduction() { return config.synergyPvpDamageReduction; }
    public boolean isCommandEnabled() { return config.commandEnabled; }
    public boolean isSecondaryAlignmentEnabled() { return config.secondaryAlignmentEnabled; }
    public float getSecondaryAlignmentRatio() { return config.secondaryAlignmentRatio; }
    public boolean isPassiveMobFearEnabled() { return config.passiveMobFearEnabled; }
    public float getPassiveMobFearRadius() { return config.passiveMobFearRadius; }
}
