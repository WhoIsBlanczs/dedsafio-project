package net.ruleta.arboles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BroteBloque extends BushBlock implements BonemealableBlock {
   public static final MapCodec<BroteBloque> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(propertiesCodec(), Codec.INT.fieldOf("tipo").forGetter(b -> b.tipo)).apply(i, BroteBloque::new)
   );
   private static final VoxelShape FORMA = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
   private final int tipo;

   public BroteBloque(Properties p, int tipo) {
      super(p);
      this.tipo = tipo;
   }


   public MutableComponent getName() {
      return Component.literal(this.tipo == 1 ? "Retoño de palmera" : "Retoño de árbol de costa");
   }

   protected VoxelShape getShape(BlockState estado, BlockGetter nivel, BlockPos pos, CollisionContext ctx) {
      return FORMA;
   }

   protected boolean mayPlaceOn(BlockState suelo, BlockGetter nivel, BlockPos pos) {
      return suelo.is(BlockTags.DIRT) || suelo.is(BlockTags.SAND) || suelo.is(Blocks.FARMLAND);
   }

   protected void randomTick(BlockState estado, ServerLevel nivel, BlockPos pos, RandomSource r) {
      if (nivel.getMaxLocalRawBrightness(pos.above()) >= 9 && r.nextInt(7) == 0) {
         this.crecer(nivel, pos, r);
      }
   }

   private void crecer(ServerLevel nivel, BlockPos pos, RandomSource r) {
      nivel.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
      GeneradorArboles.crecer(nivel, pos, r, this.tipo);
   }

   public boolean isValidBonemealTarget(LevelReader nivel, BlockPos pos, BlockState estado) {
      return true;
   }

   public boolean isBonemealSuccess(Level nivel, RandomSource r, BlockPos pos, BlockState estado) {
      return r.nextFloat() < 0.45F;
   }

   public void performBonemeal(ServerLevel nivel, RandomSource r, BlockPos pos, BlockState estado) {
      this.crecer(nivel, pos, r);
   }
}
