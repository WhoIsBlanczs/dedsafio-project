package net.ruleta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.trading.MerchantOffers;

import net.ruleta.PedestalDatos;

/** Los comercios del Pedestal de canje de Hombrosafío salen de la lista editada en el panel. */
@Mixin(targets = "ar.hombrosafio.CasinoExchange", remap = false)
public abstract class CasinoExchangeMixin {
	@Inject(method = "offers", at = @At("HEAD"), cancellable = true, remap = false)
	private static void ruleta$ofertas(CallbackInfoReturnable<MerchantOffers> cir) {
		MerchantOffers ofertas = PedestalDatos.ofertas();

		if (ofertas != null) {
			cir.setReturnValue(ofertas);
		}
	}
}
