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

public class ConoGordoBlock extends Block {
   public ConoGordoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.0F, 10.0F).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
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
         box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
         new VoxelShape[]{
            box(0.0, 0.0, 1.0, 15.0, 18.0, 15.0),
            box(4.0, 20.0, 7.0, 11.0, 21.0, 8.0),
            box(4.0, 18.0, 7.0, 5.0, 20.0, 8.0),
            box(10.0, 18.0, 7.0, 11.0, 20.0, 8.0)
         }
      );
   }
}
