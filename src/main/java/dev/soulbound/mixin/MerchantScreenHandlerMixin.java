package dev.soulbound.mixin;

import dev.soulbound.SoulboundMod;
import dev.soulbound.handler.MobPerceptionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin {
    @Shadow
    @Final
    private Merchant merchant;

    @Inject(method = "switchTo", at = @At("TAIL"))
    private void soulbound$modifyTradePrices(int recipeIndex, CallbackInfo ci) {
        PlayerEntity customer = merchant.getCustomer();
        if (!(customer instanceof ServerPlayerEntity serverPlayer)) return;

        MobPerceptionHandler mobHandler = SoulboundMod.getMobPerceptionHandler();
        if (mobHandler == null) return;

        float modifier = mobHandler.getTradeModifier(serverPlayer);
        if (modifier == 0.0f) return;

        MerchantScreenHandler handler = (MerchantScreenHandler) (Object) this;
        if (recipeIndex < 0 || recipeIndex >= handler.getRecipes().size()) return;

        TradeOffer offer = handler.getRecipes().get(recipeIndex);
        if (offer == null) return;

        int currentPrice = offer.getOriginalFirstBuyItem().getCount();
        int priceChange = Math.max(1, (int) (currentPrice * Math.abs(modifier)));

        if (modifier > 0) {
            offer.increaseSpecialPrice(priceChange);
        } else {
            offer.increaseSpecialPrice(-priceChange);
        }
    }
}
