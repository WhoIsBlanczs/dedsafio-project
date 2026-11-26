package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.entity.Entity;
import net.ruleta.dedsafio.EscenaPayload;
import org.joml.Matrix4f;

public final class OlaCliente {
   private static ShaderInstance shaderOla;
   private static ShaderInstance shaderVista;
   private static boolean activa;
   private static long t0;
   private static float altura;
   private static float largo;
   private static float velocidad;
   private static float x0;
   private static float z0;
   private static float yaw = 180.0F;
   private static float yBase = -2.2F;
   private static String dimAct = "";
   private static boolean dentro;
   private static long dentroDesde;
   private static float dentroDur;
   private static boolean dentroVuelve = false;

   private OlaCliente() {
   }

   public static void shaderOla(ShaderInstance s) {
      shaderOla = s;
   }

   public static void shaderVista(ShaderInstance s) {
      shaderVista = s;
   }

   public static void recibir(EscenaPayload m) {
      switch (m.tipo()) {
         case 3:
            activa = true;
            t0 = System.nanoTime() - (long)(m.transcurrido() * 1.0E9F);
            altura = m.a();
            largo = m.b();
            velocidad = m.c();
            x0 = m.d();
            z0 = m.e();
            yaw = m.r();
            yBase = Float.isNaN(m.y()) ? -2.2F : m.y();
            Minecraft mcr = Minecraft.getInstance();
            dimAct = mcr.level == null ? "" : mcr.level.dimension().location().toString();
            break;
         case 4:
            dentro = true;
            dentroDesde = System.nanoTime();
            dentroDur = m.a();
            dentroVuelve = m.b() > 0.5F;
            AudioDedsafio.tragado();
            break;
         default:
            limpiar();
      }
   }

   public static void limpiar() {
      activa = false;
      dentro = false;
      AudioDedsafio.pararOla();
      AudioDedsafio.pararDentro();
   }

   public static boolean activaFuera() {
      return activa && !DedsafioCielo.enDimension();
   }

   public static boolean dentro() {
      return dentro;
   }

   private static float segundosDentro() {
      return (float)(System.nanoTime() - dentroDesde) / 1.0E9F;
   }

   private static double dirX() {
      return -Math.sin(Math.toRadians(yaw));
   }

   private static double dirZ() {
      return Math.cos(Math.toRadians(yaw));
   }

   private static double adelante(double px, double pz) {
      return (px - x0) * dirX() + (pz - z0) * dirZ();
   }

   private static double lado(double px, double pz) {
      return -(px - x0) * dirZ() + (pz - z0) * dirX();
   }

   private static double frente() {
      return velocidad * (float)(System.nanoTime() - t0) / 1.0E9;
   }

