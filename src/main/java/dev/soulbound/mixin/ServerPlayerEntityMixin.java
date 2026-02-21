package dev.soulbound.mixin;

import dev.soulbound.alignment.AlignmentData;
import dev.soulbound.alignment.AlignmentDataAccessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements AlignmentDataAccessor {
    @Unique
    private AlignmentData soulbound$alignmentData = new AlignmentData();

    @Override
    public AlignmentData soulbound$getAlignmentData() {
        return soulbound$alignmentData;
    }

    @Override
    public void soulbound$setAlignmentData(AlignmentData data) {
        this.soulbound$alignmentData = data;
    }

    @Override
    public void soulbound$writeAlignmentNbt(NbtCompound nbt) {
        nbt.put("SoulboundAlignment", soulbound$alignmentData.toNbt());
    }

    @Override
    public void soulbound$readAlignmentNbt(NbtCompound nbt) {
        if (nbt.contains("SoulboundAlignment")) {
            soulbound$alignmentData = AlignmentData.fromNbt(nbt.getCompound("SoulboundAlignment"));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void soulbound$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        soulbound$writeAlignmentNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void soulbound$readNbt(NbtCompound nbt, CallbackInfo ci) {
        soulbound$readAlignmentNbt(nbt);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void soulbound$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        AlignmentDataAccessor oldAccessor = (AlignmentDataAccessor) oldPlayer;
        this.soulbound$alignmentData = oldAccessor.soulbound$getAlignmentData();
    }
}
