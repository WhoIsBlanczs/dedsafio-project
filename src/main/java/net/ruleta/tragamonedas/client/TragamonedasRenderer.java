package net.ruleta.tragamonedas.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import net.minecraft.client.renderer.Object;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ruleta.tragamonedas.DeditaItem;
import net.ruleta.tragamonedas.TragamonedasBE;
import net.ruleta.tragamonedas.TragamonedasBloque;
import org.joml.Vector3f;

public class TragamonedasRenderer implements BlockEntityRenderer<TragamonedasBE> {
   private static final Identifier TEX_TAMBOR = Identifier.fromNamespaceAndPath("ruleta", "textures/block/tragamonedas/drum.png");
   private static final Identifier TEX_BRAZO = Identifier.fromNamespaceAndPath("ruleta", "textures/block/tragamonedas/lever_arm.png");
   private static final Identifier TEX_BOLA = Identifier.fromNamespaceAndPath("ruleta", "textures/block/tragamonedas/red.png");
   private static final float[] RODILLO_X = new float[]{-6.7F, 0.0F, 6.7F};
   private static final float RODILLO_Y = 25.2F;
   private static final float RODILLO_Z = -1.0F;
   private static final float APOTEMA = 5.4F;
   private static final float MEDIO_ANCHO = 2.9F;
   private static final float MEDIA_CARA = 5.4F * (float)Math.tan(Math.PI / 8);
   private static final double DIST_ITEMS = 28.0;
   private static final float VELOCIDAD = 30.0F;
   private static final int[] FRENA_DESDE = new int[]{30, 35, 40};
   private static final float TICKS_REBOTE = 8.0F;
   private static final float PASADA = 14.0F;
   private static final double PIVOTE_Y = 18.785;
   private static final double PIVOTE_Z = -5.185;
   private static final double PIVOTE_X = 13.7;

   public TragamonedasRenderer(Context ctx) {
   }

   public boolean shouldRenderOffScreen(TragamonedasBE be) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }

   public void render(TragamonedasBE be, float parcial, PoseStack ps, Object buf, int luz, int overlay) {
      Level nivel = be.getLevel();
      if (nivel != null && be.getBlockState().getBlock() instanceof TragamonedasBloque) {
         Camera camara = Minecraft.getInstance().gameRenderer.getMainCamera();
         Vec3 pc = camara.getPosition();
         Vector3f mira = camara.getLookVector();
         BlockPos bp = be.getBlockPos();
         double dx = bp.getX() + 0.5 - pc.x;
         double dy = bp.getY() + 0.5 - pc.y;
         double dz = bp.getZ() + 0.5 - pc.z;
         double d2 = dx * dx + dy * dy + dz * dz;
         if (!(d2 > 25.0) || !(dx * mira.x() + dy * mira.y() + dz * mira.z() < -2.5)) {
            boolean conItems = d2 < 784.0;
            Direction frente = (Direction)be.getBlockState().getValue(TragamonedasBloque.FACING);
            float t = (float)(nivel.getGameTime() - be.inicio) + parcial;
            ps.pushPose();
            ps.translate(0.5, 0.0, 0.5);
            ps.mulPose(Axis.YP.rotationDegrees(180.0F - frente.toYRot()));
            ps.scale(0.0625F, 0.0625F, 0.0625F);
            ps.translate(0.0, -16.0, 0.0);

            for (int r = 0; r < 3; r++) {
               dibujarRodillo(be, r, angulo(be, r, t - 18.0F), ps, buf, luz, overlay, conItems);
            }

            dibujarPalanca(palanca(t), ps, buf, luz, overlay);
            ps.popPose();
         }
      }
   }

   private static float angulo(TragamonedasBE be, int r, float t) {
      float fin = -45.0F * be.resultado[r];
      float antes = -45.0F * be.previo[r];
      float dur = be.duracion[r];
      if (t < 0.0F) {
         return antes;
      }

      if (t >= dur) {
         return fin;
      }

      float inicioFreno = FRENA_DESDE[r];
      float largoFreno = dur - 8.0F - inicioFreno;
      float faltaAlFrenar = 30.0F * largoFreno / 2.0F - 14.0F;
      float objetivo = antes - fin - faltaAlFrenar;
      float d1 = objetivo + 360.0F * Math.round((30.0F * inicioFreno - objetivo) / 360.0F);
      float falta;
      if (t < inicioFreno) {
         falta = faltaAlFrenar + d1 * (inicioFreno - t) / inicioFreno;
      } else if (t < inicioFreno + largoFreno) {
         float s = (t - inicioFreno) / largoFreno;
         falta = faltaAlFrenar - 30.0F * largoFreno * (s - 0.5F * s * s);
      } else {
         float u = (t - inicioFreno - largoFreno) / 8.0F;
         falta = -14.0F * (1.0F - suave(u));
      }

      return fin + falta;
   }

   private static float palanca(float t) {
      float bajada = 5.0F;
      if (t < 0.0F || t >= 18.0F) {
         return 0.0F;
      } else {
         return t < bajada ? -95.0F * suave(t / bajada) : -95.0F * (1.0F - suave((t - bajada) / (18.0F - bajada)));
      }
   }

   private static float suave(float x) {
      x = Mth.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static void dibujarRodillo(TragamonedasBE be, int r, float grados, PoseStack ps, Object buf, int luz, int overlay, boolean conItems) { }

   private static void dibujarPalanca(float extra, PoseStack ps, Object buf, int luz, int overlay) { }

   private static void caja(Object vc, Pose p, float x1, float y1, float z1, float x2, float y2, float z2, int luz, int ovl) {
      quad(vc, p, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1, 0.0F, 0.0F, -1.0F, 0.8F, luz, ovl);
      quad(vc, p, x2, y1, z2, x1, y1, z2, x1, y2, z2, x2, y2, z2, 0.0F, 0.0F, 1.0F, 0.8F, luz, ovl);
      quad(vc, p, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1, 1.0F, 0.0F, 0.0F, 0.65F, luz, ovl);
      quad(vc, p, x1, y1, z2, x1, y1, z1, x1, y2, z1, x1, y2, z2, -1.0F, 0.0F, 0.0F, 0.65F, luz, ovl);
      quad(vc, p, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2, 0.0F, 1.0F, 0.0F, 1.0F, luz, ovl);
      quad(vc, p, x1, y1, z2, x2, y1, z2, x2, y1, z1, x1, y1, z1, 0.0F, -1.0F, 0.0F, 0.5F, luz, ovl);
   }

   private static void quad(
      Object vc,
      Pose p,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz,
      float nx,
      float ny,
      float nz,
      float brillo,
      int luz,
      int ovl
   ) {
      int c = Math.round(255.0F * brillo);
      v(vc, p, ax, ay, az, c, nx, ny, nz, luz, ovl);
      v(vc, p, bx, by, bz, c, nx, ny, nz, luz, ovl);
      v(vc, p, cx, cy, cz, c, nx, ny, nz, luz, ovl);
      v(vc, p, dx, dy, dz, c, nx, ny, nz, luz, ovl);
   }

   private static void v(Object vc, Pose p, float x, float y, float z, int c, float nx, float ny, float nz, int luz, int ovl) {
      vc.addVertex(p, x, y, z).setColor(c, c, c, 255).setUv(0.03F, 0.03F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(p, nx, ny, nz);
   }
}
