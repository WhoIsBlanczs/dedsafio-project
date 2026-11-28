package com.novastudio.dedsafio4;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.registries.BuiltInRegistries;

public final class DinoFruitShapes {
   private static final Direction[] SIDES = new Direction[]{Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST};
   private static final Map<Block, DinoFruitShapes.Pose> POSES = new ConcurrentHashMap<>();
   private static final Map<String, VoxelShape> SHAPES = new ConcurrentHashMap<>();

   private DinoFruitShapes() {
   }

   public static Direction trunkSide(BlockGetter var0, BlockPos var1) {
      for (Direction var5 : SIDES) {
         if (var0.getBlockState(var1.relative(var5)).getBlock() instanceof DinoTreeTrunkBlock) {
            return var5;
         }
      }

      return Direction.SOUTH;
   }

   public static float yaw(Direction var0) {
      return var0 == Direction.EAST ? 90.0F : (var0 == Direction.NORTH ? 180.0F : (var0 == Direction.WEST ? 270.0F : 0.0F));
   }

   public static DinoFruitShapes.Pose pose(Block var0) {
      return POSES.computeIfAbsent(var0, DinoFruitShapes::load);
   }

   public static VoxelShape shape(Block var0) {
      return shape(var0, Direction.SOUTH);
   }

   public static VoxelShape shape(Block var0, BlockGetter var1, BlockPos var2) {
      return shape(var0, trunkSide(var1, var2));
   }

   private static VoxelShape shape(Block var0, Direction var1) {
      return SHAPES.computeIfAbsent(BuiltInRegistries.BLOCK.getKey(var0) + "|" + var1, var2 -> build(pose(var0), yaw(var1)));
   }

   private static VoxelShape build(DinoFruitShapes.Pose var0, float var1) {
      if (var0.cubes().isEmpty()) {
         return Shapes.block();
      } else {
         double var2 = Math.toRadians(var1);
         double var4 = Math.round(Math.cos(var2));
         double var6 = Math.round(Math.sin(var2));
         VoxelShape var8 = Shapes.empty();

         for (double[] var10 : var0.cubes()) {
            double var11 = 99.0;
            double var13 = 99.0;
            double var15 = 99.0;
            double var17 = -99.0;
            double var19 = -99.0;
            double var21 = -99.0;

            for (int var23 = 0; var23 < 8; var23++) {
               double var24 = (var23 & 1) == 0 ? var10[0] : var10[3];
               double var26 = (var23 & 2) == 0 ? var10[1] : var10[4];
               double var28 = (var23 & 4) == 0 ? var10[2] : var10[5];
               if (var0.horizontal()) {
                  double var30 = var26;
                  var26 = var28;
                  var28 = -var30;
               }

               var24 += var0.dx();
               var26 += var0.dy();
               var28 += var0.dz();
               double var37 = var24 * var4 + var28 * var6;
               double var32 = -var24 * var6 + var28 * var4;
               var11 = Math.min(var11, var37);
               var17 = Math.max(var17, var37);
               var13 = Math.min(var13, var26);
               var19 = Math.max(var19, var26);
               var15 = Math.min(var15, var32);
               var21 = Math.max(var21, var32);
            }

            var8 = Shapes.or(
               var8, Block.box(clamp(var11 + 8.0), clamp(var13), clamp(var15 + 8.0), clamp(var17 + 8.0), clamp(var19), clamp(var21 + 8.0))
            );
         }

         return var8.optimize();
      }
   }

   private static DinoFruitShapes.Pose load(Block var0) {
      String var1 = BuiltInRegistries.BLOCK.getKey(var0).getPath();
      boolean var2 = var1.startsWith("huevo_de_eburia") || var1.startsWith("fruta_solaria");
      ArrayList<double[]> var3 = new ArrayList<>();
      String var4 = "/assets/nexo/geo/block/" + var1 + ".geo.json";

      try (InputStream var5 = DinoFruitShapes.class.getResourceAsStream(var4)) {
         if (var5 != null) {
            JsonObject var6 = JsonParser.parseReader(new InputStreamReader(var5, StandardCharsets.UTF_8))
               .getAsJsonObject()
               .getAsJsonArray("minecraft:geometry")
               .get(0)
               .getAsJsonObject();

            for (JsonElement var8 : var6.getAsJsonArray("bones")) {
               if (var8.getAsJsonObject().has("cubes")) {
                  for (JsonElement var10 : var8.getAsJsonObject().getAsJsonArray("cubes")) {
                     double[] var11 = vec(var10.getAsJsonObject(), "origin");
                     double[] var12 = vec(var10.getAsJsonObject(), "size");
                     var3.add(new double[]{-var11[0] - var12[0], var11[1], var11[2], -var11[0], var11[1] + var12[1], var11[2] + var12[2]});
                  }
               }
            }
         }
      } catch (Exception var15) {
         System.err.println("[Dedsafio4] fruit model read failed for " + var4 + ": " + var15);
      }

      double var16 = 0.0;
      double var17 = 0.0;
      double var18 = 0.0;
      if (!var3.isEmpty()) {
         var17 = 99.0;
         var16 = 99.0;
         var18 = -99.0;

         for (double[] var20 : var3) {
            var16 = Math.min(var16, var20[1]);
            var17 = Math.min(var17, var20[2]);
            var18 = Math.max(var18, var20[5]);
         }
      }

      return var2
         ? new DinoFruitShapes.Pose(var3, true, 0.0, 8.0 - (var17 + var18) / 2.0, 8.0 + var16)
         : new DinoFruitShapes.Pose(var3, false, 0.0, 0.0, 8.0 - var18);
   }

   private static double clamp(double var0) {
      return Math.max(0.0, Math.min(16.0, var0));
   }

   private static double[] vec(JsonObject var0, String var1) {
      JsonArray var2 = var0.getAsJsonArray(var1);
      return new double[]{var2.get(0).getAsDouble(), var2.get(1).getAsDouble(), var2.get(2).getAsDouble()};
   }

   public record Pose(List<double[]> cubes, boolean horizontal, double dx, double dy, double dz) {
   }
}
