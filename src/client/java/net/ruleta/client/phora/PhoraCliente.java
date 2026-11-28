package net.ruleta.client.phora;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import com.mojang.blaze3d.vertex.VertexSorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;

import net.ruleta.phora.MisilEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class PhoraCliente {
   private static final SoundEvent TEMA_INTRO = sonido("phora.tema_intro");
   private static final SoundEvent TEMA_BUCLE = sonido("phora.tema_bucle");
   private static final SoundEvent VOZ = sonido("phora.voz");
   private static final SoundEvent SILBIDO = sonido("phora.silbido");
   private static final SoundEvent IMPACTO = sonido("phora.impacto");
   private static final float INTRO_DURA = 56.66F;
   private static final float ENTRA = 4.0F;
   private static final float SALE = 4.0F;
   private static final float RADIO = 1.7F;
   private static final float ESTELA = 58.0F;
   private static final float HUMO = 80.0F;
   private static final float POLVO_DURA = 26.0F;
   private static final float VINO_R = 0.3F;
   private static final float VINO_G = 0.035F;
   private static final float VINO_B = 0.06F;


   private static long desde = -1L;
   private static long acaba = -1L;
   private static SoundInstance intro;
   private static SoundInstance bucle;
   private static final Map<Integer, SoundInstance> SILBIDOS = new HashMap<>();
   private static final List<PhoraCliente.Hongo> POLVOS = new ArrayList<>();
   private static float destello;
   private static long destelloEn;

   private PhoraCliente() {
   }

   public static void shaderCielo(Object s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static void shader(Object s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruletafanproject", id));
   }

   public static void recibir(int valor) {
      long ahora = System.nanoTime();
      if (valor >= 0) {
         if (desde < 0L || acaba >= 0L) {
            pararMusica();
            desde = ahora - valor * 1000000000L;
            acaba = -1L;
            Minecraft mc = Minecraft.getInstance();
            if (valor < 55.66F) {
               intro = new SonidoPhora(TEMA_INTRO, false, PhoraCliente::volumenMusica, null);
               mc.getSoundManager().play(intro);
            }

            if (valor <= 1) {
               voz();
            }
         }
      } else if (desde >= 0L && acaba < 0L) {
         acaba = ahora;
      }
   }

   public static void voz() {
      Minecraft.getInstance()
         .getSoundManager()
         .play(
            new SimpleSoundInstance(VOZ.location(), SoundSource.MASTER, 1.0F, 1.0F, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true)
         );
   }

   public static void impacto(int potencia, String donde) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.level != null) {
         String[] p = donde.trim().split(" ");
         if (p.length >= 3) {
            Vec3 c;
            try {
               c = new Vec3(Double.parseDouble(p[0]), Double.parseDouble(p[1]), Double.parseDouble(p[2]));
            } catch (NumberFormatException ex) {
               return;
            }

            double d = mc.player.getEyePosition().distanceTo(c);
            mc.getSoundManager().play(new net.minecraft.client.resources.sounds.SimpleSoundInstance(
               IMPACTO.location(), net.minecraft.sounds.SoundSource.MASTER, d < 40.0 ? 2.0F : 9.0F, 0.9F + mc.level.getRandom().nextFloat() * 0.2F,
               net.minecraft.util.RandomSource.create(), false, 0,
               d < 40.0 ? net.minecraft.client.resources.sounds.SoundInstance.Attenuation.NONE : net.minecraft.client.resources.sounds.SoundInstance.Attenuation.LINEAR,
               c.x, c.y, c.z, d < 40.0
            ));
         }
      }
   }

   public static void limpiar() {
      pararMusica();
      desde = -1L;
      acaba = -1L;
      POLVOS.clear();
      SILBIDOS.clear();
      destello = 0.0F;
   }

   private static void pararMusica() {
      for (SoundInstance s : new SoundInstance[]{intro, bucle}) {
         if (s != null) {
            Minecraft.getInstance().getSoundManager().stop(s);
         }
      }

      intro = null;
      bucle = null;
   }

   private static float suave(float x) {
      x = Mth.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static float segundos() {
      return (float)(System.nanoTime() - desde) / 1.0E9F;
   }

   public static boolean enCurso() {
      return desde >= 0L && acaba < 0L;
   }

   private static float fuerza() {
      if (desde < 0L) {
         return 0.0F;
      }

      float f = suave(segundos() / 4.0F);
      if (acaba >= 0L) {
         float s = (float)(System.nanoTime() - acaba) / 1.0E9F;
         f *= 1.0F - suave(s / 4.0F);
         if (s > 4.0F) {
            desde = -1L;
            acaba = -1L;
            return 0.0F;
         }
      }

      return f;
   }

   private static float volumenMusica() {
      if (desde < 0L) {
         return -1.0F;
      } else if (acaba >= 0L) {
         float s = (float)(System.nanoTime() - acaba) / 1.0E9F;
         return s > 3.0F ? -1.0F : 1.0F - suave(s / 3.0F);
      } else {
         return 1.0F;
      }
   }

   private static float fadeDestello() {
      return (float)Math.exp(-((float)(System.nanoTime() - destelloEn) / 1.0E9F) / 0.35F);
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            if (desde >= 0L && acaba < 0L && bucle == null && segundos() >= 56.61F) {
               bucle = new SonidoPhora(TEMA_BUCLE, true, PhoraCliente::volumenMusica, null);
               mc.getSoundManager().play(bucle);
            }

            for (Entity ent : mc.level.entitiesForRendering()) {
               if (ent instanceof MisilEntity m && !SILBIDOS.containsKey(m.getId()) && mc.player != null && mc.player.distanceToSqr(m) < 48400.0) {
                  int id = m.getId();
                  SoundInstance s = new SonidoPhora(SILBIDO, false, () -> m.isRemoved() ? -1.0F : 6.0F, () -> m.isRemoved() ? null : m.position());
                  SILBIDOS.put(id, s);
                  mc.getSoundManager().play(s);
               }
            }

            SILBIDOS.entrySet().removeIf(x -> mc.level.getEntity(x.getKey()) == null && !mc.getSoundManager().isActive(x.getValue()));
            POLVOS.removeIf(p -> p.edad() > 26.0F);
         }
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      limpiar();
   }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent e) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void cielo(float lejos, float t, float fuerza, float flash) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static Object usar(float modo, float t, float semilla, boolean sumar) {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   private static void misil(MisilEntity m, Vec3 cam, float pt, float t, Camera camara) {
      Vec3 p = new Vec3(Mth.lerp(pt, m.xo, m.getX()), Mth.lerp(pt, m.yo, m.getY()), Mth.lerp(pt, m.zo, m.getZ())).add(0.0, 1.0, 0.0).subtract(cam);
      Vec3 v = m.getDeltaMovement();
      if (v.lengthSqr() < 1.0E-6) {
         v = new Vec3(0.0, -1.0, 0.0);
      }

      Vector3f dir = new Vector3f((float)v.x, (float)v.y, (float)v.z).normalize();
      Vector3f h = new Vector3f((float)p.x, (float)p.y, (float)p.z);
      float semilla = m.getId() % 97 * 1.37F;
      usar(2.0F, t, semilla, true);
      cinta(h, dir, 49.300003F, 5.44F, 0.55F);
      cinta(h, dir, 58.0F, 2.38F, 1.0F);
      usar(1.0F, t, semilla, true);
      cartel(h, 15.3F, camara, 1.0F, 1.0F, 1.0F, 0.55F);
      cartel(h, 5.44F, camara, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void cinta(Vector3f cabeza, Vector3f dir, float largo, float ancho, float alfa) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   static void cartel(Vector3f c, float r, Camera cam, float cr, float cg, float cb, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void esfera(Vector3f c, float r, float giro) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void punto(BufferBuilder b, Vector3f c, float r, int i, int j, int lat, int lon, float giro) {
      float a = (float) Math.PI * i / lat;
      float o = (float) (Math.PI * 2) * j / lon;
      float nx = Mth.sin(a) * Mth.cos(o);
      float ny = Mth.cos(a);
      float nz = Mth.sin(a) * Mth.sin(o);
      float bulto = 1.0F + 0.1F * Mth.sin(nx * 5.1F + ny * 3.3F) * Mth.cos(nz * 4.7F - ny * 2.1F);
      float cg = Mth.cos(giro);
      float sg = Mth.sin(giro);
      float x = (nx * cg - nz * sg) * r * bulto;
      float z = (nx * sg + nz * cg) * r * bulto;
      float y = ny * r * bulto;
      b.addVertex(c.x + x, c.y + y, c.z + z)
         .setColor(nx * 0.5F + 0.5F, ny * 0.5F + 0.5F, nz * 0.5F + 0.5F, 1.0F)
         .setUv(o / (float) (Math.PI * 2), a / (float) Math.PI);
   }

   private static void plano(float r, float g, float bl, float a, boolean sumar) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static final class Hongo {
      final Vec3 centro;
      final long nace = System.nanoTime();
      final float escala;
      final float[][] columna;
      final float[][] cabeza;
      final float[][] base;
      final float semilla;

      Hongo(Vec3 centro, float escala) {
         this.centro = centro;
         this.escala = escala;
         Random r = new Random(Double.doubleToLongBits(centro.x * 31.0 + centro.z * 17.0));
         this.semilla = r.nextFloat() * 50.0F;
         this.columna = new float[80][];

         for (int i = 0; i < this.columna.length; i++) {
            this.columna[i] = new float[]{
               r.nextFloat(), r.nextFloat() * 6.283F, (float)Math.sqrt(r.nextFloat()), 4.0F + r.nextFloat() * 3.0F, 0.8F + r.nextFloat() * 0.25F, r.nextFloat()
            };
         }

         this.cabeza = new float[170][];

         for (int i = 0; i < this.cabeza.length; i++) {
            this.cabeza[i] = new float[]{
               r.nextFloat() * 6.283F,
               r.nextFloat() * 6.283F,
               0.35F + 0.65F * (float)Math.sqrt(r.nextFloat()),
               7.0F + r.nextFloat() * 5.0F,
               0.88F + r.nextFloat() * 0.22F,
               r.nextFloat()
            };
         }

         this.base = new float[90][];

         for (int i = 0; i < this.base.length; i++) {
            this.base[i] = new float[]{
               r.nextFloat() * 6.283F,
               0.25F + 0.85F * (float)Math.sqrt(r.nextFloat()),
               r.nextFloat() * 4.0F,
               6.0F + r.nextFloat() * 5.0F,
               0.78F + r.nextFloat() * 0.25F,
               r.nextFloat()
            };
         }
      }

      float edad() {
         return (float)(System.nanoTime() - this.nace) / 1.0E9F;
      }

      void dibujar(Vec3 cam, Camera camara, float t) {
         // efecto de dibujo pendiente de portar a 26.3
      }

      private static void caja(float x0, float y0, float z0, float x1, float y1, float z1) {
         // efecto de dibujo pendiente de portar a 26.3
      }

      private static void bocanada(BufferBuilder b, Camera cam, float x, float y, float z, float r, float tono, float semilla, float fuego, float a) {
         // efecto de dibujo pendiente de portar a 26.3
      }

      private static void anilloSuelo(float cx, float cy, float cz, float r0, float r1, float a) {
         // efecto de dibujo pendiente de portar a 26.3
      }
   }
}
