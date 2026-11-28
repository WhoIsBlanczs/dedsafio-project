package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.NaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class NaveRenderer extends EntityRenderer<NaveEntity, com.ghostsafio.mitomod.compat.Compat.EstadoEntidad> {
   private static final Identifier TEXTURE = Ghostsafio.id("textures/entity/nave.png");
   private static final Identifier MODEL_ID = Ghostsafio.id("bb/nave.json");
   private static final double PIVOT_HEIGHT = 3.3F;
   public static float tiltSign = -1.0F;
   public static float rollOffset = 0.0F;
   public static float yawOffset = 0.0F;
   public static float previewTilt = -1.0F;
   private static BbModel model;
   private static boolean failed = false;
   private static BbModel.Pose pose;

   public NaveRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   protected boolean affectedByCulling(NaveEntity entity) {
      return false;
   }

   public Identifier getTextureLocation(NaveEntity entity) {
      return TEXTURE;
   }

   public com.ghostsafio.mitomod.compat.Compat.EstadoEntidad createRenderState() {
      return new com.ghostsafio.mitomod.compat.Compat.EstadoEntidad();
   }

   public void extractRenderState(NaveEntity entidad$, com.ghostsafio.mitomod.compat.Compat.EstadoEntidad estado$, float pt$) {
      super.extractRenderState(entidad$, estado$, pt$);
      estado$.entidad = entidad$;
      estado$.pt = pt$;
   }

   public void submit(com.ghostsafio.mitomod.compat.Compat.EstadoEntidad estado$, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      NaveEntity nave = (NaveEntity)estado$.entidad;
      float partialTick = estado$.pt;
      float entityYaw = nave.getYRot();
      int packedLight = estado$.lightCoords;
      if (model == null && !failed) {
         try {
            model = BbModel.load(Minecraft.getInstance().getResourceManager(), MODEL_ID);
            pose = new BbModel.Pose(model.boneCount());
         } catch (Exception e) {
            failed = true;
            Ghostsafio.LOGGER.error("No se pudo cargar el modelo de la nave", e);
         }
      }

      if (model != null) {
         pose.reset();
         boolean open = nave.isDoorOpen();
         float doorTime = (nave.doorAnimTicks + partialTick) / 20.0F;
         BbModel.Clip doorClip = model.clip(open ? "abrir" : "cerrar");
         if (doorClip != null && doorTime < doorClip.length()) {
            model.applyClip(doorClip, doorTime, pose);
         } else {
            model.applyClip(model.clip(open ? "abierta" : "cerrada"), 0.0F, pose);
         }

         int engine = nave.getEngine();
         float time = (nave.tickCount + partialTick) / 20.0F;
         if (engine == 0) {
            int fire = model.indexOf("fuego");
            if (fire >= 0) {
               pose.scl[fire][0] = 0.0F;
               pose.scl[fire][1] = 0.0F;
               pose.scl[fire][2] = 0.0F;
            }
         } else {
            model.applyClip(model.clip(engine == 2 ? "subir" : "idle"), time, pose);
         }

         poseStack.pushPose();
         float shake = OrbitFx.shake(nave);
         if (shake > 0.01F) {
            float st = nave.tickCount + partialTick;
            poseStack.translate(
               (Mth.sin(st * 3.7F) + Mth.sin(st * 6.1F)) * 0.035 * shake,
               (Mth.sin(st * 4.3F) + Mth.sin(st * 7.7F)) * 0.025 * shake,
               (Mth.sin(st * 5.1F) + Mth.sin(st * 8.3F)) * 0.035 * shake
            );
         }

         poseStack.rotate(Axis.YP.rotationDegrees(180.0F - entityYaw + yawOffset));
         poseStack.scale(0.7F, 0.7F, 0.7F);
         float tilt = previewTilt >= 0.0F ? previewTilt : Mth.lerp(partialTick, nave.tiltO, nave.tiltClient);
         if (tilt > 0.01F) {
            poseStack.translate(0.0, 3.3F, 0.0);
            poseStack.rotate(Axis.XP.rotationDegrees(tiltSign * tilt));
            poseStack.translate(0.0, -3.3F, 0.0);
         }

         float roll = Mth.lerp(partialTick, nave.rollO, nave.rollClient) + rollOffset;
         if (Math.abs(roll) > 0.01F) {
            poseStack.rotate(Axis.YP.rotationDegrees(roll));
         }

         com.ghostsafio.mitomod.client.compat.Grabadora graba$ = new com.ghostsafio.mitomod.client.compat.Grabadora(colector$);
      VertexConsumer vc = graba$.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.entityCutout(TEXTURE));
         model.render(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, pose, !nave.isVehicle());
         poseStack.popPose();
         graba$.enviar();
      }
   }
}
