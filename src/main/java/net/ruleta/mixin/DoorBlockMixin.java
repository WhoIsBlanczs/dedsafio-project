package net.ruleta.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.ruleta.Ruleta;
import net.ruleta.Vanilla;
import net.ruleta.candado.Candados;
import net.ruleta.candado.PuertaCandadoBloque;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
   private DoorBlockMixin(Properties propiedades) {
      super(propiedades);
   }

   @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
   private void ruleta$propiedadAgua(Builder<Block, BlockState> constructor, CallbackInfo ci) {
      if (this.getClass() == DoorBlock.class || this.getClass() == PuertaCandadoBloque.class) {
         constructor.add(new Property[]{BlockStateProperties.WATERLOGGED});
      }
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void ruleta$sinAguaPorDefecto(BlockSetType tipo, Properties propiedades, CallbackInfo ci) {
      if (this.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED)) {
         this.registerDefaultState((BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
      }
   }

   @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
   private void ruleta$colocarEnAgua(BlockPlaceContext contexto, CallbackInfoReturnable<BlockState> cir) {
      BlockState estado = (BlockState)cir.getReturnValue();
      if (estado != null && estado.hasProperty(BlockStateProperties.WATERLOGGED)) {
         Level nivel = contexto.getLevel();
         BlockPos pos = contexto.getClickedPos();
         boolean agua = !nivel.isClientSide()
            && Ruleta.cambioActivoPublico("respirar-bajo-agua")
            && nivel.getFluidState(pos).is(FluidTags.WATER)
            && nivel.getFluidState(pos.above()).is(FluidTags.WATER)
            && Vanilla.es(estado.getBlock());
         cir.setReturnValue((BlockState)estado.setValue(BlockStateProperties.WATERLOGGED, agua));
      }
   }

   @Inject(method = "updateShape", at = @At("HEAD"))
   private void ruleta$mantenerAgua(
      BlockState estado, Direction direccion, BlockState vecino, LevelAccessor nivel, BlockPos pos, BlockPos posVecino, CallbackInfoReturnable<BlockState> cir
   ) {
      if (estado.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)estado.getValue(BlockStateProperties.WATERLOGGED)) {
         nivel.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(nivel));
      }
   }

   @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
   private void ruleta$sinRedstone(BlockState estado, Level nivel, BlockPos pos, Block bloque, BlockPos origen, boolean movido, CallbackInfo ci) {
      if (Candados.estaBloqueado(nivel, pos)) {
         ci.cancel();
      }
   }
}
