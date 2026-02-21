package dev.soulbound.mixin;

import dev.soulbound.SoulboundMod;
import dev.soulbound.handler.MobPerceptionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {
    @Shadow
    private int fuseTime;

    @Shadow
    private int currentFuseTime;

    @Inject(method = "tick", at = @At("HEAD"))
    private void soulbound$modifyCreeperFuse(CallbackInfo ci) {
        CreeperEntity self = (CreeperEntity) (Object) this;
        LivingEntity target = self.getTarget();
        if (target == null) return;
        if (!(target instanceof ServerPlayerEntity player)) return;

        MobPerceptionHandler handler = SoulboundMod.getMobPerceptionHandler();
        if (handler == null) return;

        if (handler.shouldCreeperDelay(player)) {
            int extension = handler.getCreeperFuseExtension(player);
            if (currentFuseTime > 0 && currentFuseTime < extension) {
                currentFuseTime = Math.max(0, currentFuseTime - 1);
            }
        }
    }
}
