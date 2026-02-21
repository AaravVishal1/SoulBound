package dev.soulbound.config;

public class SoulboundConfig {
public boolean globalEnabled = true;
    public float alignmentGainRate = 1.0f;
    public float alignmentDecayRate = 0.05f;
    public float apexThreshold = 100.0f;
    public float transcendentThreshold = 150.0f;
    public float fractureThreshold = 10.0f;
    public float effectStrengthMultiplier = 1.0f;
    public float hardcoreMultiplier = 1.5f;
    public float bossAlignmentMultiplier = 3.0f;
    public float eliteAlignmentMultiplier = 1.8f;

public float grinderPenaltyFactor = 0.25f;
    public int grinderCooldownTicks = 100;
    public int maxKillsPerTypePerWindow = 5;
    public int killWindowTicks = 6000;
    public double spatialGrinderRadius = 16.0;
    public int spatialKillThreshold = 10;
    public float spatialPenaltyFactor = 0.15f;

public int decayIntervalTicks = 24000;
    public float fractureDecayRate = 0.1f;

public boolean momentumEnabled = true;
    public float maxMomentumMultiplier = 2.5f;
    public float momentumGainPerKill = 0.1f;
    public int momentumDecayPerCycle = 1;

public boolean opposingDecayEnabled = true;
    public float opposingDecayFactor = 0.3f;

public boolean dimensionalBonusEnabled = true;
    public float dimensionalOverworldBonus = 1.25f;
    public float dimensionalNetherBonus = 1.35f;
    public float dimensionalEndBonus = 1.5f;

public boolean resonanceEnabled = true;
    public double resonanceRadius = 32.0;
    public float resonanceBonusPerPlayer = 0.15f;
    public float maxResonanceBonus = 0.6f;
    public int resonanceCheckInterval = 200;

public boolean soulEchoEnabled = true;
    public int soulEchoDurationTicks = 6000;
    public float soulEchoRadius = 8.0f;

public float fractureSeverityMultiplier = 1.0f;
    public boolean fractureRealityTearEnabled = true;
    public int fractureEffectInterval = 200;

public boolean particlesEnabled = true;
    public int particleInterval = 40;

public boolean soulWhispersEnabled = true;
    public int whisperCooldownTicks = 2400;

public boolean pvpAlignmentModifiersEnabled = true;
    public float opposingPvpDamageBonus = 0.15f;
    public float synergyPvpDamageReduction = 0.1f;

public boolean commandEnabled = true;

public boolean secondaryAlignmentEnabled = true;
    public float secondaryAlignmentRatio = 0.35f;

public boolean passiveMobFearEnabled = true;
    public float passiveMobFearRadius = 8.0f;
}
