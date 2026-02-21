package dev.soulbound.alignment;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class AlignmentData {
    private final Map<AlignmentType, Float> alignments;
    private AlignmentType dominant;
    private AlignmentType secondary;
    private float fractureLevel;
    private long lastUpdateTimestamp;
    private boolean isApex;
    private boolean isFractured;
    private final Map<String, Long> killTimestamps;
    private final Map<String, Integer> killCounts;

    private int momentumStreak;
    private AlignmentType momentumType;
    private float momentumMultiplier;

    private int totalKills;
    private final Map<AlignmentType, Integer> killsByAlignment;

    private final List<AlignmentShift> shiftHistory;
    private static final int MAX_HISTORY = 20;

    private double lastKillX;
    private double lastKillY;
    private double lastKillZ;
    private int spatialKillCount;

    private long lastResonanceCheck;
    private float resonanceBonus;

    private boolean soulEchoActive;
    private long soulEchoExpiry;

    private AlignmentType previousDominant;
    private long dominantChangedAt;

    public AlignmentData() {
        this.alignments = new EnumMap<>(AlignmentType.class);
        for (AlignmentType type : AlignmentType.values()) {
            alignments.put(type, 0.0f);
        }
        this.dominant = null;
        this.secondary = null;
        this.fractureLevel = 0.0f;
        this.lastUpdateTimestamp = 0L;
        this.isApex = false;
        this.isFractured = false;
        this.killTimestamps = new HashMap<>();
        this.killCounts = new HashMap<>();
        this.momentumStreak = 0;
        this.momentumType = null;
        this.momentumMultiplier = 1.0f;
        this.totalKills = 0;
        this.killsByAlignment = new EnumMap<>(AlignmentType.class);
        this.shiftHistory = new ArrayList<>();
        this.lastKillX = 0;
        this.lastKillY = 0;
        this.lastKillZ = 0;
        this.spatialKillCount = 0;
        this.lastResonanceCheck = 0;
        this.resonanceBonus = 0;
        this.soulEchoActive = false;
        this.soulEchoExpiry = 0;
        this.previousDominant = null;
        this.dominantChangedAt = 0;
    }

    public float getAlignment(AlignmentType type) {
        return alignments.getOrDefault(type, 0.0f);
    }

    public Map<AlignmentType, Float> getAllAlignments() {
        return Collections.unmodifiableMap(alignments);
    }

    public void addAlignment(AlignmentType type, float amount) {
        float current = alignments.getOrDefault(type, 0.0f);
        float newValue = Math.max(0, current + amount);
        alignments.put(type, newValue);

        applyOpposingDecay(type, amount * 0.3f);

        if (type == momentumType) {
            momentumStreak++;
            momentumMultiplier = Math.min(2.5f, 1.0f + (momentumStreak * 0.1f));
        } else {
            momentumStreak = 1;
            momentumType = type;
            momentumMultiplier = 1.0f;
        }

        killsByAlignment.merge(type, 1, Integer::sum);
        totalKills++;

        addShift(type, amount);
        recalculateDominance();
    }

    private void applyOpposingDecay(AlignmentType gained, float amount) {
        for (AlignmentType opposing : gained.getOpposing()) {
            float current = alignments.getOrDefault(opposing, 0.0f);
            if (current > 0) {
                alignments.put(opposing, Math.max(0, current - amount));
            }
        }
    }

    private void addShift(AlignmentType type, float amount) {
        shiftHistory.add(new AlignmentShift(type, amount, System.currentTimeMillis()));
        while (shiftHistory.size() > MAX_HISTORY) {
            shiftHistory.remove(0);
        }
    }

    public void setAlignment(AlignmentType type, float value) {
        alignments.put(type, Math.max(0, value));
        recalculateDominance();
    }

    public void decayAll(float decayRate) {
        for (AlignmentType type : AlignmentType.values()) {
            float current = alignments.getOrDefault(type, 0.0f);
            if (current > 0) {
                float decay = decayRate * (1.0f + (current / 200.0f));
                alignments.put(type, Math.max(0, current - decay));
            }
        }
        if (momentumStreak > 0) {
            momentumStreak = Math.max(0, momentumStreak - 1);
            momentumMultiplier = Math.max(1.0f, momentumMultiplier - 0.05f);
        }
        recalculateDominance();
    }

    public void recalculateDominance() {
        List<Map.Entry<AlignmentType, Float>> sorted = new ArrayList<>(alignments.entrySet());
        sorted.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        if (sorted.isEmpty() || sorted.get(0).getValue() <= 0) {
            if (dominant != null) {
                previousDominant = dominant;
                dominantChangedAt = System.currentTimeMillis();
            }
            dominant = null;
            secondary = null;
            return;
        }

        AlignmentType newDominant = sorted.get(0).getKey();
        if (dominant != null && dominant != newDominant) {
            previousDominant = dominant;
            dominantChangedAt = System.currentTimeMillis();
        }
        dominant = newDominant;

        if (sorted.size() > 1 && sorted.get(1).getValue() > 0) {
            secondary = sorted.get(1).getKey();
        } else {
            secondary = null;
        }
    }

    public void recalculateFracture(float threshold) {
        List<Float> topValues = new ArrayList<>();
        for (AlignmentType type : AlignmentType.values()) {
            float val = alignments.getOrDefault(type, 0.0f);
            if (val > 0) {
                topValues.add(val);
            }
        }
        topValues.sort(Collections.reverseOrder());

        if (topValues.size() >= 3) {
            float first = topValues.get(0);
            float third = topValues.get(2);

            if (first > 0 && (first - third) <= threshold) {
                float fractureGain = 0.5f + (topValues.size() - 3) * 0.1f;
                fractureLevel = Math.min(fractureLevel + fractureGain, 100.0f);
                isFractured = true;
            } else {
                float fractureDecay = 0.25f + (first - third) * 0.01f;
                fractureLevel = Math.max(0, fractureLevel - fractureDecay);
                isFractured = fractureLevel > 0;
            }
        } else {
            fractureLevel = Math.max(0, fractureLevel - 0.5f);
            isFractured = fractureLevel > 0;
        }
    }

    public AlignmentType.Tier getDominantTier() {
        if (dominant == null) return AlignmentType.Tier.DORMANT;
        return AlignmentType.Tier.fromValue(getDominantValue());
    }

    public float getAlignmentStrength() {
        if (dominant == null) return 0;
        float total = 0;
        for (float val : alignments.values()) {
            total += val;
        }
        if (total == 0) return 0;
        return getDominantValue() / total;
    }

    public float getSynergyBonus() {
        if (dominant == null || secondary == null) return 0;
        if (dominant.getSynergies().contains(secondary)) {
            return Math.min(getAlignment(secondary) / 100.0f, 0.5f);
        }
        return 0;
    }

    public float getConflictPenalty() {
        if (dominant == null || secondary == null) return 0;
        if (dominant.getOpposing().contains(secondary)) {
            return Math.min(getAlignment(secondary) / 150.0f, 0.3f);
        }
        return 0;
    }

    public boolean hasDominantShiftedRecently(long windowMs) {
        return previousDominant != null
                && (System.currentTimeMillis() - dominantChangedAt) < windowMs;
    }

    public void recordKillLocation(double x, double y, double z, double spatialRadius) {
        double dx = x - lastKillX;
        double dy = y - lastKillY;
        double dz = z - lastKillZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq < spatialRadius * spatialRadius) {
            spatialKillCount++;
        } else {
            spatialKillCount = 1;
        }

        lastKillX = x;
        lastKillY = y;
        lastKillZ = z;
    }

    public int getSpatialKillCount() {
        return spatialKillCount;
    }

    public AlignmentType getDominant() {
        return dominant;
    }

    public AlignmentType getSecondary() {
        return secondary;
    }

    public AlignmentType getPreviousDominant() {
        return previousDominant;
    }

    public float getFractureLevel() {
        return fractureLevel;
    }

    public void setFractureLevel(float level) {
        this.fractureLevel = Math.max(0, level);
        this.isFractured = this.fractureLevel > 0;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long timestamp) {
        this.lastUpdateTimestamp = timestamp;
    }

    public boolean isApex() {
        return isApex;
    }

    public void setApex(boolean apex) {
        this.isApex = apex;
    }

    public boolean isFractured() {
        return isFractured;
    }

    public Map<String, Long> getKillTimestamps() {
        return killTimestamps;
    }

    public Map<String, Integer> getKillCounts() {
        return killCounts;
    }

    public float getDominantValue() {
        if (dominant == null) return 0;
        return alignments.getOrDefault(dominant, 0.0f);
    }

    public int getMomentumStreak() {
        return momentumStreak;
    }

    public float getMomentumMultiplier() {
        return momentumMultiplier;
    }

    public AlignmentType getMomentumType() {
        return momentumType;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public Map<AlignmentType, Integer> getKillsByAlignment() {
        return Collections.unmodifiableMap(killsByAlignment);
    }

    public List<AlignmentShift> getShiftHistory() {
        return Collections.unmodifiableList(shiftHistory);
    }

    public float getResonanceBonus() {
        return resonanceBonus;
    }

    public void setResonanceBonus(float bonus) {
        this.resonanceBonus = bonus;
    }

    public long getLastResonanceCheck() {
        return lastResonanceCheck;
    }

    public void setLastResonanceCheck(long time) {
        this.lastResonanceCheck = time;
    }

    public boolean isSoulEchoActive() {
        return soulEchoActive;
    }

    public void setSoulEchoActive(boolean active) {
        this.soulEchoActive = active;
    }

    public long getSoulEchoExpiry() {
        return soulEchoExpiry;
    }

    public void setSoulEchoExpiry(long expiry) {
        this.soulEchoExpiry = expiry;
    }

    public boolean isTranscendent() {
        return getDominantTier() == AlignmentType.Tier.TRANSCENDENT;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        NbtCompound alignNbt = new NbtCompound();
        for (Map.Entry<AlignmentType, Float> entry : alignments.entrySet()) {
            alignNbt.putFloat(entry.getKey().getId(), entry.getValue());
        }
        nbt.put("Alignments", alignNbt);

        if (dominant != null) {
            nbt.putString("Dominant", dominant.getId());
        }
        if (secondary != null) {
            nbt.putString("Secondary", secondary.getId());
        }
        if (previousDominant != null) {
            nbt.putString("PreviousDominant", previousDominant.getId());
        }
        nbt.putFloat("FractureLevel", fractureLevel);
        nbt.putLong("LastUpdate", lastUpdateTimestamp);
        nbt.putBoolean("IsApex", isApex);
        nbt.putBoolean("IsFractured", isFractured);

        NbtCompound killTimestampNbt = new NbtCompound();
        for (Map.Entry<String, Long> entry : killTimestamps.entrySet()) {
            killTimestampNbt.putLong(entry.getKey(), entry.getValue());
        }
        nbt.put("KillTimestamps", killTimestampNbt);

        NbtCompound killCountNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : killCounts.entrySet()) {
            killCountNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("KillCounts", killCountNbt);

        nbt.putInt("MomentumStreak", momentumStreak);
        if (momentumType != null) {
            nbt.putString("MomentumType", momentumType.getId());
        }
        nbt.putFloat("MomentumMultiplier", momentumMultiplier);
        nbt.putInt("TotalKills", totalKills);

        NbtCompound killsByAlignNbt = new NbtCompound();
        for (Map.Entry<AlignmentType, Integer> entry : killsByAlignment.entrySet()) {
            killsByAlignNbt.putInt(entry.getKey().getId(), entry.getValue());
        }
        nbt.put("KillsByAlignment", killsByAlignNbt);

        nbt.putDouble("LastKillX", lastKillX);
        nbt.putDouble("LastKillY", lastKillY);
        nbt.putDouble("LastKillZ", lastKillZ);
        nbt.putInt("SpatialKillCount", spatialKillCount);

        nbt.putFloat("ResonanceBonus", resonanceBonus);
        nbt.putLong("LastResonanceCheck", lastResonanceCheck);
        nbt.putBoolean("SoulEchoActive", soulEchoActive);
        nbt.putLong("SoulEchoExpiry", soulEchoExpiry);
        nbt.putLong("DominantChangedAt", dominantChangedAt);

        NbtList historyList = new NbtList();
        for (AlignmentShift shift : shiftHistory) {
            NbtCompound shiftNbt = new NbtCompound();
            shiftNbt.putString("Type", shift.type().getId());
            shiftNbt.putFloat("Amount", shift.amount());
            shiftNbt.putLong("Time", shift.timestamp());
            historyList.add(shiftNbt);
        }
        nbt.put("ShiftHistory", historyList);

        return nbt;
    }

    public static AlignmentData fromNbt(NbtCompound nbt) {
        AlignmentData data = new AlignmentData();

        if (nbt.contains("Alignments")) {
            NbtCompound alignNbt = nbt.getCompound("Alignments");
            for (AlignmentType type : AlignmentType.values()) {
                if (alignNbt.contains(type.getId())) {
                    data.alignments.put(type, alignNbt.getFloat(type.getId()));
                }
            }
        }

        if (nbt.contains("Dominant")) {
            data.dominant = AlignmentType.fromId(nbt.getString("Dominant"));
        }
        if (nbt.contains("Secondary")) {
            data.secondary = AlignmentType.fromId(nbt.getString("Secondary"));
        }
        if (nbt.contains("PreviousDominant")) {
            data.previousDominant = AlignmentType.fromId(nbt.getString("PreviousDominant"));
        }
        data.fractureLevel = nbt.getFloat("FractureLevel");
        data.lastUpdateTimestamp = nbt.getLong("LastUpdate");
        data.isApex = nbt.getBoolean("IsApex");
        data.isFractured = nbt.getBoolean("IsFractured");

        if (nbt.contains("KillTimestamps")) {
            NbtCompound killTimestampNbt = nbt.getCompound("KillTimestamps");
            for (String key : killTimestampNbt.getKeys()) {
                data.killTimestamps.put(key, killTimestampNbt.getLong(key));
            }
        }

        if (nbt.contains("KillCounts")) {
            NbtCompound killCountNbt = nbt.getCompound("KillCounts");
            for (String key : killCountNbt.getKeys()) {
                data.killCounts.put(key, killCountNbt.getInt(key));
            }
        }

        data.momentumStreak = nbt.getInt("MomentumStreak");
        if (nbt.contains("MomentumType")) {
            data.momentumType = AlignmentType.fromId(nbt.getString("MomentumType"));
        }
        data.momentumMultiplier = nbt.contains("MomentumMultiplier") ? nbt.getFloat("MomentumMultiplier") : 1.0f;
        data.totalKills = nbt.getInt("TotalKills");

        if (nbt.contains("KillsByAlignment")) {
            NbtCompound killsByAlignNbt = nbt.getCompound("KillsByAlignment");
            for (AlignmentType type : AlignmentType.values()) {
                if (killsByAlignNbt.contains(type.getId())) {
                    data.killsByAlignment.put(type, killsByAlignNbt.getInt(type.getId()));
                }
            }
        }

        data.lastKillX = nbt.getDouble("LastKillX");
        data.lastKillY = nbt.getDouble("LastKillY");
        data.lastKillZ = nbt.getDouble("LastKillZ");
        data.spatialKillCount = nbt.getInt("SpatialKillCount");

        data.resonanceBonus = nbt.getFloat("ResonanceBonus");
        data.lastResonanceCheck = nbt.getLong("LastResonanceCheck");
        data.soulEchoActive = nbt.getBoolean("SoulEchoActive");
        data.soulEchoExpiry = nbt.getLong("SoulEchoExpiry");
        data.dominantChangedAt = nbt.getLong("DominantChangedAt");

        if (nbt.contains("ShiftHistory")) {
            NbtList historyList = nbt.getList("ShiftHistory", 10);
            for (int i = 0; i < historyList.size(); i++) {
                NbtCompound shiftNbt = historyList.getCompound(i);
                AlignmentType type = AlignmentType.fromId(shiftNbt.getString("Type"));
                if (type != null) {
                    data.shiftHistory.add(new AlignmentShift(
                            type, shiftNbt.getFloat("Amount"), shiftNbt.getLong("Time")
                    ));
                }
            }
        }

        data.recalculateDominance();
        return data;
    }

    public record AlignmentShift(AlignmentType type, float amount, long timestamp) {}
}
