package net.ruleta.mixin;

import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.ruleta.candado.Propiedades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends Block {
   private ChestBlockMixin(Properties propiedades) {
      super(propiedades);
   }

   @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
   private void ruleta$propiedadLocked(Builder<Block, BlockState> constructor, CallbackInfo ci) {
      if ((Object)this.getClass() == ChestBlock.class || (Object)this.getClass() == TrappedChestBlock.class) {
         constructor.add(new Property[]{Propiedades.LOCKED});
      }
   }

   @Inject(method = "<init>", at = @At("TAIL"))
   private void ruleta$sinCandadoPorDefecto(Supplier<BlockEntityType<? extends ChestBlockEntity>> tipo, net.minecraft.sounds.SoundEvent abrir, net.minecraft.sounds.SoundEvent cerrar, Properties propiedades, CallbackInfo ci) {
      if (this.defaultBlockState().hasProperty(Propiedades.LOCKED)) {
         this.registerDefaultState((BlockState)(Object)this.defaultBlockState().setValue(Propiedades.LOCKED, false));
      }
   }
}
