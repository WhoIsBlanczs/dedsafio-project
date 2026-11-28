package com.watonstudio.creepermetalico;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import com.watonstudio.creepermetalico.client.compat.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public final class FloralRootsFeatureRenderer extends RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
   private static final BlockState WOOD_ROOT = Blocks.MANGROVE_ROOTS.defaultBlockState();
   private static final BlockState VINE = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.NORTH, true))
            .setValue(VineBlock.EAST, true))
         .setValue(VineBlock.SOUTH, true))
      .setValue(VineBlock.WEST, true);
   private static final BlockState PINK_FLOWER = Blocks.PINK_TULIP.defaultBlockState();

   public FloralRootsFeatureRenderer(RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> context) {
      super(context);
   }

   public void submit(PoseStack matrices, net.minecraft.client.renderer.SubmitNodeCollector colector, int light, net.minecraft.client.renderer.entity.state.AvatarRenderState estado, float yRot, float xRot) {
      if (!(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(estado.id) instanceof AbstractClientPlayer player)) {
         return;
      }
      com.watonstudio.creepermetalico.client.compat.Grabadora vertexConsumers = new com.watonstudio.creepermetalico.client.compat.Grabadora(colector);
      if (CreeperQteClient.shouldRenderRoots(player)) {
         int stage = CreeperQteClient.getRootStage(player);
         Object blocks = null;
         PlayerModel model = this.getParentModel();
         if (stage <= 0) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.head, -0.13F, 0.06F, -0.08F, 0.31F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, -0.28F, -0.62F, -0.17F, 0.43F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, 0.12F, -0.59F, -0.17F, 0.42F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, -0.04F, -0.38F, -0.19F, 0.41F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, 0.19F, -0.48F, -0.19F, 0.38F);
         }

         if (stage <= 1) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, -0.21F, -0.19F, -0.18F, 0.39F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, 0.15F, -0.13F, -0.19F, 0.38F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, -0.08F, 0.02F, -0.18F, 0.38F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.body, 0.12F, 0.15F, -0.18F, 0.35F);
         }

         if (stage <= 2) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightArm, -0.15F, -0.25F, -0.07F, 0.38F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightArm, -0.07F, 0.02F, -0.07F, 0.35F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightArm, 0.06F, 0.2F, -0.06F, 0.31F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightArm, -0.1F, 0.37F, -0.07F, 0.29F);
         }

         if (stage <= 3) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftArm, -0.14F, -0.24F, -0.07F, 0.38F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftArm, -0.07F, 0.03F, -0.07F, 0.35F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftArm, 0.05F, 0.2F, -0.06F, 0.31F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftArm, -0.1F, 0.37F, -0.07F, 0.29F);
         }

         if (stage <= 4) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightLeg, -0.1F, 0.02F, -0.06F, 0.34F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightLeg, 0.05F, 0.16F, -0.06F, 0.31F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.rightLeg, -0.04F, 0.3F, -0.05F, 0.29F);
         }

         if (stage <= 5) {
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftLeg, -0.1F, 0.02F, -0.06F, 0.34F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftLeg, 0.05F, 0.16F, -0.06F, 0.31F);
            drawRootCluster(matrices, vertexConsumers, blocks, light, model.leftLeg, -0.04F, 0.3F, -0.05F, 0.29F);
         }
      }
   }

   private static void drawRootCluster(
      PoseStack matrices, MultiBufferSource vertices, Object blocks, int light, ModelPart part, float x, float y, float z, float scale
   ) {
      drawWoodRoot(matrices, vertices, blocks, light, part, x, y, z, scale * 0.22F, scale * 0.92F, scale * 0.22F, -12.0F, 14.0F);
      drawWoodRoot(
         matrices, vertices, blocks, light, part, x + scale * 0.12F, y + scale * 0.18F, z - 0.012F, scale * 0.15F, scale * 0.65F, scale * 0.15F, 39.0F, 18.0F
      );
      drawWoodRoot(
         matrices, vertices, blocks, light, part, x - scale * 0.12F, y - scale * 0.16F, z + 0.012F, scale * 0.15F, scale * 0.59F, scale * 0.15F, -43.0F, -17.0F
      );
      drawSmallBlock(matrices, vertices, blocks, light, part, VINE, x + scale * 0.13F, y + scale * 0.22F, z - 0.025F, scale * 0.27F);
      drawSmallBlock(matrices, vertices, blocks, light, part, PINK_FLOWER, x + scale * 0.19F, y + scale * 0.3F, z - 0.045F, scale * 0.36F);
      drawSmallBlock(matrices, vertices, blocks, light, part, PINK_FLOWER, x - scale * 0.16F, y - scale * 0.11F, z - 0.05F, scale * 0.3F);
   }

   private static void drawWoodRoot(
      PoseStack matrices,
      MultiBufferSource vertices,
      Object blocks,
      int light,
      ModelPart part,
      float x,
      float y,
      float z,
      float sx,
      float sy,
      float sz,
      float rotateZ,
      float rotateX
   ) {
      matrices.pushPose();
      part.translateAndRotate(matrices);
      matrices.translate(x, y, z);
      matrices.rotate(Axis.ZP.rotationDegrees(rotateZ));
      matrices.rotate(Axis.XP.rotationDegrees(rotateX));
      matrices.scale(sx, sy, sz);
      com.watonstudio.creepermetalico.client.compat.Grabadora.bloque(vertices, WOOD_ROOT, matrices, light, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
   }

   private static void drawSmallBlock(
      PoseStack matrices,
      MultiBufferSource vertices,
      Object blocks,
      int light,
      ModelPart part,
      BlockState state,
      float x,
      float y,
      float z,
      float scale
   ) {
      matrices.pushPose();
      part.translateAndRotate(matrices);
      matrices.translate(x, y, z);
      matrices.scale(scale, scale, scale);
      com.watonstudio.creepermetalico.client.compat.Grabadora.bloque(vertices, state, matrices, light, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
   }

   public static void renderFirstPersonArmRoots(PoseStack matrices, MultiBufferSource vertices, int light, AbstractClientPlayer player, ModelPart arm) {
      if (CreeperQteClient.shouldRenderRoots(player)) {
         int stage = CreeperQteClient.getRootStage(player);
         Object blocks = null;
         matrices.pushPose();
         arm.translateAndRotate(matrices);
         if (stage <= 0) {
            drawArmRootBlock(matrices, vertices, blocks, light, -0.12F, 0.0F, -0.17F, 0.15F, 0.28F, 0.14F, -24.0F, 8.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, -0.02F, 0.15F, -0.19F, 0.24F, 0.09F, 0.13F, 32.0F, -12.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.08F, 0.1F, -0.21F, 0.14F);
            drawArmFlower(matrices, vertices, blocks, light, -0.02F, 0.18F, -0.25F, 0.14F);
            drawArmFlower(matrices, vertices, blocks, light, -0.15F, 0.04F, -0.24F, 0.11F);
         }

         if (stage <= 1) {
            drawArmRootBlock(matrices, vertices, blocks, light, 0.02F, 0.12F, -0.18F, 0.13F, 0.25F, 0.12F, 18.0F, -10.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, -0.13F, 0.28F, -0.2F, 0.22F, 0.08F, 0.12F, -38.0F, 12.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.02F, 0.28F, -0.22F, 0.13F);
            drawArmFlower(matrices, vertices, blocks, light, 0.05F, 0.3F, -0.25F, 0.13F);
            drawArmFlower(matrices, vertices, blocks, light, -0.16F, 0.23F, -0.24F, 0.1F);
         }

         if (stage <= 2) {
            drawArmRootBlock(matrices, vertices, blocks, light, -0.03F, 0.26F, -0.18F, 0.15F, 0.24F, 0.13F, -18.0F, 9.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, 0.02F, 0.38F, -0.2F, 0.22F, 0.08F, 0.12F, 42.0F, -10.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.11F, 0.38F, -0.22F, 0.12F);
            drawArmFlower(matrices, vertices, blocks, light, 0.1F, 0.4F, -0.25F, 0.12F);
            drawArmFlower(matrices, vertices, blocks, light, -0.13F, 0.35F, -0.24F, 0.1F);
         }

         if (stage <= 3) {
            drawArmRootBlock(matrices, vertices, blocks, light, -0.1F, 0.39F, -0.18F, 0.14F, 0.23F, 0.13F, 24.0F, -7.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, 0.01F, 0.48F, -0.2F, 0.2F, 0.08F, 0.11F, -35.0F, 11.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.04F, 0.48F, -0.22F, 0.12F);
            drawArmFlower(matrices, vertices, blocks, light, 0.08F, 0.5F, -0.25F, 0.12F);
            drawArmFlower(matrices, vertices, blocks, light, -0.15F, 0.45F, -0.24F, 0.1F);
         }

         if (stage <= 4) {
            drawArmRootBlock(matrices, vertices, blocks, light, 0.0F, 0.5F, -0.18F, 0.13F, 0.2F, 0.12F, -20.0F, 8.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, -0.1F, 0.57F, -0.2F, 0.18F, 0.07F, 0.11F, 36.0F, -8.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.04F, 0.58F, -0.22F, 0.11F);
            drawArmFlower(matrices, vertices, blocks, light, 0.05F, 0.59F, -0.25F, 0.11F);
            drawArmFlower(matrices, vertices, blocks, light, -0.14F, 0.55F, -0.24F, 0.09F);
         }

         if (stage <= 5) {
            drawArmRootBlock(matrices, vertices, blocks, light, -0.04F, 0.61F, -0.18F, 0.12F, 0.15F, 0.12F, 20.0F, 7.0F);
            drawArmRootBlock(matrices, vertices, blocks, light, -0.1F, 0.66F, -0.2F, 0.17F, 0.07F, 0.11F, -32.0F, 9.0F);
            drawArmVine(matrices, vertices, blocks, light, -0.05F, 0.66F, -0.22F, 0.1F);
            drawArmFlower(matrices, vertices, blocks, light, 0.04F, 0.67F, -0.25F, 0.11F);
            drawArmFlower(matrices, vertices, blocks, light, -0.14F, 0.64F, -0.24F, 0.09F);
         }

         matrices.popPose();
      }
   }

   private static void drawArmRootBlock(
      PoseStack matrices,
      MultiBufferSource vertices,
      Object blocks,
      int light,
      float x,
      float y,
      float z,
      float sx,
      float sy,
      float sz,
      float rotateZ,
      float rotateX
   ) {
      matrices.pushPose();
      matrices.translate(x, y, z);
      matrices.rotate(Axis.ZP.rotationDegrees(rotateZ));
      matrices.rotate(Axis.XP.rotationDegrees(rotateX));
      matrices.scale(sx, sy, sz);
      com.watonstudio.creepermetalico.client.compat.Grabadora.bloque(vertices, WOOD_ROOT, matrices, light, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
   }

   private static void drawArmVine(
      PoseStack matrices, MultiBufferSource vertices, Object blocks, int light, float x, float y, float z, float scale
   ) {
      matrices.pushPose();
      matrices.translate(x, y, z);
      matrices.rotate(Axis.ZP.rotationDegrees(28.0F));
      matrices.scale(scale, scale * 1.7F, scale);
      com.watonstudio.creepermetalico.client.compat.Grabadora.bloque(vertices, VINE, matrices, light, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
   }

   private static void drawArmFlower(
      PoseStack matrices, MultiBufferSource vertices, Object blocks, int light, float x, float y, float z, float scale
   ) {
      matrices.pushPose();
      matrices.translate(x, y, z);
      matrices.rotate(Axis.YP.rotationDegrees(24.0F));
      matrices.rotate(Axis.ZP.rotationDegrees(-12.0F));
      matrices.scale(scale, scale, scale);
      com.watonstudio.creepermetalico.client.compat.Grabadora.bloque(vertices, PINK_FLOWER, matrices, light, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
   }
}
