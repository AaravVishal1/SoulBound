package dev.soulbound.mixin;

import dev.soulbound.SoulboundMod;
import dev.soulbound.handler.MobPerceptionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void soulbound$modifyTargeting(LivingEntity target, CallbackInfo ci) {
        if (target == null) return;
        if (!(target instanceof ServerPlayerEntity player)) return;

        MobEntity self = (MobEntity) (Object) this;
        MobPerceptionHandler handler = SoulboundMod.getMobPerceptionHandler();
        if (handler == null) return;

        if (handler.shouldAvoidTarget(self, player)) {
            ci.cancel();
            return;
        }

        if (self instanceof SpiderEntity && handler.shouldSpiderAvoidDuringDay(player)) {
            long timeOfDay = self.getWorld().getTimeOfDay() % 24000;
            boolean isDaytime = timeOfDay < 13000 || timeOfDay > 23000;
            if (isDaytime) {
                ci.cancel();
                return;
            }
        }

        int delayTicks = handler.getAttackDelayTicks(self, player);
        if (delayTicks > 0 && self.age % delayTicks < delayTicks / 2) {
            ci.cancel();
        }
    }
}
