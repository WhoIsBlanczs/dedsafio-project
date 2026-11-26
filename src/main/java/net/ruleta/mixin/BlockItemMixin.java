package net.ruleta.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.Bus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
   @Inject(
      method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"
      ),
      cancellable = true,
      require = 0
   )
   private void ruleta$antesDePoner(BlockPlaceContext contexto, CallbackInfoReturnable<InteractionResult> cir) {
      Player jugador = contexto.getPlayer();
      if (jugador != null && !contexto.getLevel().isClientSide && Bus.hayOyentes(BlockEvent.EntityPlaceEvent.class)) {
         BlockItem yo = (BlockItem)this;
         BlockEvent.EntityPlaceEvent ev = new BlockEvent.EntityPlaceEvent(
            contexto.getLevel(), contexto.getClickedPos(), yo.getBlock().defaultBlockState(), jugador
         );
         if (Bus.post(ev)) {
            cir.setReturnValue(InteractionResult.FAIL);
         }
      }
   }
}
