package dev.soulbound.mixin;

import dev.soulbound.SoulboundMod;
import dev.soulbound.handler.MobPerceptionHandler;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin {
    @Inject(method = "isPlayerStaring", at = @At("HEAD"), cancellable = true)
    private void soulbound$preventAnger(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        MobPerceptionHandler handler = SoulboundMod.getMobPerceptionHandler();
        if (handler == null) return;

        if (handler.shouldEndermanBeNeutral(serverPlayer)) {
            cir.setReturnValue(false);
        }
    }
}
