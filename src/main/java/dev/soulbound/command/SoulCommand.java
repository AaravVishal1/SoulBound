package dev.soulbound.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import dev.soulbound.alignment.AlignmentType;
import dev.soulbound.config.ConfigManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class SoulCommand {
    private final ConfigManager configManager;

    public SoulCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    private static final SuggestionProvider<ServerCommandSource> ALIGNMENT_SUGGESTIONS = (context, builder) -> {
        for (AlignmentType type : AlignmentType.values()) {
            builder.suggest(type.getId());
        }
        return builder.buildFuture();
    };

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("soul")
                        .executes(this::showSummary)
                        .then(CommandManager.literal("details")
                                .executes(this::showDetails))
                        .then(CommandManager.literal("history")
                                .executes(this::showHistory))
                        .then(CommandManager.literal("stats")
                                .executes(this::showStats))
                        .then(CommandManager.literal("help")
                                .executes(this::showHelp))
                        .then(CommandManager.literal("lore")
                                .executes(this::showLore))
                        .then(CommandManager.literal("trait")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("alignment", StringArgumentType.word())
                                        .suggests(ALIGNMENT_SUGGESTIONS)
                                        .then(CommandManager.argument("amount", FloatArgumentType.floatArg(0.1f, 500.0f))
                                                .executes(this::grantTrait))))
        );
    }

    private int showSummary(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();

        player.sendMessage(Text.literal("═══════ Soul Alignment ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

        AlignmentType dominant = data.getDominant();
        if (dominant == null) {
            player.sendMessage(Text.literal("  No dominant alignment yet. Go forth and slay!")
                    .formatted(Formatting.GRAY), false);
            return 1;
        }

        AlignmentType.Tier tier = data.getDominantTier();

MutableText domLine = Text.literal("  ◆ Dominant: ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(dominant.getDisplayName())
                        .formatted(dominant.getFormatting(), Formatting.BOLD))
                .append(Text.literal(" [" + String.format("%.1f", data.getDominantValue()) + "]")
                        .formatted(Formatting.GRAY));
        player.sendMessage(domLine, false);

MutableText tierLine = Text.literal("  ★ Tier: ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(tier.name())
                        .formatted(Formatting.GOLD));
        player.sendMessage(tierLine, false);

AlignmentType secondary = data.getSecondary();
        if (secondary != null) {
            MutableText secLine = Text.literal("  ◇ Secondary: ")
                    .formatted(Formatting.WHITE)
                    .append(Text.literal(secondary.getDisplayName())
                            .formatted(secondary.getFormatting()))
                    .append(Text.literal(" [" + String.format("%.1f", data.getAlignment(secondary)) + "]")
                            .formatted(Formatting.GRAY));
            player.sendMessage(secLine, false);
        }

if (data.isApex()) {
            player.sendMessage(Text.literal("  ☀ State: APEX")
                    .formatted(Formatting.GOLD, Formatting.BOLD), false);
}
        if (data.isTranscendent()) {
            player.sendMessage(Text.literal("  ✦ State: TRANSCENDENT")
                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        }
        if (data.isFractured()) {
            player.sendMessage(Text.literal("  ☠ FRACTURED [Level: " + String.format("%.1f", data.getFractureLevel()) + "]")
                    .formatted(Formatting.DARK_RED), false);
        }

if (data.getMomentumStreak() > 0) {
            player.sendMessage(Text.literal("  🔥 Momentum: x" + data.getMomentumStreak()
                            + " (" + String.format("%.1f", data.getMomentumMultiplier()) + "x)")
                    .formatted(Formatting.RED), false);
}

if (data.getResonanceBonus() > 0) {
            player.sendMessage(Text.literal("  ✧ Resonance: +" + String.format("%.0f%%", data.getResonanceBonus() * 100))
                    .formatted(Formatting.AQUA), false);
}

float synergy = data.getSynergyBonus();
        float conflict = data.getConflictPenalty();
        if (synergy > 0) {
            player.sendMessage(Text.literal("  ⚡ Synergy Bonus: +" + String.format("%.0f%%", synergy * 100))
                    .formatted(Formatting.GREEN), false);
        }
        if (conflict > 0) {
            player.sendMessage(Text.literal("  ⚡ Conflict Penalty: -" + String.format("%.0f%%", conflict * 100))
                    .formatted(Formatting.RED), false);
        }

        return 1;
    }

    private int showDetails(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();

        player.sendMessage(Text.literal("═══════ Soul Details ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

        for (Map.Entry<AlignmentType, Float> entry : data.getAllAlignments().entrySet()) {
            AlignmentType type = entry.getKey();
            float value = entry.getValue();
            if (value <= 0) continue;

            AlignmentType.Tier tier = AlignmentType.Tier.fromValue(value);
            MutableText line = Text.literal("  ● " + type.getDisplayName() + ": ")
                    .formatted(type.getFormatting())
                    .append(Text.literal(String.format("%.1f", value))
                            .formatted(Formatting.WHITE))
                    .append(Text.literal(" (" + tier.getName() + ")")
                            .formatted(Formatting.GRAY));
            player.sendMessage(line, false);
        }

        return 1;
    }

    private int showHistory(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();

        player.sendMessage(Text.literal("═══════ Shift History ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

        var history = data.getShiftHistory();
        if (history.isEmpty()) {
            player.sendMessage(Text.literal("  No alignment shifts recorded yet.")
                    .formatted(Formatting.GRAY), false);
            return 1;
        }

        for (int i = history.size() - 1; i >= 0; i--) {
            var shift = history.get(i);
            MutableText line = Text.literal("  → ")
                    .formatted(Formatting.GRAY)
                    .append(Text.literal(shift.type().getDisplayName())
                            .formatted(shift.type().getFormatting()))
                    .append(Text.literal(String.format(" +%.1f", shift.amount()))
                            .formatted(Formatting.WHITE));
            player.sendMessage(line, false);
        }

        return 1;
    }

    private int showStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();

        player.sendMessage(Text.literal("═══════ Soul Statistics ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

        player.sendMessage(Text.literal("  Total Kills: " + data.getTotalKills())
                .formatted(Formatting.WHITE), false);

        for (Map.Entry<AlignmentType, Integer> entry : data.getKillsByAlignment().entrySet()) {
            if (entry.getValue() > 0) {
                player.sendMessage(Text.literal("    " + entry.getKey().getDisplayName() + ": " + entry.getValue())
                        .formatted(entry.getKey().getFormatting()), false);
            }
        }

        if (data.getMomentumStreak() > 0) {
            player.sendMessage(Text.literal("  Momentum Streak: x" + data.getMomentumStreak()
                            + " (" + String.format("%.1f", data.getMomentumMultiplier()) + "x multiplier)")
                    .formatted(Formatting.RED), false);
        }

        return 1;
    }

    private int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        player.sendMessage(Text.literal("═══════ Soulbound Help ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("  /soul")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Show alignment summary")
                        .formatted(Formatting.GRAY)), false);
        player.sendMessage(Text.literal("  /soul details")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Show all alignment values")
                        .formatted(Formatting.GRAY)), false);
        player.sendMessage(Text.literal("  /soul history")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Show recent alignment shifts")
                        .formatted(Formatting.GRAY)), false);
        player.sendMessage(Text.literal("  /soul stats")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Show kill statistics")
                        .formatted(Formatting.GRAY)), false);
        player.sendMessage(Text.literal("  /soul lore")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Show alignment lore")
                        .formatted(Formatting.GRAY)), false);
        player.sendMessage(Text.literal("  /soul trait <alignment> <amount>")
                .formatted(Formatting.GOLD)
                .append(Text.literal(" — Grant alignment points (OP)")
                        .formatted(Formatting.GRAY)), false);

        return 1;
    }

    private int grantTrait(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        String alignmentId = StringArgumentType.getString(context, "alignment");
        float amount = FloatArgumentType.getFloat(context, "amount");

        AlignmentType type = AlignmentType.fromId(alignmentId);
        if (type == null) {
            source.sendError(Text.literal("Unknown alignment: " + alignmentId + ". Valid types: decay, precision, volatility, void, instinct, order, savagery"));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        data.addAlignment(type, amount);

        player.sendMessage(Text.literal("═══════ Soul Trait Granted ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("  ✦ +" + String.format("%.1f", amount) + " ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(type.getDisplayName())
                        .formatted(type.getFormatting(), Formatting.BOLD))
                .append(Text.literal(" absorbed into your soul.")
                        .formatted(Formatting.GRAY)), false);

        AlignmentType.Tier newTier = data.getDominantTier();
        player.sendMessage(Text.literal("  Current " + type.getDisplayName() + ": " + String.format("%.1f", data.getAlignment(type)))
                .formatted(type.getFormatting()), false);
        if (data.getDominant() == type) {
            player.sendMessage(Text.literal("  Tier: " + newTier.getName())
                    .formatted(Formatting.GOLD), false);
        }

        return 1;
    }

    private int showLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("This command can only be used by players."));
            return 0;
        }

        AlignmentData data = ((AlignmentDataAccessor) player).soulbound$getAlignmentData();
        AlignmentType dominant = data.getDominant();

        player.sendMessage(Text.literal("═══════ Soul Lore ═══════")
                .formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);

        if (dominant == null) {
            player.sendMessage(Text.literal("  Your soul is unaligned. Kill mobs to absorb their essence.")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), false);
            return 1;
        }

        player.sendMessage(Text.literal("  " + dominant.getDisplayName())
                .formatted(dominant.getFormatting(), Formatting.BOLD), false);
        player.sendMessage(Text.literal("  \"" + dominant.getLoreText() + "\"")
                .formatted(Formatting.GRAY, Formatting.ITALIC), false);

        AlignmentType.Tier tier = data.getDominantTier();
        player.sendMessage(Text.literal("  Tier: " + tier.getName())
                .formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("  \"" + tier.getDescription() + "\"")
                .formatted(Formatting.GRAY, Formatting.ITALIC), false);

        return 1;
    }
}
