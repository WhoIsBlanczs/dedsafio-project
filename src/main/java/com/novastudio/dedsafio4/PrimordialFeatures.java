package com.novastudio.dedsafio4;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction;

public final class PrimordialFeatures {
   private PrimordialFeatures() {
   }

   static BlockState block(String var0) {
      return ((Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", var0))).defaultBlockState();
   }

   static boolean open(WorldGenLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.isAir() || var2.canBeReplaced() || var2.is(BlockTags.LEAVES);
   }

   static boolean soil(WorldGenLevel var0, BlockPos var1) {
      return var0.getBlockState(var1.below()).is(BlockTags.DIRT);
   }

   static void leaf(WorldGenLevel var0, BlockPos var1, BlockState var2) {
      if (var0.getBlockState(var1).isAir()) {
         var0.setBlock(var1, var2, 2);
      }
   }

   static boolean clear(WorldGenLevel var0, BlockPos var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (!open(var0, var1.above(var3))) {
            return false;
         }
      }

      return true;
   }

   public static final class AmberCluster implements Feature {
      public static final com.mojang.serialization.MapCodec<AmberCluster> CODEC = com.mojang.serialization.MapCodec.unit(AmberCluster::new);

      public com.mojang.serialization.MapCodec<AmberCluster> codec() {
         return CODEC;
      }

      public AmberCluster() {
      }

      public boolean place(net.minecraft.world.level.WorldGenLevel var1Level, net.minecraft.world.level.chunk.ChunkGenerator var1Gen, net.minecraft.util.RandomSource var1Random, net.minecraft.core.BlockPos var1Origin) {
         WorldGenLevel var2 = var1Level;
         RandomSource var3 = var1Random;
         BlockPos var4 = var1Origin;
         if (!var2.getBlockState(var4.above()).isRedstoneConductor(var2, var4.above())) {
            return false;
         } else {
            BlockState var5 = PrimordialFeatures.block(var3.nextBoolean() ? "tierra_gruesa_prehistorica" : "tierra_prehistorica");
            BlockState var6 = PrimordialFeatures.block("farol_de_ambar");
            int var7 = 1 + var3.nextInt(2);
            int var8 = 2 + var3.nextInt(3);

            for (int var9 = 0; var9 < var8; var9++) {
               int var10 = Math.max(0, var7 - var9 / 2);

               for (int var11 = -var10; var11 <= var10; var11++) {
                  for (int var12 = -var10; var12 <= var10; var12++) {
                     if (var11 * var11 + var12 * var12 <= var10 * var10 + 1) {
                        BlockPos var13 = var4.offset(var11, -var9, var12);
                        if (var2.getBlockState(var13).isAir()) {
                           boolean var14 = var9 == var8 - 1 || Math.abs(var11) == var10 || Math.abs(var12) == var10;
                           var2.setBlock(var13, var14 && var3.nextInt(3) == 0 ? var6 : var5, 2);
                        }
                     }
                  }
               }
            }

            BlockPos var15 = var4.below(var8);
            if (var2.getBlockState(var15).isAir()) {
               var2.setBlock(var15, var6, 2);
            }

            for (int var16 = 0; var16 < 4; var16++) {
               BlockPos var17 = var4.offset(var3.nextInt(3) - 1, -var8 - 1, var3.nextInt(3) - 1);
               if (var2.getBlockState(var17).isAir() && !var2.getBlockState(var17.above()).isAir()) {
                  var2.setBlock(var17, Blocks.HANGING_ROOTS.defaultBlockState(), 2);
               }
            }

            return true;
         }
      }
   }

   public static final class Araucaria implements Feature {
      public static final com.mojang.serialization.MapCodec<Araucaria> CODEC = com.mojang.serialization.MapCodec.unit(Araucaria::new);

      public com.mojang.serialization.MapCodec<Araucaria> codec() {
         return CODEC;
      }

      public Araucaria() {
      }

      public boolean place(net.minecraft.world.level.WorldGenLevel var1Level, net.minecraft.world.level.chunk.ChunkGenerator var1Gen, net.minecraft.util.RandomSource var1Random, net.minecraft.core.BlockPos var1Origin) {
         WorldGenLevel var2 = var1Level;
         RandomSource var3 = var1Random;
         BlockPos var4 = var1Origin;
         int var5 = 14 + var3.nextInt(15);
         if (PrimordialFeatures.soil(var2, var4) && PrimordialFeatures.clear(var2, var4, var5 + 3)) {
            BlockState var6 = PrimordialFeatures.block("tronco_roble_quiu_dino");
            BlockState var7 = (BlockState)Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);

            for (int var8 = 0; var8 < var5; var8++) {
               var2.setBlock(var4.above(var8), var6, 2);
            }

            BlockPos var16 = var4.above(var5);
            disc(var2, var16, 2, var7, var3, 0.0F);
            disc(var2, var16.below(), 4, var7, var3, 0.15F);
            disc(var2, var16.below(2), 3, var7, var3, 0.35F);
            int var9 = 0;

            for (int var10 = var5 - 3; var9 < 3 && var10 > var5 / 2; var10 -= 3 + var3.nextInt(2)) {
               int var11 = 2 + var9;

               for (Direction var13 : Direction.Plane.HORIZONTAL) {
                  BlockPos var14 = var4.above(var10);

                  for (int var15 = 1; var15 <= var11; var15++) {
                     var14 = var4.offset(var13.getStepX() * var15, var10, var13.getStepZ() * var15);
                     if (var2.getBlockState(var14).isAir()) {
                        var2.setBlock(var14, var6, 2);
                     }
                  }

                  disc(var2, var14.above(), 1, var7, var3, 0.2F);
                  PrimordialFeatures.leaf(var2, var14.relative(var13), var7);
               }

               var9++;
            }

            return true;
         } else {
            return false;
         }
      }

      private static void disc(WorldGenLevel var0, BlockPos var1, int var2, BlockState var3, RandomSource var4, float var5) {
         for (int var6 = -var2; var6 <= var2; var6++) {
            for (int var7 = -var2; var7 <= var2; var7++) {
               if (var6 * var6 + var7 * var7 <= var2 * var2 + 1
                  && (!(var5 > 0.0F) || var6 * var6 + var7 * var7 <= (var2 - 1) * (var2 - 1) || !(var4.nextFloat() < var5))) {
                  PrimordialFeatures.leaf(var0, var1.offset(var6, 0, var7), var3);
               }
            }
         }
      }
   }

