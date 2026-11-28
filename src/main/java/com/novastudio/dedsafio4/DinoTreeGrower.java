package com.novastudio.dedsafio4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.core.registries.BuiltInRegistries;

public final class DinoTreeGrower {
   private static final int[][] WOOD;
   private static final int[][] LEAVES;
   private static final int STRETCH_LAYER;
   private static final int CROWN_START = 7;
   private static final int TOP_BAND = 8;
   private static final int MAX_TREE_BLOCKS = 600;

   private DinoTreeGrower() {
   }

   private static int[][] points(JsonArray var0) {
      int[][] var1 = new int[var0.size()][3];

      for (int var2 = 0; var2 < var1.length; var2++) {
         for (int var3 = 0; var3 < 3; var3++) {
            var1[var2][var3] = var0.get(var2).getAsJsonArray().get(var3).getAsInt();
         }
      }

      return var1;
   }

   private static List<BlockPos> place(BlockPos var0, int[][] var1, int var2, int var3) {
      ArrayList<BlockPos> var4 = new ArrayList<>();

      for (int[] var8 : var1) {
         int var9 = var8[1] == STRETCH_LAYER ? var2 + 1 : 1;

         for (int var10 = 0; var10 < var9; var10++) {
            int var11 = var8[1] + (var8[1] > STRETCH_LAYER ? var2 : var10);
            int var12 = var8[0];
            int var13 = var8[2];

            for (int var14 = 0; var14 < var3; var14++) {
               int var15 = var12;
               var12 = -var13;
               var13 = var15;
            }

            var4.add(var0.offset(var12, var11, var13));
         }
      }

      return var4;
   }

   private static boolean free(LevelAccessor var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.isAir() || var2.canBeReplaced() || var2.is(BlockTags.LEAVES) || var2.is(BlockTags.SAPLINGS);
   }

   public static void grow(ServerLevel var0, RandomSource var1, BlockPos var2, boolean var3) {
      generate(var0, var1, var2, var3, 3);
   }

   public static boolean generate(LevelAccessor var0, RandomSource var1, BlockPos var2, boolean var3, int var4) {
      int var5 = var1.nextInt(4);
      int var6 = var1.nextInt(4);
      List<BlockPos> var7 = place(var2, WOOD, var5, var6);

      for (BlockPos var9 : var7) {
         if (var9.getY() - var2.getY() >= 1 && var9.getY() - var2.getY() < 7 + var5 && !free(var0, var9)) {
            return false;
         }
      }

      DinoTreeTrunkBlock var15 = (DinoTreeTrunkBlock)block(var3 ? "tronco_abedul_dino" : "tronco_roble_quiu_dino");
      BlockState var16 = (BlockState)(var3 ? Blocks.BIRCH_LEAVES : Blocks.OAK_LEAVES).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
      int var10 = var1.nextInt(DinoFruitRender.FRUITS.length);
      var0.setBlock(var2, var15.fruitBearingState(var15.defaultBlockState(), var10, 0), var4);
      ArrayList<BlockPos> var11 = new ArrayList<>();
      var11.add(var2);

      for (BlockPos var13 : var7) {
         if (!var13.equals(var2) && free(var0, var13)) {
            var0.setBlock(var13, var15.defaultBlockState(), var4);
            var11.add(var13);
         }
      }

      for (BlockPos var19 : place(var2, LEAVES, var5, var6)) {
         if (var0.getBlockState(var19).isAir()) {
            var0.setBlock(var19, var16, 2);
         }
      }

      Block var18 = block(DinoFruitRender.FRUITS[var10] + "_tree_fruit");
      List<BlockPos> var20 = fruitSpots(var0, topTrunk(var11));
      Collections.shuffle(var20, new Random(var1.nextLong()));

      for (int var14 = 0; var14 < Math.min(5 + var1.nextInt(2), var20.size()); var14++) {
         var0.setBlock(var20.get(var14), var18.defaultBlockState(), var4);
      }

      return true;
   }

   static List<BlockPos> tree(LevelAccessor var0, BlockPos var1) {
      ArrayList<BlockPos> var2 = new ArrayList<>();
      HashSet<BlockPos> var3 = new HashSet<>();
      ArrayDeque<BlockPos> var4 = new ArrayDeque<>();
      var4.add(var1.immutable());

      while (!var4.isEmpty() && var2.size() < 600) {
         BlockPos var5 = var4.poll();
         if (var3.add(var5) && var0.getBlockState(var5).getBlock() instanceof DinoTreeTrunkBlock) {
            var2.add(var5);

            for (int var6 = -1; var6 <= 1; var6++) {
               for (int var7 = -1; var7 <= 1; var7++) {
                  for (int var8 = -1; var8 <= 1; var8++) {
                     if (var6 != 0 || var7 != 0 || var8 != 0) {
                        var4.add(var5.offset(var6, var7, var8));
                     }
                  }
               }
            }
         }
      }

      return var2;
   }

   static List<BlockPos> topTrunk(List<BlockPos> var0) {
      int var1 = Integer.MIN_VALUE;

      for (BlockPos var3 : var0) {
         var1 = Math.max(var1, var3.getY());
      }

      ArrayList<BlockPos> var5 = new ArrayList<>();

      for (BlockPos var4 : var0) {
         if (var4.getY() > var1 - 8) {
            var5.add(var4);
         }
      }

      return var5;
   }

   static List<BlockPos> fruitSpots(LevelAccessor var0, List<BlockPos> var1) {
      LinkedHashSet var2 = new LinkedHashSet();

      for (BlockPos var4 : var1) {
         for (int[] var8 : DinoSaplingBlock.FRUIT_SIDES) {
            BlockPos var9 = var4.offset(var8[0], 0, var8[2]);
            BlockState var10 = var0.getBlockState(var9);
            if (var10.isAir() || var10.is(BlockTags.LEAVES)) {
               var2.add(var9);
            }
         }
      }

      return new ArrayList<>(var2);
   }

   static int fruitCount(LevelAccessor var0, List<BlockPos> var1) {
      HashSet<BlockPos> var2 = new HashSet<>();

      for (BlockPos var4 : var1) {
         for (int[] var8 : DinoSaplingBlock.FRUIT_SIDES) {
            BlockPos var9 = var4.offset(var8[0], 0, var8[2]);
            if (var0.getBlockState(var9).getBlock() instanceof DinoFruitBlock) {
               var2.add(var9);
            }
         }
      }

      return var2.size();
   }

   private static Block block(String var0) {
      return (Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", var0));
   }

   static {
      try {
         try (InputStream var0 = DinoTreeGrower.class.getResourceAsStream("/data/nexo/dino_tree.json")) {
            JsonObject var1 = JsonParser.parseReader(new InputStreamReader(var0, StandardCharsets.UTF_8)).getAsJsonObject();
            WOOD = points(var1.getAsJsonArray("wood"));
            LEAVES = points(var1.getAsJsonArray("leaves"));
            STRETCH_LAYER = var1.get("stretch_layer").getAsInt();
         }
      } catch (Exception var51) {
         throw new IllegalStateException("[Dedsafio4] dino_tree.json missing or broken", var51);
      }
   }
}