   public static void tick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null || !DedsafioCielo.enDimension() && !mc.level.dimension().location().toString().equals(dimAct)) {
         if (activa || dentro) {
            limpiar();
         }
      } else {
         if (dentro && segundosDentro() > dentroDur + (dentroVuelve ? 3.5F : 0.7F)) {
            dentro = false;
            AudioDedsafio.pararDentro();
         }

         Entity e = mc.getCameraEntity();
         if (activa && !dentro && e != null) {
            double zf = frente();
            if (zf > 1500.0) {
               activa = false;
               AudioDedsafio.pararOla();
            } else {
               double d = adelante(e.getX(), e.getZ()) - zf;
               double lateral = Math.max(0.0, Math.abs(lado(e.getX(), e.getZ())) - largo / 2.0);
               double dist = Math.sqrt(Math.max(d, 0.0) * Math.max(d, 0.0) + lateral * lateral);
               float vol = (float)Math.pow(Math.max(0.0, 1.0 - dist / 320.0), 1.6) * 0.95F;
               if (vol < 0.01F) {
                  AudioDedsafio.pararOla();
               } else {
                  AudioDedsafio.ola(vol, ojo -> {
                     double lt = Math.max(-largo / 2.0, Math.min(largo / 2.0, lado(ojo.x, ojo.z)));
                     double sf = frente();
                     double px = x0 - dirZ() * lt + dirX() * sf;
                     double pz = z0 + dirX() * lt + dirZ() * sf;
                     double dx = px - ojo.x;
                     double dz = pz - ojo.z;
                     double n = Math.sqrt(dx * dx + dz * dz);
                     return n < 0.001 ? ojo.add(0.0, 0.0, 6.0) : ojo.add(dx / n * 6.0, 0.0, dz / n * 6.0);
                  });
               }
            }
         } else {
            if (!activa || dentro) {
               AudioDedsafio.pararOla();
            }
         }
      }
   }

   private static void fijar(ShaderInstance sh, String nombre, float a, float b, float c, float d, int n) {
      Uniform u = sh.getUniform(nombre);
      if (u != null) {
         if (n == 4) {
            u.set(a, b, c, d);
         } else {
            u.set(a);
         }
      }
   }

   public static void dibujar(Matrix4f vista, double camX, double camY, double camZ) {
      ShaderInstance sh = shaderOla;
      if (activa && sh != null) {
         double zf = frente();
         double lejos = adelante(camX, camZ) - zf;
         if (!(lejos > 1600.0) && !(zf > 1500.0)) {
            if (lejos < -140.0 && !dentro) {
               activa = false;
               AudioDedsafio.pararOla();
            } else {
               float t = DedsafioCielo.tiempo();
               fijar(sh, "Tiempo", t % 3600.0F, 0.0F, 0.0F, 0.0F, 1);
               fijar(sh, "Ola", altura, largo, 0.0F, 0.0F, 4);
               fijar(sh, "Dir", (float)dirX(), (float)dirZ(), 0.0F, 0.0F, 4);
               fijar(sh, "Mundo", (float)(camX % 4096.0), (float)(camZ % 4096.0), 0.0F, 0.0F, 4);
               Vtx.empujarVista(vista);
               RenderSystem.setShader(() -> sh);
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.disableCull();

               try {
                  int FILAS = 56;
                  float paso = Math.max(0.4F, largo / 330.0F);
                  int cols = (int)Math.ceil(largo / paso);
                  BufferBuilder b = Vtx.quads();

                  for (int i = 0; i < cols; i++) {
                     float ua = -largo / 2.0F + i * paso;
                     float ub = Math.min(ua + paso, largo / 2.0F);

                     for (int k = 0; k < 56; k++) {
                        float ta = 2.0F * k / 56.0F;
                        float tb = 2.0F * (k + 1) / 56.0F;
                        vertice(b, ua, ta, zf, yBase, camX, camY, camZ);
                        vertice(b, ub, ta, zf, yBase, camX, camY, camZ);
                        vertice(b, ub, tb, zf, yBase, camX, camY, camZ);
                        vertice(b, ua, tb, zf, yBase, camX, camY, camZ);
                     }
                  }

                  Vtx.dibujar(b);
               } finally {
                  RenderSystem.enableCull();
                  RenderSystem.depthMask(true);
                  RenderSystem.disableBlend();
                  Vtx.sacarVista();
               }
            }
         }
      }
   }

   private static void vertice(BufferBuilder b, float u, float t, double zf, float y0, double cx, double cy, double cz) {
      double wx = x0 - dirZ() * u + dirX() * zf;
      double wz = z0 + dirX() * u + dirZ() * zf;
      Vtx.v(b, (float)(wx - cx), (float)(y0 - cy), (float)(wz - cz), u, t);
   }

   public static void dibujarDentro() {
      ShaderInstance sh = shaderVista;
      if (dentro && sh != null) {
         Minecraft mc = Minecraft.getInstance();
         float seg = segundosDentro();
         float cubre = Math.min(1.0F, seg / 0.35F);
         if (!dentroVuelve) {
            float k = Math.min(1.0F, Math.max(0.0F, (seg - dentroDur) / 0.6F));
            cubre *= 1.0F - k * k * (3.0F - 2.0F * k);
         }

         float asp = (float)mc.getWindow().getWidth() / Math.max(1, mc.getWindow().getHeight());
         float cabeceo = mc.player == null ? 0.0F : mc.player.getXRot();
         fijar(sh, "Dentro", cubre, seg, asp, cabeceo, 4);
         fijar(sh, "Tiempo", DedsafioCielo.tiempo() % 3600.0F, 0.0F, 0.0F, 0.0F, 1);
         RenderSystem.setShader(() -> sh);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();

         try {
            BufferBuilder b = Vtx.quads();
            Vtx.v(b, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F);
            Vtx.v(b, 1.0F, -1.0F, 0.0F, 1.0F, 0.0F);
            Vtx.v(b, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F);
            Vtx.v(b, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
            Vtx.dibujar(b);
         } finally {
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
         }
      }
   }

   public static float[] balanceo() {
      if (!dentro) {
         return null;
      } else {
         float s = segundosDentro();
         float k = Math.min(1.0F, s / 0.6F);
         return new float[]{2.6F * k * (float)Math.sin(s * 0.9), 1.9F * k * (float)Math.sin(s * 1.35 + 1.0), 6.5F * k * (float)Math.sin(s * 0.7)};
      }
   }
}
