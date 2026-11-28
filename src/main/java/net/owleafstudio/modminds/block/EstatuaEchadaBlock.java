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

public class EstatuaEchadaBlock extends Block {
   public EstatuaEchadaBlock() {
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
         box(4.0, -2.0, 16.0, 12.0, 6.0, 24.0),
         new VoxelShape[]{
            box(4.0, 0.0, 4.0, 12.0, 4.0, 16.0),
            box(2.0, -0.001, 3.2, 6.0, 3.999, 15.2),
            box(10.0, -0.001, 3.2, 14.0, 3.999, 15.2),
            box(12.3, -0.001, -7.1, 16.3, 3.999, 4.9),
            box(-0.3, -1.0E-4, -7.1, 3.7, 3.9999, 4.9)
         }
      );
   }
}
