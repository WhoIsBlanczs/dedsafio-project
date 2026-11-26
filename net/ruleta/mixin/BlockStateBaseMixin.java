package net.ruleta.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.ruleta.Proteccion;
import net.ruleta.Vanilla;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockStateBase.class})
public abstract class BlockStateBaseMixin {
   @Inject(
      method = {"getFluidState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ruleta$puertaConAgua(CallbackInfoReturnable<FluidState> cir) {
      BlockState self = (BlockState)this;
      if (self.getBlock() instanceof DoorBlock
         && self.hasProperty(BlockStateProperties.WATERLOGGED)
         && (Boolean)self.getValue(BlockStateProperties.WATERLOGGED)
         && Vanilla.es(self.getBlock())) {
         cir.setReturnValue(Fluids.WATER.getSource(false));
      }
   }

   @Inject(
      method = {"getDestroyProgress"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$soloCreativoOOp(Player jugador, BlockGetter nivel, BlockPos pos, CallbackInfoReturnable<Float> cir) {
      BlockState self = (BlockState)this;
      if (Proteccion.protegido(self.getBlock()) && !Proteccion.puede(jugador)) {
         cir.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getPistonPushReaction"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$sinPiston(CallbackInfoReturnable<PushReaction> cir) {
      if (Proteccion.protegido(((BlockState)this).getBlock())) {
         cir.setReturnValue(PushReaction.BLOCK);
      }
   }
}
