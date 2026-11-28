package com.novastudio.dedsafio4;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;

public final class PrehistoricTreeFeature implements Feature {
   public static final com.mojang.serialization.MapCodec<PrehistoricTreeFeature> ROBLE = com.mojang.serialization.MapCodec.unit(() -> new PrehistoricTreeFeature(false));
   public static final com.mojang.serialization.MapCodec<PrehistoricTreeFeature> ABEDUL = com.mojang.serialization.MapCodec.unit(() -> new PrehistoricTreeFeature(true));

   public com.mojang.serialization.MapCodec<PrehistoricTreeFeature> codec() {
      return this.birch ? ABEDUL : ROBLE;
   }

   private static final int CLEARANCE = 26;
   private static final int SPACING = 12;
   private final boolean birch;

   public PrehistoricTreeFeature(boolean var1) {
      this.birch = var1;
   }

   public boolean place(net.minecraft.world.level.WorldGenLevel var1Level, net.minecraft.world.level.chunk.ChunkGenerator var1Gen, net.minecraft.util.RandomSource var1Random, net.minecraft.core.BlockPos var1Origin) {
      WorldGenLevel var2 = var1Level;
      BlockPos var3 = var1Origin;
      if (!var2.getBlockState(var3.below()).is(BlockTags.DIRT)) {
         return false;
      } else {
         for (int var4 = 0; var4 < 26; var4++) {
            BlockState var5 = var2.getBlockState(var3.above(var4));
            if (!var5.isAir() && !var5.canBeReplaced()) {
               return false;
            }
         }

         for (byte var7 = -12; var7 <= 12; var7 = (byte)(var7 + 3)) {
            for (byte var8 = -12; var8 <= 12; var8 = (byte)(var8 + 3)) {
               for (byte var6 = 2; var6 <= 8; var6 = (byte)(var6 + 3)) {
                  if (var2.getBlockState(var3.offset(var7, var6, var8)).getBlock() instanceof DinoTreeTrunkBlock) {
                     return false;
                  }
               }
            }
         }

         return DinoTreeGrower.generate(var2, var1Random, var3, this.birch, 2);
      }
   }
}
