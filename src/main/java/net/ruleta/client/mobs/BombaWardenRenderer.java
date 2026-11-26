package net.ruleta.client.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.mobs.BombaWarden;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BombaWardenRenderer extends EntityRenderer<BombaWarden> {
   private static final Identifier WARDEN = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden.png");
   private static final Identifier BRILLO = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/warden/warden_bioluminescent_layer.png");
   private static final float TEX = 128.0F;
   private static int alfa = 255;

   public BombaWardenRenderer(Context ctx) {
      super(ctx);
      this.shadowRadius = 0.25F;
   }

   public Identifier getTextureLocation(BombaWarden e) {
      return WARDEN;
   }

   public boolean shouldRender(BombaWarden e, Frustum frustum, double x, double y, double z) {
      return frustum.isVisible(e.getBoundingBox().inflate(1.0, 0.8, 1.0));
   }

   public void render(BombaWarden e, float yaw, float pt, PoseStack ps, MultiBufferSource buf, int luz) {
      float cuerpo = Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot);
      float edad = e.tickCount + pt;
      boolean alerta = e.enAlerta();
      Vec3 mov = e.getDeltaMovement();
      double horiz = Math.sqrt(mov.x * mov.x + mov.z * mov.z);
      float frec = alerta ? 1.35F : 0.55F;
      float aleteo = (float)Math.sin(edad * frec) * (alerta ? 40.0F : 28.0F) + (alerta ? 8.0F : 4.0F);
      float inclina = Mth.clamp((float)horiz * 70.0F, 0.0F, 42.0F);
      LocalPlayer jugador = Minecraft.getInstance().player;
      double dist = jugador == null ? 99.0 : Math.sqrt(e.distanceToSqr(jugador));
      float cerca = (float)Mth.clamp((10.0 - dist) / 10.0, 0.0, 1.0);
      float latido = (float)(Math.sin(edad * (0.5F + cerca * 1.4F)) * 0.5 + 0.5);
      float escala = 1.0F + cerca * 0.12F * latido;
      ps.pushPose();
      ps.translate(0.0, 0.3 + Math.sin(edad * 0.22F) * (alerta ? 0.015 : 0.04), 0.0);
      ps.mulPose(Axis.YP.rotationDegrees(180.0F - cuerpo));
      ps.mulPose(Axis.XP.rotationDegrees(-inclina));
      ps.scale(escala / 16.0F, escala / 16.0F, escala / 16.0F);
      VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(WARDEN));
      float giroCabeza = (float)Math.sin(edad * frec + 1.2F) * (alerta ? 6.0F : 3.0F) + (alerta ? 6.0F : 0.0F);
      ps.pushPose();
      ps.mulPose(Axis.XP.rotationDegrees(giroCabeza));
      caja(vc, ps, luz, -3.5F, -3.5F, -2.25F, 3.5F, 3.5F, 2.25F, 0, 32, 16, 16, 10);
      ps.popPose();
      alas(vc, ps, luz, aleteo);
      ps.pushPose();
      ps.mulPose(Axis.XP.rotationDegrees(giroCabeza));
      alfa = (int)(70.0F + 185.0F * (0.35F + 0.65F * latido));
      VertexConsumer vb = buf.getBuffer(RenderType.entityTranslucentEmissive(BRILLO));
      caja(vb, ps, 15728880, -3.6F, -3.6F, -2.35F, 3.6F, 3.6F, 2.35F, 0, 32, 16, 16, 10);
      alfa = 255;
      ps.popPose();
      ps.popPose();
      super.render(e, yaw, pt, ps, buf, luz);
   }

   private static void alas(VertexConsumer vc, PoseStack ps, int luz, float aleteo) {
      float A = aleteo * (float) (Math.PI / 180.0);
      float B = (aleteo - 22.0F - 0.35F * aleteo) * (float) (Math.PI / 180.0);
      float[][] L = new float[5][];
      L[0] = new float[]{3.2F, 0.5F, 0.0F};
      L[1] = new float[]{L[0][0] + 3.2F * Mth.cos(A), L[0][1] + 3.2F * Mth.sin(A), -0.3F};
      L[2] = new float[]{L[1][0] + 3.2F * Mth.cos(A), L[1][1] + 3.2F * Mth.sin(A), -0.5F};
      L[3] = new float[]{L[2][0] + 3.0F * Mth.cos(B), L[2][1] + 3.0F * Mth.sin(B), -0.3F};
      L[4] = new float[]{L[3][0] + 2.6F * Mth.cos(B), L[3][1] + 2.6F * Mth.sin(B), 0.4F};
      float[] ancho = new float[]{6.0F, 6.8F, 5.2F, 4.0F, 1.2F};
      float[][] T = new float[5][];

      for (int i = 0; i < 5; i++) {
         float caida = i == 0 ? 3.0F : 2.4F;
         T[i] = new float[]{L[i][0] - (i == 0 ? 0.6F : 0.0F), L[i][1] - caida * 0.8F, L[i][2] + ancho[i]};
      }

      for (int lado = -1; lado <= 1; lado += 2) {
         for (int i = 0; i < 4; i++) {
            float u0 = lado < 0 ? 68.0F : 74.0F;
            float vv = (lado < 0 ? 32.0F : 0.0F) + i % 2 * 4.0F;
            panel(vc, ps, luz, lado, L[i], L[i + 1], T[i + 1], T[i], u0, vv, u0 + 10.0F, vv + 12.0F);
         }

         for (int i = 0; i < 3; i++) {
            hueso(vc, ps, luz, lado, L[i], L[i + 1], 1.5F - i * 0.25F, lado < 0 ? 44.0F : 0.0F, lado < 0 ? 50.0F : 58.0F);
         }
      }
   }

   private static void panel(
      VertexConsumer vc, PoseStack ps, int luz, int lado, float[] a, float[] b, float[] c, float[] d, float u0, float v0, float u1, float v1
   ) {
      cara(vc, ps, luz, lado * a[0], a[1], a[2], lado * b[0], b[1], b[2], lado * c[0], c[1], c[2], lado * d[0], d[1], d[2], u0, v0, u1, v1, 0.0F, 1.0F, 0.0F);
      cara(vc, ps, luz, lado * d[0], d[1], d[2], lado * c[0], c[1], c[2], lado * b[0], b[1], b[2], lado * a[0], a[1], a[2], u0, v0, u1, v1, 0.0F, -1.0F, 0.0F);
   }

   private static void hueso(VertexConsumer vc, PoseStack ps, int luz, int lado, float[] a, float[] b, float grosor, float u, float v) {
      Vector3f desde = new Vector3f(lado * a[0], a[1], a[2]);
      Vector3f dir = new Vector3f(lado * b[0], b[1], b[2]).sub(desde);
      float largo = dir.length();
      if (!(largo < 0.001F)) {
         dir.div(largo);
         ps.pushPose();
         ps.translate(desde.x, desde.y, desde.z);
         ps.mulPose(new Quaternionf().rotationTo(new Vector3f(0.0F, -1.0F, 0.0F), dir));
         caja(vc, ps, luz, -grosor, -largo, -grosor, grosor, 0.0F, grosor, (int)u, (int)v, 8, 28, 8);
         ps.popPose();
      }
   }

   private static void caja(
      VertexConsumer vc, PoseStack ps, int luz, float x0, float y0, float z0, float x1, float y1, float z1, int u, int v, int tw, int th, int td
   ) {
      float uN = u + td;
      float vN = v + td;
      float uE = u + td + tw;
      float uS = u + 2 * td + tw;
      boolean ax = x1 - x0 > 1.0E-4F;
      boolean ay = y1 - y0 > 1.0E-4F;
      boolean az = z1 - z0 > 1.0E-4F;
      if (ax && ay) {
         cara(vc, ps, luz, x0, y1, z0, x1, y1, z0, x1, y0, z0, x0, y0, z0, uN, vN, uN + tw, vN + th, 0.0F, 0.0F, -1.0F);
         cara(vc, ps, luz, x1, y1, z1, x0, y1, z1, x0, y0, z1, x1, y0, z1, uS, vN, uS + tw, vN + th, 0.0F, 0.0F, 1.0F);
      }

      if (az && ay) {
         cara(vc, ps, luz, x1, y1, z0, x1, y1, z1, x1, y0, z1, x1, y0, z0, uE, vN, uE + td, vN + th, 1.0F, 0.0F, 0.0F);
         cara(vc, ps, luz, x0, y1, z1, x0, y1, z0, x0, y0, z0, x0, y0, z1, u, vN, u + td, vN + th, -1.0F, 0.0F, 0.0F);
      }

      if (ax && az) {
         cara(vc, ps, luz, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, uN, v, uN + tw, v + td, 0.0F, 1.0F, 0.0F);
         cara(vc, ps, luz, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, uN + tw, v, uN + 2 * tw, v + td, 0.0F, -1.0F, 0.0F);
      }
   }

   private static void cara(
      VertexConsumer vc,
      PoseStack ps,
      int luz,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4,
      float u0,
      float v0,
      float u1,
      float v1,
      float nx,
      float ny,
      float nz
   ) {
      Matrix4f p = ps.last().pose();
      Pose n = ps.last();
      punto(vc, p, n, luz, x1, y1, z1, u0 / 128.0F, v0 / 128.0F, nx, ny, nz);
      punto(vc, p, n, luz, x2, y2, z2, u1 / 128.0F, v0 / 128.0F, nx, ny, nz);
      punto(vc, p, n, luz, x3, y3, z3, u1 / 128.0F, v1 / 128.0F, nx, ny, nz);
      punto(vc, p, n, luz, x4, y4, z4, u0 / 128.0F, v1 / 128.0F, nx, ny, nz);
   }

   private static void punto(VertexConsumer vc, Matrix4f p, Pose n, int luz, float x, float y, float z, float u, float v, float nx, float ny, float nz) {
      vc.addVertex(p, x, y, z).setColor(255, 255, 255, alfa).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(n, nx, ny, nz);
   }
}
