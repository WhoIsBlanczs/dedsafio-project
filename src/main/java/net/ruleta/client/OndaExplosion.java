package net.ruleta.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.OndaPayload;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;

public final class OndaExplosion {
   private static final float DURACION = 1.1F;
   private static final float PIXEL = 0.5F;
   private static final int MAXIMO = 24;
   private static final List<OndaExplosion.Onda> ONDAS = new ArrayList<>();

   private OndaExplosion() {
   }

   static void agregar(OndaPayload p) {
      if (ONDAS.size() >= 24) {
         ONDAS.remove(0);
      }

      ONDAS.add(new OndaExplosion.Onda(p.x(), p.y(), p.z(), p.radio(), System.nanoTime()));
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut evento) {
      ONDAS.clear();
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent evento) {
      if (evento.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && !ONDAS.isEmpty()) {
         long ahora = System.nanoTime();
         Vec3 cam = evento.getCamera().getPosition();
         Matrix4f m = evento.getPoseStack().last().pose();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         boolean algo = false;
         Iterator<OndaExplosion.Onda> it = ONDAS.iterator();

         while (it.hasNext()) {
            OndaExplosion.Onda o = it.next();
            float t = (float)(ahora - o.inicio) / 1.0E9F / 1.1F;
            if (t >= 1.0F) {
               it.remove();
            } else {
               algo |= dibujar(b, m, o, t, cam);
            }
         }

         if (algo) {
            BufferUploader.drawWithShader(b.buildOrThrow());
         } else {
            MeshData vacio = b.build();
            if (vacio != null) {
               vacio.close();
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   private static boolean dibujar(BufferBuilder b, Matrix4f m, OndaExplosion.Onda o, float t, Vec3 cam) {
      float sale = 1.0F - t;
      float r = o.radio * (1.0F - sale * sale * sale);
      float ancho = Math.max(0.5F, r * (0.95F * (float)Math.pow(sale, 1.6F) + 0.04F));
      float dentro = r - ancho;
      float alfa = t < 0.55F ? 1.0F : 1.0F - (t - 0.55F) / 0.45F;
      if (!(r < 0.25F) && !(alfa <= 0.0F)) {
         float y = (float)(o.y + 0.12 - cam.y);
         int n = Mth.ceil(r / 0.5F);
         boolean algo = false;

         for (int gx = -n; gx < n; gx++) {
            for (int gz = -n; gz < n; gz++) {
               float cx = (gx + 0.5F) * 0.5F;
               float cz = (gz + 0.5F) * 0.5F;
               float d = Mth.sqrt(cx * cx + cz * cz);
               if (!(d > r) && !(d < dentro)) {
                  float u = (d - dentro) / ancho;
                  int color = color(u, t);
                  int a = (int)(alfa * (color >>> 24 & 0xFF));
                  if (a > 2) {
                     int rr = color >> 16 & 0xFF;
                     int gg = color >> 8 & 0xFF;
                     int bb = color & 0xFF;
                     float x0 = (float)(o.x - cam.x) + gx * 0.5F;
                     float z0 = (float)(o.z - cam.z) + gz * 0.5F;
                     float x1 = x0 + 0.5F;
                     float z1 = z0 + 0.5F;
                     b.addVertex(m, x0, y, z0).setColor(rr, gg, bb, a);
                     b.addVertex(m, x0, y, z1).setColor(rr, gg, bb, a);
                     b.addVertex(m, x1, y, z1).setColor(rr, gg, bb, a);
                     b.addVertex(m, x1, y, z0).setColor(rr, gg, bb, a);
                     algo = true;
                  }
               }
            }
         }

         return algo;
      } else {
         return false;
      }
   }

   private static int color(float u, float t) {
      if (t > 0.6F) {
         return u > 0.5F ? (t > 0.8F ? -523753444 : -252681196) : -385890284;
      } else if (u > 0.82F) {
         return -117474816;
      } else if (u > 0.55F) {
         return -184564736;
      } else {
         return u > 0.3F ? -419435892 : -1258293032;
      }
   }

   private record Onda(double x, double y, double z, float radio, long inicio) {
   }
}
