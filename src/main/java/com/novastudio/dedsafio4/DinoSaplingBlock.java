package com.novastudio.dedsafio4;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class DinoSaplingBlock extends SaplingBlock {
   private static final String[] FRUITS = new String[]{"fruta_solaria", "arandano_nocturino", "huevo_de_eburia", "baya_uvina"};
   static final int[][] FRUIT_SIDES = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final int MAX_TREE_HEIGHT = 14;
   private final boolean birch;

   public DinoSaplingBlock(boolean var1) {
      super(TreeGrower.OAK, BlockBehaviour.Properties.of().noCollision().noOcclusion().randomTicks().instabreak().sound(SoundType.GRASS));
      this.birch = var1;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3, net.minecraft.world.level.block.BonemealSource src$) {
      return this.hasRoom(var1, var2);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4, net.minecraft.world.level.block.BonemealSource src$) {
      return this.hasRoom(var1, var3);
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4, net.minecraft.world.level.block.BonemealSource src$) {
      this.growTree(var1, var2, var3);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2 instanceof ServerLevel var5 && var4.nextInt(7) == 0) {
         this.growTree(var5, var4, var3);
      }
   }

   private boolean hasRoom(LevelReader var1, BlockPos var2) {
      for (int var3 = 1; var3 <= 15; var3++) {
         BlockState var4 = var1.getBlockState(var2.offset(0, var3, 0));
         Block var5 = var4.getBlock();
         if (!var4.isAir() && var5 != Blocks.BIRCH_LEAVES && var5 != Blocks.OAK_LEAVES) {
            return false;
         }
      }

      return true;
   }

   private void growTree(ServerLevel var1, RandomSource var2, BlockPos var3) {
      DinoTreeGrower.grow(var1, var2, var3, this.birch);
   }

   private static void addLeafCluster(ServerLevel var0, RandomSource var1, BlockPos var2, BlockState var3, int var4) {
      for (int var5 = -var4; var5 <= var4; var5++) {
         for (int var6 = -1; var6 <= 1; var6++) {
            for (int var7 = -var4; var7 <= var4; var7++) {
               if (Math.abs(var5) + Math.abs(var7) <= var4 * 2 - 1 && (var5 == 0 && var7 == 0 || var1.nextInt(5) != 0)) {
                  addLeaf(var0, var2.offset(var5, var6, var7), var3);
               }
            }
         }
      }
   }

   private static void addLeaf(ServerLevel var0, BlockPos var1, BlockState var2) {
      if (var0.getBlockState(var1).isAir()) {
         var0.setBlock(var1, var2, 3);
      }
   }

   private static Block registeredBlock(String var0) {
      return (Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", var0));
   }
}
