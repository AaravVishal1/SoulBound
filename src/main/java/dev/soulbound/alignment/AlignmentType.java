package dev.soulbound.alignment;

import net.minecraft.util.Formatting;

import java.util.EnumSet;
import java.util.Set;

public enum AlignmentType {
    DECAY("decay", "Decay", 0x556B2F, "The grave whispers your name.", Formatting.DARK_GREEN),
    PRECISION("precision", "Precision", 0xC0C0C0, "Your aim sharpens beyond mortal limits.", Formatting.WHITE),
    VOLATILITY("volatility", "Volatility", 0x00FF00, "Chaos pulses through your veins.", Formatting.GREEN),
    VOID("void", "Void", 0x800080, "The space between worlds embraces you.", Formatting.DARK_PURPLE),
    INSTINCT("instinct", "Instinct", 0x8B0000, "You feel the web of life tighten.", Formatting.DARK_RED),
    ORDER("order", "Order", 0xFFD700, "Civilization's light burns within you.", Formatting.GOLD),
    SAVAGERY("savagery", "Savagery", 0x708090, "The frenzy of battle consumes you.", Formatting.GRAY);

    private final String id;
    private final String displayName;
    private final int color;
    private final String loreText;
    private final Formatting formatting;

    AlignmentType(String id, String displayName, int color, String loreText, Formatting formatting) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.loreText = loreText;
        this.formatting = formatting;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public String getLoreText() {
        return loreText;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    public Set<AlignmentType> getOpposing() {
        return switch (this) {
            case DECAY -> EnumSet.of(ORDER);
            case ORDER -> EnumSet.of(DECAY, SAVAGERY);
            case PRECISION -> EnumSet.of(VOLATILITY);
            case VOLATILITY -> EnumSet.of(PRECISION);
            case VOID -> EnumSet.of(INSTINCT);
            case INSTINCT -> EnumSet.of(VOID);
            case SAVAGERY -> EnumSet.of(ORDER);
        };
    }

    public Set<AlignmentType> getSynergies() {
        return switch (this) {
            case DECAY -> EnumSet.of(VOID);
            case VOID -> EnumSet.of(DECAY);
            case PRECISION -> EnumSet.of(ORDER);
            case ORDER -> EnumSet.of(PRECISION);
            case VOLATILITY -> EnumSet.of(SAVAGERY);
            case SAVAGERY -> EnumSet.of(VOLATILITY);
            case INSTINCT -> EnumSet.of(SAVAGERY);
        };
    }

    public DimensionAffinity getDimensionAffinity() {
        return switch (this) {
            case DECAY, VOLATILITY, SAVAGERY -> DimensionAffinity.NETHER;
            case VOID -> DimensionAffinity.END;
            case ORDER, PRECISION, INSTINCT -> DimensionAffinity.OVERWORLD;
        };
    }

    public static AlignmentType fromId(String id) {
        for (AlignmentType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }

    public enum DimensionAffinity {
        OVERWORLD("minecraft:overworld"),
        NETHER("minecraft:the_nether"),
        END("minecraft:the_end");

        private final String dimensionId;

        DimensionAffinity(String dimensionId) {
            this.dimensionId = dimensionId;
        }

        public String getDimensionId() {
            return dimensionId;
        }
    }

    public enum Tier {
        DORMANT(0, 0, "Dormant", "Your soul is unaligned."),
        FLEDGLING(1, 10, "Fledgling", "A faint echo stirs within."),
        ATTUNED(2, 30, "Attuned", "The alignment takes root."),
        RESONANT(3, 60, "Resonant", "Your soul resonates deeply."),
        APEX(4, 100, "Apex", "You have become one with the alignment."),
        TRANSCENDENT(5, 150, "Transcendent", "Reality itself bends to your nature.");

        private final int level;
        private final float threshold;
        private final String name;
        private final String description;

        Tier(int level, float threshold, String name, String description) {
            this.level = level;
            this.threshold = threshold;
            this.name = name;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public float getThreshold() {
            return threshold;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public static Tier fromValue(float value) {
            Tier result = DORMANT;
            for (Tier tier : values()) {
                if (value >= tier.threshold) {
                    result = tier;
                }
            }
            return result;
        }
    }
}
