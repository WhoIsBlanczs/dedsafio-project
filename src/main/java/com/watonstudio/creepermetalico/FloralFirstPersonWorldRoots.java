package com.watonstudio.creepermetalico;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class FloralFirstPersonWorldRoots {
   private static final int FULL_BRIGHT = 15728880;
   private static final int RING_SEGMENTS = 20;
   private static final BlockState ROOTS = Blocks.MANGROVE_ROOTS.defaultBlockState();
   private static final BlockState FLOWER = Blocks.PINK_TULIP.defaultBlockState();
   private static final BlockState VINE = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.NORTH, true))
            .setValue(VineBlock.SOUTH, true))
         .setValue(VineBlock.EAST, true))
      .setValue(VineBlock.WEST, true);
   private static final Map<Integer, Integer> VISUAL_STAGES = new ConcurrentHashMap<>();

   private FloralFirstPersonWorldRoots() {
   }

   public static void register() {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static void setVisualState(int playerId, int stage, boolean active) {
      if (active) {
         VISUAL_STAGES.put(playerId, Math.max(0, Math.min(5, stage)));
      } else {
         VISUAL_STAGES.remove(playerId);
      }
   }

   public static boolean hasVisual(int playerId) {
      return VISUAL_STAGES.containsKey(playerId);
   }

   public static int getVisualStage(int playerId) {
      return VISUAL_STAGES.getOrDefault(playerId, 0);
   }

   public static void clearVisuals() {
      VISUAL_STAGES.clear();
   }

   private static void render(Object context) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void renderBodyRings(
      PoseStack matrices, Object consumers, Object blocks, double px, double py, double pz, int stage
   ) {
      float[] heights = new float[]{0.16F, 0.42F, 0.68F, 0.94F, 1.2F, 1.48F};
      float[] radii = new float[]{0.35F, 0.37F, 0.39F, 0.4F, 0.42F, 0.45F};

      for (int level = stage; level < heights.length; level++) {
         float y = heights[level];
         float radius = radii[level];
         float twist = level * 0.13F;

         for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2) * i / 20.0 + twist;
            float x = (float)(Math.cos(angle) * radius);
            float z = (float)(Math.sin(angle) * radius);
            float tangent = (float)Math.toDegrees(angle) + 90.0F;
            drawBlock(matrices, consumers, blocks, ROOTS, px + x, py + y, pz + z, 0.205F, 0.135F, 0.17F, tangent, (float)(Math.sin(angle * 3.0) * 8.0));
            if (i % 2 == 0) {
               float vx = (float)(Math.cos(angle) * (radius + 0.01F));
               float vz = (float)(Math.sin(angle) * (radius + 0.01F));
               drawBlock(matrices, consumers, blocks, VINE, px + vx, py + y + 0.045F, pz + vz, 0.115F, 0.18F, 0.12F, tangent, 12.0F);
            }

            if (i % 5 == 0) {
               float fx = (float)(Math.cos(angle) * (radius + 0.045F));
               float fz = (float)(Math.sin(angle) * (radius + 0.045F));
               drawBlock(
                  matrices, consumers, blocks, FLOWER, px + fx, py + y + 0.075F, pz + fz, 0.205F, 0.205F, 0.205F, tangent, (float)(Math.sin(angle) * 12.0)
               );
            }
         }
      }
   }

   private static void drawBlock(
      PoseStack matrices,
      Object consumers,
      Object blocks,
      BlockState state,
      double x,
      double y,
      double z,
      float sx,
      float sy,
      float sz,
      float rotateY,
      float rotateZ
   ) {
      // efecto de dibujo pendiente de portar a 26.3
   }
}
