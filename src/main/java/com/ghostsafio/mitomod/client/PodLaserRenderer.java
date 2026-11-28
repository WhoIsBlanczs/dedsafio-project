package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.PodPhoraLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PodLaserRenderer extends EntityRenderer<PodPhoraLaserEntity, com.ghostsafio.mitomod.compat.Compat.EstadoEntidad> {
   private static final Identifier TEXTURE = Ghostsafio.id("textures/entity/pod_laser.png");
   private static final int FULL_BRIGHT = 15728880;

   public PodLaserRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   public Identifier getTextureLocation(PodPhoraLaserEntity entity) {
      return TEXTURE;
   }

   public com.ghostsafio.mitomod.compat.Compat.EstadoEntidad createRenderState() {
      return new com.ghostsafio.mitomod.compat.Compat.EstadoEntidad();
   }

   public void extractRenderState(PodPhoraLaserEntity entidad$, com.ghostsafio.mitomod.compat.Compat.EstadoEntidad estado$, float pt$) {
      super.extractRenderState(entidad$, estado$, pt$);
      estado$.entidad = entidad$;
      estado$.pt = pt$;
   }

   public void submit(com.ghostsafio.mitomod.compat.Compat.EstadoEntidad estado$, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      PodPhoraLaserEntity entity = (PodPhoraLaserEntity)estado$.entidad;
      float partialTick = estado$.pt;
      float entityYaw = entity.getYRot();
      int packedLight = estado$.lightCoords;
      poseStack.pushPose();
      poseStack.rotate(camara$.orientation);
      com.ghostsafio.mitomod.client.compat.Grabadora graba$ = new com.ghostsafio.mitomod.client.compat.Grabadora(colector$);
      VertexConsumer vc = graba$.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(TEXTURE));
      Matrix4f m = poseStack.last().pose();
      float pulse = 1.0F + 0.12F * (float)Math.sin((entity.tickCount + partialTick) * 1.9F);
      quad(vc, m, 0.42F * pulse, 90);
      quad(vc, m, 0.2F * pulse, 255);
      poseStack.popPose();
      graba$.enviar();
      super.submit(estado$, poseStack, colector$, camara$);
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
