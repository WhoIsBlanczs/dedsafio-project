package net.ruleta.client.eclipse;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.lang.reflect.Method;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.linterna.LinternaCliente;
import net.ruleta.herobrine.client.OjosBrillo;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class EclipseCliente {
   private static final float ENTRA = 3.0F;
   private static final float PARCIAL = 24.0F;
   private static final float ANILLO = 2.5F;
   private static final float SALE_PARCIAL = 24.0F;
   private static final float SALE = 3.0F;
   private static final float OSCURO_PARCIAL = 0.25F;
   private static final float OSCURO_TOTAL = 1.0F;
   private static final float OSCURO_BLOQUES = 1.0F;
   private static final float OSCURO_SHADERS = 0.82F;
   private static final Vector3f SOL_FIJO = new Vector3f(0.55F, 0.8F, 0.24F).normalize();
   private static final SoundEvent AMBIENTE = sonido("eclipse.ambiente");
   private static final SoundEvent ANILLO_SND = sonido("eclipse.anillo");
   private static final SoundEvent TOTALIDAD_SND = sonido("eclipse.totalidad");
   private static final SoundEvent MUSICA = sonido("eclipse.musica");
   private static final float ADELANTO_ANILLO = 1.0F;
   private static SoundInstance ambiente;
   private static SoundInstance musica;
   private static float sonadoHasta = -1.0F;
   private static Object shader;
   private static long desde = -1L;
   private static float totalidad = 30.0F;
   private static Vector3f solFijado;
   private static float cortado = -1.0F;
   private static long cortadoEn;
   private static long cacheEn = Long.MIN_VALUE;
   private static float[] cache;
   private static Boolean irisApi;
   private static Method irisInstancia;
   private static Method irisEnUso;

   private EclipseCliente() {
   }

   public static void shader(Object s) {
      shader = s;
   }

   public static void recibir(int segundos, String lleva) {
      long ahora = System.nanoTime();
      if (segundos >= 0) {
         int ya = 0;

         try {
            ya = Integer.parseInt(lleva.trim());
         } catch (NumberFormatException var6) {
         }

         desde = ahora - ya * 1000000000L;
         totalidad = segundos;
         pararSonidos();
         sonadoHasta = ya > 0 ? ya : -1.0F;
         solFijado = ya > 2 ? new Vector3f(SOL_FIJO) : solDeVerdad(0.0F);
         cortado = -1.0F;
      } else if (desde >= 0L && cortado < 0.0F) {
         cortado = segundos(ahora);
         cortadoEn = ahora;
      }
   }

   private static Vector3f solDeVerdad(float pt) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         return new Vector3f(SOL_FIJO);
      }

      float giro = (((float)(mc.level.getOverworldClockTime() % 24000L)) / 24000.0F) * (float) (Math.PI * 2);
      Vector3f sol = new Vector3f(-Mth.sin(giro), Mth.cos(giro), 0.0F);
      return sol.y < 0.03F ? new Vector3f(SOL_FIJO) : sol;
   }

   public static void limpiar() {
      pararSonidos();
      desde = -1L;
      cortado = -1.0F;
   }

   public static boolean activo() {
      return desde >= 0L;
   }

   private static float segundos(long ahora) {
      return (float)(ahora - desde) / 1.0E9F;
   }

   private static float suave(float x) {
      x = Mth.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static float inicioTotal() {
      return 29.5F;
   }

   private static float finTotal() {
      return inicioTotal() + totalidad;
   }

   private static float fin() {
      return finTotal() + 2.5F + 24.0F + 3.0F;
   }

   private static float t(long ahora) {
      float t = segundos(ahora);
      if (cortado >= 0.0F) {
         float pasa = (float)(ahora - cortadoEn) / 1.0E9F;
         float salida = fin() - 3.0F;
         float u = suave(pasa / 4.0F);
         t = cortado < salida ? cortado + (salida - cortado) * u : cortado;
         if (pasa > 4.0F) {
            t = salida + (pasa - 4.0F);
         }
      }

      return t;
   }

   private static float[] fases(float t) {
      float opac = suave(t / 3.0F) * (1.0F - suave((t - (fin() - 3.0F)) / 3.0F));
      float a = 3.0F;
      float b = a + 24.0F;
      float c = inicioTotal();
      float d = finTotal();
      float e = d + 2.5F;
      float f = e + 24.0F;
      float luna;
      if (t < b) {
         luna = 1.0F - suave((t - a) / 24.0F) * 0.93F;
      } else if (t < c) {
         luna = 0.07F * (1.0F - suave((t - b) / 2.5F));
      } else if (t < d) {
         luna = 0.0F;
      } else if (t < e) {
         luna = -0.07F * suave((t - d) / 2.5F);
      } else {
         luna = -0.07F - 0.93F * suave((t - e) / 24.0F);
      }

      float queda = Mth.clamp(Math.abs(luna) * 2.4F / 2.05F, 0.0F, 1.0F);
      queda = (float)Math.pow(queda, 0.8);
      float total = suave((t - c + 0.6F) / 1.2F) * (1.0F - suave((t - d + 0.6F) / 1.2F));
      float tapado = 1.0F - queda;
      float noche = Math.max(total, (float)Math.pow(tapado, 1.5) * 0.9F) * opac;
      float anillo = (float)(Math.exp(-Math.pow((t - (c - 0.9F)) / 0.7F, 2.0)) + Math.exp(-Math.pow((t - (d + 0.9F)) / 0.7F, 2.0)));
      float corona = total;
      float oscuro = Math.max(total * 1.0F, Math.max(tapado * tapado * 0.25F, (float)Math.pow(tapado, 6.0) * 1.0F)) * opac;
      float bloques = Math.max(total, (float)Math.pow(tapado, 8.0)) * 1.0F * opac;
      return new float[]{luna, noche, Math.min(1.0F, anillo) * opac, opac, queda * (1.0F - total), corona * opac, oscuro, bloques};
   }

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", id));
   }

   private static void tocar(SoundEvent evento, float volumen) {
      Minecraft.getInstance()
         .getSoundManager()
         .play(
            new SimpleSoundInstance(
               evento.location(), SoundSource.MASTER, volumen, 1.0F, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
            )
         );
   }

   private static void pararSonidos() {
      for (SoundInstance s : new SoundInstance[]{ambiente, musica}) {
         if (s != null) {
            Minecraft.getInstance().getSoundManager().stop(s);
         }
      }

      ambiente = null;
      musica = null;
   }

   private static float volumenAmbiente() {
      if (desde < 0L) {
         return -1.0F;
      }

      float t = t(System.nanoTime());
      if (t >= fin()) {
         return -1.0F;
      }

      float[] f = fases(t);
      float tapado = 1.0F - f[4] - f[5];
      return f[3] * (0.12F + 0.55F * Math.max(0.0F, tapado)) * (1.0F - 0.6F * f[5]);
   }

   private static float volumenMusica() {
      if (desde < 0L) {
         return -1.0F;
      }

      float t = t(System.nanoTime());
      float entra = suave((t - inicioTotal()) / 2.0F);
      float sale = 1.0F - suave((t - finTotal()) / 4.0F);
      return !(t > finTotal() + 4.0F) && (!(cortado >= 0.0F) || !(t > finTotal()))
         ? 0.85F * entra * sale * (cortado >= 0.0F ? 1.0F - suave((float)(System.nanoTime() - cortadoEn) / 3.0E9F) : 1.0F)
         : -1.0F;
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END && desde >= 0L) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null) {
            float t = t(System.nanoTime());
            if (t >= fin()) {
               pararSonidos();
            } else {
               if ((ambiente == null || ambiente instanceof SonidoEclipse s && s.isStopped()) && t < fin() - 3.0F) {
                  ambiente = new SonidoEclipse(AMBIENTE, true, EclipseCliente::volumenAmbiente);
                  mc.getSoundManager().play(ambiente);
               }

               if (cortado < 0.0F) {
                  cruzar(t, inicioTotal(), TOTALIDAD_SND, 1.0F);
                  cruzar(t, finTotal() + 0.9F - 1.0F, ANILLO_SND, 0.9F);
                  if (musica == null && t >= inicioTotal() && t < finTotal()) {
                     musica = new SonidoEclipse(MUSICA, true, EclipseCliente::volumenMusica);
                     mc.getSoundManager().play(musica);
                  }
               }

               sonadoHasta = t;
            }
         }
      }
   }

   private static void cruzar(float t, float m, SoundEvent evento, float volumen) {
      if (sonadoHasta < m && t >= m) {
         tocar(evento, volumen);
      }
   }

   private static float suavizar() {
      return Ajustes.entero("eclipse.oscuridad") / 100.0F;
   }

   public static float oscuridad() {
      float[] f = ahora();
      return f == null ? 0.0F : f[6] * suavizar() * (1.0F - 0.8F * LinternaCliente.luzTotal());
   }

   public static float oscuridadBloques() {
      float[] f = ahora();
      return f == null ? 0.0F : f[7] * suavizar() * (1.0F - LinternaCliente.luzTotal());
   }

   public static boolean linternaEnMano() {
      return LinternaCliente.luz() > 0.0F;
   }

   private static float[] ahora() {
      if (desde < 0L) {
         return null;
      } else {
         long ahora = System.nanoTime();
         if (cache != null && ahora - cacheEn < 2000000L) {
            return cache;
         } else {
            float t = t(ahora);
            if (t >= fin()) {
               desde = -1L;
               cache = null;
               return null;
            } else {
               cache = fases(t);
               cacheEn = ahora;
               return cache;
            }
         }
      }
   }

   @SubscribeEvent
   public static void alDibujarMundo(RenderLevelStageEvent e) { }

   private static boolean shadersActivos() {
      try {
         if (irisApi == null) {
            irisApi = false;
            Class<?> c = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            irisInstancia = c.getMethod("getInstance");
            irisEnUso = c.getMethod("isShaderPackInUse");
            irisApi = true;
         }

         return irisApi && (Boolean)irisEnUso.invoke(irisInstancia.invoke(null));
      } catch (Throwable ex) {
         irisApi = false;
         return false;
      }
   }

   private static void oscurecerMundo(float k) { }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      if (desde >= 0L) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.level.dimension() == Level.OVERWORLD) {
            float[] f = fases(t(System.nanoTime()));
            float g = nivelNeblina(f) * (1.0F - f[1]);
            e.setRed(Mth.lerp(g * 0.9F, e.getRed(), 0.36F));
            e.setGreen(Mth.lerp(g * 0.9F, e.getGreen(), 0.43F));
            e.setBlue(Mth.lerp(g * 0.9F, e.getBlue(), 0.52F));
            float k = f[1] * 0.92F;
            e.setRed(Mth.lerp(k, e.getRed(), 0.22F));
            e.setGreen(Mth.lerp(k, e.getGreen(), 0.12F));
            e.setBlue(Mth.lerp(k, e.getBlue(), 0.1F));
         }
      }
   }

   private static float nivelNeblina(float[] f) {
      float tapado = 1.0F - f[4] / Math.max(f[3], 0.001F);
      return Mth.clamp(f[3] * (0.6F + 0.4F * Mth.clamp(tapado, 0.0F, 1.0F)), 0.0F, 1.0F);
   }

   @SubscribeEvent
   public static void alNiebla(ViewportEvent.RenderFog e) { }

   private static void velo(float alfa) { }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      limpiar();
   }

   private static void cubo(float s) { }
}
