package net.ruleta.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.ruleta.compat.ItemBloqueado;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class ItemStackMixin {
   @Inject(
      method = {"useOn"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$sinUsar(UseOnContext contexto, CallbackInfoReturnable<InteractionResult> cir) {
      if (ItemBloqueado.bloqueado(contexto.getPlayer())) {
         cir.setReturnValue(InteractionResult.PASS);
      }
   }
}
