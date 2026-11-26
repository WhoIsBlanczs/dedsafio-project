package net.ruleta.puerta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class LadrilloBunkerBloque extends Block {
   public static final IntegerProperty PX = IntegerProperty.create("px", 0, 3);
   public static final IntegerProperty PY = IntegerProperty.create("py", 0, 3);
   public static final IntegerProperty PZ = IntegerProperty.create("pz", 0, 3);

   public LadrilloBunkerBloque(Properties props) {
      super(props);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PX, 0)).setValue(PY, 0)).setValue(PZ, 0)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{PX, PY, PZ});
   }

   private BlockState conPosicion(BlockState s, BlockPos pos) {
      return (BlockState)((BlockState)((BlockState)s.setValue(PX, Math.floorMod(pos.getX(), 4))).setValue(PY, Math.floorMod(pos.getY(), 4)))
         .setValue(PZ, Math.floorMod(pos.getZ(), 4));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return this.conPosicion(this.defaultBlockState(), ctx.getClickedPos());
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState anterior, boolean moviendo) {
      if (!level.isClientSide) {
         BlockState correcto = this.conPosicion(state, pos);
         if (correcto != state) {
            level.setBlock(pos, correcto, 2);
         }
      }

      super.onPlace(state, level, pos, anterior, moviendo);
   }
}
