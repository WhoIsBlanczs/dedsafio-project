package com.novastudio.dedsafio4;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootParams;

public class DinoFruitBlock extends Block {
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 8);
   public static final int RIPE = 3;
   public static final int WILD = 7;
   public static final int ROTTEN = 8;
   private static final int GROW_STEP = 6000;
   private final int fruitKind;

   public DinoFruitBlock(int var1) {
      super(BlockBehaviour.Properties.of().noOcclusion().strength(1.0F).randomTicks().forceSolidOff());
      this.fruitKind = var1;
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AGE, 7));
   }

   public int kind() {
      return this.fruitKind;
   }

   public static float scale(BlockState var0) {
      int var1 = (Integer)var0.getValue(AGE);
      return var1 >= 3 ? 1.0F : 0.25F + 0.25F * var1;
   }

   private static boolean harvestable(int var0) {
      return var0 >= 3 && var0 <= 7;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(new Property[]{AGE});
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(AGE) < 7;
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(this) && (Integer)var1.getValue(AGE) < 3) {
         var2.scheduleTick(var3, this, 6000);
      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = (Integer)var1.getValue(AGE);
      if (var5 < 3) {
         var2.setBlock(var3, (BlockState)var1.setValue(AGE, var5 + 1), 3);
         if (var5 + 1 < 3) {
            var2.scheduleTick(var3, this, 6000);
         }
      }
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = (Integer)var1.getValue(AGE);
      if (var5 < 3) {
         var2.scheduleTick(var3, this, 6000);
      } else if (var4.nextInt(3) == 0) {
         if (var5 < 6) {
            var2.setBlock(var3, (BlockState)var1.setValue(AGE, var5 + 1), 2);
         } else {
            var2.setBlock(var3, (BlockState)var1.setValue(AGE, 8), 3);
         }
      }
   }

   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      return harvestable((Integer)var1.getValue(AGE)) ? super.getDrops(var1, var2) : List.of();
   }

   static boolean attached(net.minecraft.world.level.LevelReader var0, BlockPos var1) {
      for (int[] var5 : DinoSaplingBlock.FRUIT_SIDES) {
         if (var0.getBlockState(var1.offset(var5[0], 0, var5[2])).getBlock() instanceof DinoTreeTrunkBlock) {
            return true;
         }
      }

      return false;
   }

   protected BlockState updateShape(BlockState var1, net.minecraft.world.level.LevelReader var4, net.minecraft.world.level.ScheduledTickAccess tick$, BlockPos var5, Direction var2, BlockPos var6, BlockState var3, net.minecraft.util.RandomSource rnd$) {
      return var2.getAxis().isHorizontal() && !attached(var4, var5) ? Blocks.AIR.defaultBlockState() : var1;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return DinoFruitShapes.shape(this);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return DinoFruitShapes.shape(this);
   }
}
