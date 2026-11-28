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

public class EstatuaSentadoBlock extends Block {
   public EstatuaSentadoBlock() {
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
         box(4.0, 13.5, 8.4, 12.0, 21.5, 16.4),
         new VoxelShape[]{
            box(4.0, 1.5, 10.4, 12.0, 13.5, 14.4),
            box(10.9, 1.0, 10.401, 14.9, 13.0, 14.401),
            box(1.1, 1.0, 10.401, 5.1, 13.0, 14.401),
            box(7.4, 0.0, 0.0, 11.4, 4.0, 12.0),
            box(4.6, 0.0, 0.0, 8.6, 4.0, 12.0)
         }
      );
   }
}
