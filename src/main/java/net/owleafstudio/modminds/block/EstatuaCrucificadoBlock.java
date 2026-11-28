package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EstatuaCrucificadoBlock extends Block {
   public EstatuaCrucificadoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.0F, 10.0F).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return Shapes.or(
         box(4.0, 24.0, 4.0, 12.0, 32.0, 12.0),
         new VoxelShape[]{
            box(4.0, 12.0, 6.0, 12.0, 24.0, 10.0),
            box(12.0, 20.0, 6.001, 24.0, 24.0, 10.001),
            box(-8.0, 20.0, 6.001, 4.0, 24.0, 10.001),
            box(4.1, 0.0, 6.0, 8.1, 12.0, 10.0),
            box(7.9, 0.0, 6.0, 11.9, 12.0, 10.0),
            box(4.1, 0.0, 6.0, 8.1, 12.0, 10.0),
            box(7.9, 0.0, 6.0, 11.9, 12.0, 10.0)
         }
      );
   }
}
