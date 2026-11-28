package net.ruleta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import com.casinohs.SlotMachineBlockEntity;

import net.ruleta.CasinoDatos;

/** El premio que entrega la tragamonedas sale de la lista editada en el panel. */
@Mixin(targets = "com.casinohs.SlotPrizes", remap = false)
public abstract class SlotPrizesMixin {
	@Inject(method = "rewardFor", at = @At("HEAD"), cancellable = true, remap = false)
	private static void ruleta$premio(SlotMachineBlockEntity maquina, ServerLevel level, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack premio = CasinoDatos.premio(maquina.prize(), level);

		if (premio != null) {
			cir.setReturnValue(premio);
		}
	}
}
