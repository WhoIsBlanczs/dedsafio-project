package net.ruleta.puerta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PuertaBunkerHoja extends Block {
   public PuertaBunkerHoja(Properties props) {
      super(props);
   }

   public RenderShape getRenderShape(BlockState s) {
      return RenderShape.INVISIBLE;
   }

   public VoxelShape getShape(BlockState s, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return Shapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState s, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return Shapes.block();
   }

   public VoxelShape getVisualShape(BlockState s, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return Shapes.empty();
   }

   public boolean propagatesSkylightDown(BlockState s, BlockGetter level, BlockPos pos) {
      return true;
   }

   public float getShadeBrightness(BlockState s, BlockGetter level, BlockPos pos) {
      return 1.0F;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack((ItemLike)PuertaBunker.ITEM.get());
   }
}
