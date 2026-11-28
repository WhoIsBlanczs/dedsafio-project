package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.PodPhoraLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PodLaserRenderer extends EntityRenderer<PodPhoraLaserEntity> {
   private static final ResourceLocation TEXTURE = Ghostsafio.id("textures/entity/pod_laser.png");
   private static final int FULL_BRIGHT = 15728880;

   public PodLaserRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(PodPhoraLaserEntity entity) {
      return TEXTURE;
   }

   public void render(PodPhoraLaserEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
      poseStack.pushPose();
      poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
      VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
      Matrix4f m = poseStack.last().pose();
      float pulse = 1.0F + 0.12F * (float)Math.sin((entity.tickCount + partialTick) * 1.9F);
      quad(vc, m, 0.42F * pulse, 90);
      quad(vc, m, 0.2F * pulse, 255);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
   }

   private static void quad(VertexConsumer vc, Matrix4f m, float s, int alpha) {
      vc.addVertex(m, -s, -s, 0.0F)
         .setColor(255, 255, 255, alpha)
         .setUv(0.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(m, s, -s, 0.0F)
         .setColor(255, 255, 255, alpha)
         .setUv(1.0F, 1.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(m, s, s, 0.0F)
         .setColor(255, 255, 255, alpha)
         .setUv(1.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
      vc.addVertex(m, -s, s, 0.0F)
         .setColor(255, 255, 255, alpha)
         .setUv(0.0F, 0.0F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 0.0F, 1.0F);
   }
}
