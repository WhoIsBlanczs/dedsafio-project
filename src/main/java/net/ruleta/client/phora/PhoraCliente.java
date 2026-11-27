package net.ruleta.client.phora;

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
import net.ruleta.client.EfectosCliente;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.reviil.CieloReviil;
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
   private static Object shaderCielo;
   private static Object shader;
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
      shaderCielo = s;
   }

   public static void shader(Object s) {
      shader = s;
   }

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", id));
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

   public static void impacto(int potencia, String donde) { }

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
      float k = fuerza() * 0.92F;
      if (k <= 0.0F) {
         float v = CieloReviil.neblina() * 0.92F;
         if (v > 0.0F) {
            e.setRed(Mth.lerp(v, e.getRed(), 0.3F));
            e.setGreen(Mth.lerp(v, e.getGreen(), 0.035F));
            e.setBlue(Mth.lerp(v, e.getBlue(), 0.06F));
         }
      } else {
         e.setRed(Mth.lerp(k, e.getRed(), 0.8F));
         e.setGreen(Mth.lerp(k, e.getGreen(), 0.67F));
         e.setBlue(Mth.lerp(k, e.getBlue(), 0.5F));
      }
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent e) { }

   private static void cielo(float lejos, float t, float fuerza, float flash) { }

   private static Object usar(float modo, float t, float semilla, boolean sumar) { return null; }

   private static void misil(MisilEntity m, Vec3 cam, float pt, float t, Camera camara) { }

   private static void cinta(Vector3f cabeza, Vector3f dir, float largo, float ancho, float alfa) { }

   static void cartel(Vector3f c, float r, Camera cam, float cr, float cg, float cb, float a) { }

   private static void esfera(Vector3f c, float r, float giro) { }

   private static void punto(Object b, Vector3f c, float r, int i, int j, int lat, int lon, float giro) { }

   private static void plano(float r, float g, float bl, float a, boolean sumar) { }

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

      void dibujar(Vec3 cam, Camera camara, float t) { }

      private static void caja(float x0, float y0, float z0, float x1, float y1, float z1) { }

      private static void bocanada(Object b, Camera cam, float x, float y, float z, float r, float tono, float semilla, float fuego, float a) { }

      private static void anilloSuelo(float cx, float cy, float cz, float r0, float r1, float a) { }
   }
}
