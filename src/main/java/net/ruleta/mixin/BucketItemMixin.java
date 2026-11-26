package net.ruleta.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.ruleta.compat.Bus;
import net.ruleta.compat.FillBucketEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
   @Shadow
   @Final
   private Fluid field_7905;

   private BucketItemMixin(Properties propiedades) {
      super(propiedades);
   }

   @Inject(method = "use", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$usarCubo(Level nivel, Player jugador, InteractionHand mano, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
      if (!nivel.isClientSide() && Bus.hayOyentes(FillBucketEvent.class)) {
         ItemStack cubo = jugador.getItemInHand(mano);
         net.minecraft.world.level.ClipContext.Fluid modo = this.field_7905 == Fluids.EMPTY
            ? net.minecraft.world.level.ClipContext.Fluid.SOURCE_ONLY
            : net.minecraft.world.level.ClipContext.Fluid.NONE;
         FillBucketEvent ev = new FillBucketEvent(jugador, cubo, nivel, getPlayerPOVHitResult(nivel, jugador, modo));
         if (Bus.post(ev)) {
            cir.setReturnValue(InteractionResultHolder.fail(cubo));
         }
      }
   }
}
