package net.ruleta.client.cosmeticos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CosmeticosLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   private static final float HALF_PI = (float) (Math.PI / 2);
   private static final float[][] BANDERAS = new float[][]{
      {-0.03F, 0.85F, 0.014F, 0.0F, 0.27F, 0.031F},
      {-0.03F, 0.85F, 0.014F, (float) (Math.PI / 2), 0.27F, 0.026F},
      {-0.03F, 0.85F, 0.014F, (float) Math.PI, 0.27F, 0.034F},
      {-0.03F, 0.85F, 0.014F, (float) (Math.PI * 3.0 / 2.0), 0.27F, 0.028F}
   };
   private static final int COLUMNAS = 12;

   private static ResourceLocation tex(String n) {
      return ResourceLocation.fromNamespaceAndPath("ruleta", "textures/entity/cosmeticos/" + n + ".png");
   }

   public CosmeticosLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> padre) {
      super(padre);
   }

   public void render(
      PoseStack pila,
      MultiBufferSource buf,
      int luz,
      AbstractClientPlayer p,
      float limb,
      float limbAmt,
      float parcial,
      float edad,
      float yawCab,
      float pitchCab
   ) {
      CosmeticosCliente.Estado e = CosmeticosCliente.ESTADOS.get(p.getUUID());
      if (e != null && !p.isInvisible() && !p.isSpectator()) {
         float t = edad;
         pila.pushPose();
         if (!e.aura.isEmpty()) {
            this.bandera(pila, buf, luz, e.aura, t, limbAmt);
         }

         pila.popPose();
      }
   }

   private static void v(
      VertexConsumer vc, Pose pose, int luz, float x, float y, float z, float u, float w, float r, float g, float b, float a, float nx, float ny, float nz
   ) {
      vc.addVertex(pose.pose(), x, y, z).setColor(r, g, b, a).setUv(u, w).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(pose, nx, ny, nz);
   }

   private static void quad(
      VertexConsumer vc,
      Pose pose,
      int luz,
      float x0,
      float y0,
      float x1,
      float y1,
      float z,
      float u0,
      float v0,
      float u1,
      float v1,
      float r,
      float g,
      float b,
      float a
   ) {
      v(vc, pose, luz, x0, y0, z, u0, v0, r, g, b, a, 0.0F, 0.0F, 1.0F);
      v(vc, pose, luz, x1, y0, z, u1, v0, r, g, b, a, 0.0F, 0.0F, 1.0F);
      v(vc, pose, luz, x1, y1, z, u1, v1, r, g, b, a, 0.0F, 0.0F, 1.0F);
      v(vc, pose, luz, x0, y1, z, u0, v1, r, g, b, a, 0.0F, 0.0F, 1.0F);
   }

   private void bandera(PoseStack pila, MultiBufferSource buf, int luz, String pais, float t, float movimiento) {
      ResourceLocation tex = tex("bandera_" + pais);
      VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(tex));
      float mov = 0.0F;

      for (int i = 0; i < BANDERAS.length; i++) {
         float[] b = BANDERAS[i];
         float ang = t * b[2] + b[3];
         float y = b[0] + b[4] * (float)Math.sin(t * b[5] + i * 1.9F);
         pila.pushPose();
         pila.translate(Math.cos(ang) * b[1], y, Math.sin(ang) * b[1]);
         pila.mulPose(Axis.YP.rotation(-ang + (float) (Math.PI / 2) + 0.45F * (float)Math.sin(t * 0.03F + b[3] * 1.7F)));
         pila.mulPose(Axis.ZP.rotationDegrees(22.0F * (float)Math.sin(t * 0.055F + b[3] * 1.3F)));
         pila.mulPose(Axis.XP.rotationDegrees(14.0F * (float)Math.sin(t * 0.047F + b[3] * 2.1F)));
         ondeada(vc, pila.last(), t + b[3] * 9.0F, mov);
         pila.popPose();
      }
   }

   private static void ondeada(VertexConsumer vc, Pose pose, float t, float mov) {
      float hw = 0.18F;
      float hh = 0.135F;
      float amp = 0.012F + 0.075F * mov;
      float vel = 0.16F + 0.2F * mov;

      for (int i = 0; i < 12; i++) {
         float f0 = i / 12.0F;
         float f1 = (i + 1) / 12.0F;
         float z0 = onda(f0, t, amp, vel);
         float z1 = onda(f1, t, amp, vel);
         float x0 = -hw + 2.0F * hw * f0;
         float x1 = -hw + 2.0F * hw * f1;
         float s0 = 0.86F + 0.14F * (float)Math.cos(f0 * 11.0F - t * vel * 2.0F);
         float s1 = 0.86F + 0.14F * (float)Math.cos(f1 * 11.0F - t * vel * 2.0F);
         v(vc, pose, 15728880, x0, -hh, z0, f0, 1.0F, s0, s0, s0, 1.0F, 0.0F, 0.0F, 1.0F);
         v(vc, pose, 15728880, x1, -hh, z1, f1, 1.0F, s1, s1, s1, 1.0F, 0.0F, 0.0F, 1.0F);
         v(vc, pose, 15728880, x1, hh, z1, f1, 0.0F, s1, s1, s1, 1.0F, 0.0F, 0.0F, 1.0F);
         v(vc, pose, 15728880, x0, hh, z0, f0, 0.0F, s0, s0, s0, 1.0F, 0.0F, 0.0F, 1.0F);
      }
   }

   private static float onda(float f, float t, float amp, float vel) {
      return amp * f * (float)Math.sin(f * 7.0F - t * vel * 2.0F + 0.5 * Math.sin(t * 0.05F));
   }
}