   public static final class Cicada implements Feature {
      public static final com.mojang.serialization.MapCodec<Cicada> CODEC = com.mojang.serialization.MapCodec.unit(Cicada::new);

      public com.mojang.serialization.MapCodec<Cicada> codec() {
         return CODEC;
      }

      public Cicada() {
      }

      public boolean place(net.minecraft.world.level.WorldGenLevel var1Level, net.minecraft.world.level.chunk.ChunkGenerator var1Gen, net.minecraft.util.RandomSource var1Random, net.minecraft.core.BlockPos var1Origin) {
         WorldGenLevel var2 = var1Level;
         RandomSource var3 = var1Random;
         BlockPos var4 = var1Origin;
         int var5 = 6 + var3.nextInt(7);
         if (PrimordialFeatures.soil(var2, var4) && PrimordialFeatures.clear(var2, var4, var5 + 3)) {
            BlockState var6 = (BlockState)Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
            BlockState var7 = (BlockState)Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
            Direction var8 = Direction.Plane.HORIZONTAL.getRandomDirection(var3);
            BlockPos var9 = var4;

            for (int var10 = 0; var10 < var5; var10++) {
               if (var10 == var5 / 2 && var3.nextBoolean()) {
                  var9 = var9.relative(var8);
               }

               var2.setBlock(var9, var6, 2);
               var9 = var9.above();
            }

            PrimordialFeatures.leaf(var2, var9, var7);
            int[][] var17 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

            for (int[] var14 : var17) {
               if (var14[0] == 0 || var14[1] == 0 || var3.nextInt(3) != 0) {
                  int var15 = 3 + var3.nextInt(3);

                  for (int var16 = 1; var16 <= var15; var16++) {
                     PrimordialFeatures.leaf(var2, var9.offset(var14[0] * var16, -(var16 / 2), var14[1] * var16), var7);
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static final class CrystalColumn implements Feature {
      public static final com.mojang.serialization.MapCodec<CrystalColumn> CODEC = com.mojang.serialization.MapCodec.unit(CrystalColumn::new);

      public com.mojang.serialization.MapCodec<CrystalColumn> codec() {
         return CODEC;
      }

      public CrystalColumn() {
      }

      public boolean place(net.minecraft.world.level.WorldGenLevel var1Level, net.minecraft.world.level.chunk.ChunkGenerator var1Gen, net.minecraft.util.RandomSource var1Random, net.minecraft.core.BlockPos var1Origin) {
         WorldGenLevel var2 = var1Level;
         BlockPos var3 = var1Origin;
         int var4 = 0;

         while (var4 < 120 && var2.getBlockState(var3.above(var4)).isAir()) {
            var4++;
         }

         if (var4 >= 20 && var4 < 120) {
            BlockState var5 = PrimordialFeatures.block("cristal_turquesa");

            for (int var6 = -1; var6 <= var4; var6++) {
               for (int var7 = 0; var7 <= 1; var7++) {
                  for (int var8 = 0; var8 <= 1; var8++) {
                     BlockPos var9 = var3.offset(var7, var6, var8);
                     Block var10 = var2.getBlockState(var9).getBlock();
                     if (var2.getBlockState(var9).isAir() || var10 == PrimordialFeatures.block("piedra_prehistorica").getBlock()) {
                        var2.setBlock(var9, var5, 2);
                     }
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
