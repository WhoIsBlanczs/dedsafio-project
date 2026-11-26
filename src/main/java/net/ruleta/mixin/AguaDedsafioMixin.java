package net.ruleta.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import Nonenet.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.ruleta.client.dedsafio.AguaDedsafio;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public abstract class AguaDedsafioMixin {
   @Inject(method = "renderLiquid", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$sinAguaDelMar(BlockPos pos, BlockAndTintGetter nivel, VertexConsumer consumidor, BlockState estado, FluidState fluido, CallbackInfo ci) {
      if (AguaDedsafio.ocultar(pos, fluido)) {
         ci.cancel();
      }
   }
}
