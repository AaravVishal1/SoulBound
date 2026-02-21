package dev.soulbound.alignment;

import net.minecraft.nbt.NbtCompound;

public interface AlignmentDataAccessor {
    AlignmentData soulbound$getAlignmentData();

    void soulbound$setAlignmentData(AlignmentData data);

    void soulbound$writeAlignmentNbt(NbtCompound nbt);

    void soulbound$readAlignmentNbt(NbtCompound nbt);
}
