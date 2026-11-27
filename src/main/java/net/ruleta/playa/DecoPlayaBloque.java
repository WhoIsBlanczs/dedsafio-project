package net.ruleta.playa;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecoPlayaBloque extends Block {
   private static final VoxelShape FORMA = Block.box(5.0, 0.0, 5.0, 11.0, 2.0, 11.0);
   private final String nombre;

   public DecoPlayaBloque(Properties p) {
      this(p, "Decoración de playa");
   }

   public DecoPlayaBloque(Properties p, String nombre) {
      super(p);
      this.nombre = nombre;
   }


   public MutableComponent getName() {
      return Component.literal(this.nombre);
   }

   protected VoxelShape getShape(BlockState estado, BlockGetter nivel, BlockPos pos, CollisionContext ctx) {
      Vec3 o = estado.getOffset(pos);
      return FORMA.move(o.x, o.y, o.z);
   }

   protected boolean canSurvive(BlockState estado, LevelReader nivel, BlockPos pos) {
      return Block.canSupportCenter(nivel, pos.below(), Direction.UP);
   }

   protected BlockState updateShape(BlockState estado, net.minecraft.world.level.LevelReader nivel, net.minecraft.world.level.ScheduledTickAccess ticks_, BlockPos pos, Direction dir, BlockPos posVecino, BlockState vecino, net.minecraft.util.RandomSource rnd_) {
      return dir == Direction.DOWN && !estado.canSurvive(nivel, pos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(estado, nivel, ticks_, pos, dir, posVecino, vecino, rnd_);
   }
}
