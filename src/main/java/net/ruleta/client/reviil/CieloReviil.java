package net.ruleta.client.reviil;

import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.client.ScreenEffectHandler;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.ruleta.client.EfectosCliente;
import net.ruleta.client.OpcionesCliente;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class CieloReviil {
   private static final float MUSICA_ORIGINAL = 46.05F;
   private static final float MUSICA_DURA = 49.22F;
   private static final float ESCALA = 1.0688382F;
   private static final float EXTIENDE = 11.75722F;
   private static final float CUBRE_TODO = 3.3F;
   private static final float RAYO = 17.101412F;
   private static final float RAYO_DURA = 3.8478177F;
   private static final float ABRE = 20.94923F;
   private static final float ABRE_DURA = 1.7101412F;
   private static final float CIERRA_GRIETA = 1.5F;
   private static final float RETIRA = 10.0F;
   private static final float[] GOLPES = new float[]{0.0F, 1.7101412F, 3.8478177F};
   private static final float SACUDIDA_RAYO = 2.2F;
   private static final float SACUDIDA_ABRE = 3.4F;
   private static final float DIST_CUBO = 0.3F;
   private static final float DIST_CABEZA = 0.72F;
   private static final SoundEvent RETUMBO = sonido("momento_reviil.retumbo");
   private static final SoundEvent DESPEJA = sonido("momento_reviil.despeja");
   private static final SoundEvent GRIETA_RAYO = sonido("momento_reviil.grieta_rayo");
   private static final SoundEvent GRIETA_ABRE = sonido("momento_reviil.grieta_abre");
   private static final SoundEvent GRIETA_CIERRA = sonido("momento_reviil.grieta_cierra");
   private static final SoundEvent CABEZAZO = sonido("momento_reviil.cabezazo");
   private static final SoundEvent MUSICA = sonido("momento_reviil.musica");
   private static final SoundEvent MUSICA_CIELO = sonido("momento_reviil.musica_cielo");
   private static Object shader;
   private static long desde = -1L;
   private static boolean bloqueado;
   private static long acaba = -1L;
   private static float cubreAlAcabar;
   private static float largoAlAcabar;
   private static float abreAlAcabar;
   private static float angulo;
   private static float semilla;
   private static float bordeGiro;
   private static float tormentaSemilla;
   private static float sonadoGrieta = -1.0F;
   private static boolean hayTormenta = true;
   private static boolean hayGrieta = true;
   private static boolean hayMusicaCielo = true;
   private static long desdeTormenta = -1L;
   private static long grietaDesde = -1L;
   private static float grietaBase;
   private static long inicioMusica = -1L;
   private static SoundInstance musica;
   private static SoundInstance musicaCielo;
   private static SoundInstance retumbo;
   private static SoundInstance despeja;
   private static long cabezazoEn = -1L;
   private static boolean cabezazoSono;
   private static boolean cabezazoGolpeo;
   private static final float CAB_PREPARA = 1.1F;
   private static final float CAB_LANZA = 1.3F;
   private static final float CAB_GOLPE = 1.6F;
   private static final float CAB_FIN = 7.6F;
   private static SoundInstance musicaSola;
   private static Object cuboVbo;
   private static float cuboLado = -1.0F;

   private CieloReviil() {
   }

   public static void shader(Object s) {
      shader = s;
   }

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", id));
   }

   public static void cabezazo() {
      long ahora = System.nanoTime();
      if (desde >= 0L && acaba < 0L && hayGrieta && !(abre(segGrieta(ahora)) < 0.5F)) {
         cabezazoEn = ahora;
         cabezazoSono = false;
         cabezazoGolpeo = false;
      }
   }

   private static float segCabezazo(long ahora) {
      return cabezazoEn < 0L ? -1.0F : (float)(ahora - cabezazoEn) / 1.0E9F;
   }

   private static void tickCabezazo(long ahora) {
      float s = segCabezazo(ahora);
      if (!(s < 0.0F)) {
         if (!cabezazoSono && s >= 1.3F) {
            cabezazoSono = true;
            tocar(CABEZAZO);
         }

         if (!cabezazoGolpeo && s >= 1.6F) {
            cabezazoGolpeo = true;
            if (visible()) {
               EfectosCliente.golpe(5.44F);
            }
         }

         if (s > 7.6F) {
            cabezazoEn = -1L;
         }
      }
   }

   private static void animarCabeza(long ahora) {
      float s = segCabezazo(ahora);
      float retro = 0.0F;
      float alza = 0.0F;
      if (s >= 0.0F) {
         float up = (float)Math.toRadians(-30.0);
         float abajo = (float)Math.toRadians(16.0);
         if (s < 1.3F) {
            float k = suave(s / 1.1F);
            retro = 0.75F * k;
            alza = up * k;
            if (s > 1.1F) {
               float tr = (float)Math.sin(s * 70.0) * 0.03F * (s - 1.1F) / 0.19999993F;
               retro += tr;
               alza += tr * 0.4F;
            }
         } else if (s < 1.6F) {
            float u = (s - 1.3F) / 0.30000007F;
            u *= u;
            retro = 0.75F + -1.3F * u;
            alza = up + (abajo - up) * u;
         } else {
            float d = s - 1.6F;
            float k = suave(d / 1.0F);
            float rebote = 0.18F * (float)Math.exp(-d * 4.5F) * (float)Math.sin(d * 20.0F);
            retro = -0.55F * (1.0F - k) + rebote;
            alza = abajo * (1.0F - k) + rebote * 0.5F;
         }
      }

      CabezaReviil.retroceso = retro;
      CabezaReviil.cabeceoExtra = alza;
   }

   private static float[] luzCabezazo(long ahora) {
      float dt = segCabezazo(ahora) - 1.6F;
      if (!(dt < 0.0F) && !(dt > 1.2F)) {
         float destello = dt < 0.03F ? dt / 0.03F : (float)Math.exp(-(dt - 0.03F) / 0.12F);
         float da = dt - 0.1F;
         float aro = 0.0F;
         float radio = 0.0F;
         if (da >= 0.0F && da < 0.9F) {
            aro = (float)Math.exp(-da / 0.22F) * Math.min(1.0F, da / 0.03F);
            radio = 0.15F + 1.9F * da;
         }

         return new float[]{destello, radio, aro};
      } else {
         return new float[]{0.0F, 0.0F, 0.0F};
      }
   }

   private static float temblorNubes(long ahora) {
      float s = segCabezazo(ahora);
      if (s < 0.0F) {
         return 0.0F;
      }

      if (s < 1.6F) {
         return 0.0F;
      }

      float d = s - 1.6F;
      return Math.max(0.0F, (float)Math.exp(-d / 1.6F) * Math.min(1.0F, d / 0.04F + 0.3F) - 0.02F);
   }

   public static void recibir(int valor) {
      long ahora = System.nanoTime();
      if (valor >= 0) {
         if (desde < 0L || acaba >= 0L) {
            bloqueado = ScreenEffectHandler.haySupernova;
            angulo = (float)(Math.random() * Math.PI * 2.0);
            semilla = (float)(Math.random() * 100.0);
            bordeGiro = (float)(Math.random() * Math.PI * 2.0);
            tormentaSemilla = (float)(Math.random() * 50.0);
         }

         desde = ahora - valor * 1000000000L;
         acaba = -1L;
         hayTormenta = true;
         hayGrieta = true;
         hayMusicaCielo = true;
         desdeTormenta = desde;
         grietaDesde = desde;
         grietaBase = 0.0F;
         inicioMusica = desde;
         sonadoGrieta = valor > 0 ? valor : -1.0F;
         SoundInstance seguida = temaSonando() ? musicaSola : null;
         if (seguida != null) {
            musicaSola = null;
         }

         pararSonidos();
         pararMusicaSola();
         if (seguida != null) {
            musica = seguida;
         } else if (valor < 49.22F) {
            musica = tocar(MUSICA);
         }

         if (valor >= 47.22F) {
            musicaCielo = seguir(MUSICA_CIELO, true, CieloReviil::volumenMusicaCielo);
         }

         if (valor < 11.75722F) {
            retumbo = seguir(RETUMBO, false, CieloReviil::volumenRetumbo);
         }
      } else if (desde >= 0L && acaba < 0L) {
         float t = segundos(ahora);
         cubreAlAcabar = cubreAct(t);
         largoAlAcabar = hayGrieta ? largo(segGrieta(ahora)) : 0.0F;
         abreAlAcabar = hayGrieta ? abre(segGrieta(ahora)) : 0.0F;
         acaba = ahora;
         parar(musica);
         parar(retumbo);
         musica = null;
         retumbo = null;
         if (largoAlAcabar > 0.0F) {
            tocar(GRIETA_CIERRA);
         }
      }
   }

   private static void iniciar(long ahora) {
      if (desde < 0L || acaba >= 0L) {
         if (acaba >= 0L) {
            pararSonidos();
         }

         bloqueado = ScreenEffectHandler.haySupernova;
         angulo = (float)(Math.random() * Math.PI * 2.0);
         semilla = (float)(Math.random() * 100.0);
         bordeGiro = (float)(Math.random() * Math.PI * 2.0);
         tormentaSemilla = (float)(Math.random() * 50.0);
         desde = ahora;
         acaba = -1L;
         hayTormenta = false;
         hayGrieta = false;
         hayMusicaCielo = false;
         desdeTormenta = -1L;
         grietaDesde = -1L;
         inicioMusica = -1L;
         sonadoGrieta = -1.0F;
      }
   }

   public static void piezaCielo(int valor) {
      long ahora = System.nanoTime();
      iniciar(ahora);
      if (!hayTormenta) {
         hayTormenta = true;
         desdeTormenta = ahora - valor * 1000000000L;
         if (valor < 11.75722F && retumbo == null) {
            retumbo = seguir(RETUMBO, false, CieloReviil::volumenRetumbo);
         }
      }
   }

   public static void piezaGrieta(int valor) {
      long ahora = System.nanoTime();
      iniciar(ahora);
      if (!hayGrieta) {
         hayGrieta = true;
         grietaDesde = ahora;
         grietaBase = 15.101412F + valor;
         sonadoGrieta = grietaBase;
      }
   }

   public static void limpiar() {
      cabezazoEn = -1L;
      CabezaReviil.retroceso = 0.0F;
      CabezaReviil.cabeceoExtra = 0.0F;
      desde = -1L;
      acaba = -1L;
      hayTormenta = false;
      hayGrieta = false;
      hayMusicaCielo = false;
      pararSonidos();
      pararMusicaSola();
   }

   public static float neblina() {
      if (!visible()) {
         return 0.0F;
      }

      long ahora = System.nanoTime();
      float k = hayTormenta ? suave(segundos(ahora) / 6.0F) : 0.0F;
      if (acaba >= 0L) {
         k *= 1.0F - suave((float)(ahora - acaba) / 1.0E9F / 4.0F);
      }

      return k;
   }

   public static boolean musicaMomentoSonando() {
      SoundManager sm = Minecraft.getInstance().getSoundManager();
      return musica != null && acaba < 0L && sm.isActive(musica) || musicaSola != null && sm.isActive(musicaSola);
   }

   public static void musicaSinCielo() {
      if (musica == null || acaba >= 0L || !Minecraft.getInstance().getSoundManager().isActive(musica)) {
         if (!temaSonando()) {
            pararMusicaSola();
            musicaSola = tocar(MUSICA);
         }

         if (desde >= 0L && acaba < 0L && !hayMusicaCielo) {
            hayMusicaCielo = true;
            inicioMusica = System.nanoTime();
         }
      }
   }

   public static void temaAdelantado() {
      if (desde < 0L || acaba >= 0L) {
         pararMusicaSola();
         musicaSola = tocar(MUSICA);
      }
   }

   private static boolean temaSonando() {
      return musicaSola != null && Minecraft.getInstance().getSoundManager().isActive(musicaSola);
   }

   public static void pararMusicaSola() {
      parar(musicaSola);
      musicaSola = null;
   }

   public static void temaMomento(boolean cortarEntrada) {
      pararMusicaSola();
      parar(musica);
      musica = null;
      parar(musicaCielo);
      musicaCielo = null;
      hayMusicaCielo = false;
      if (cortarEntrada) {
         AnimacionReviil.pararAudio();
      }

      musicaSola = tocar(MUSICA);
   }

   public static boolean activo() {
      return desde >= 0L;
   }

   public static boolean visible() {
      if (desde >= 0L && OpcionesCliente.reviilCielo) {
         if (bloqueado && !ScreenEffectHandler.haySupernova) {
            bloqueado = false;
         }

         return !bloqueado;
      } else {
         return false;
      }
   }

   private static void pararSonidos() {
      for (SoundInstance s : new SoundInstance[]{musica, musicaCielo, retumbo, despeja}) {
         parar(s);
      }

      musica = null;
      musicaCielo = null;
      retumbo = null;
      despeja = null;
   }

   private static void parar(SoundInstance s) {
      if (s != null) {
         Minecraft.getInstance().getSoundManager().stop(s);
      }
   }

   private static SoundInstance tocar(SoundEvent evento) {
      SoundInstance s = new SimpleSoundInstance(
         evento.location(), SoundSource.MASTER, 1.0F, 1.0F, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
      );
      Minecraft.getInstance().getSoundManager().play(s);
      return s;
   }

   private static SoundInstance seguir(SoundEvent evento, boolean repetir, Supplier<float[]> estado) {
      SoundInstance s = new SonidoReviil(evento, repetir, estado);
      Minecraft.getInstance().getSoundManager().play(s);
      return s;
   }

   private static float segundos(long ahora) {
      return desdeTormenta < 0L ? 0.0F : (float)(ahora - desdeTormenta) / 1.0E9F;
   }

   private static float segGrieta(long ahora) {
      return grietaDesde < 0L ? 0.0F : grietaBase + (float)(ahora - grietaDesde) / 1.0E9F;
   }

   private static float cubreAct(float t) {
      return hayTormenta ? cubre(t) : 0.0F;
   }

   private static float suave(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return x * x * (3.0F - 2.0F * x);
   }

   private static float sale(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return 1.0F - (1.0F - x) * (1.0F - x) * (1.0F - x);
   }

   private static float avance(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return (float)(x - 0.083 * (1.0 - Math.exp(-x / 0.083))) / 0.917F;
   }

   private static float cubre(float t) {
      return 3.3F * avance(t / 11.75722F);
   }

   private static float largo(float t) {
      float u = (t - 17.101412F) / 3.8478177F;
      return 0.35F * sale((u - 0.02F) / 0.17F) + 0.65F * sale((u - 0.44F) / 0.14F);
   }

   private static float abre(float t) {
      return suave((t - 20.94923F) / 1.7101412F);
   }

   private static float[] golpes(float t) {
      if (!(t < 17.101412F) && !(t > 22.149231F)) {
         float destello = 0.0F;
         float radio = 0.0F;
         float aro = 0.0F;

         for (float m : GOLPES) {
            float dt = t - 17.101412F - m;
            if (!(dt < 0.0F)) {
               destello = Math.max(destello, dt < 0.03F ? dt / 0.03F : (float)Math.exp(-(dt - 0.03F) / 0.12F));
               float da = dt - 0.1F;
               if (da >= 0.0F && da < 0.9F) {
                  float a = (float)Math.exp(-da / 0.22F) * Math.min(1.0F, da / 0.03F);
                  if (a > aro) {
                     aro = a;
                     radio = 0.15F + 1.9F * da;
                  }
               }
            }
         }

         return new float[]{destello, radio, aro};
      } else {
         return new float[]{0.0F, 0.0F, 0.0F};
      }
   }

   private static float acabando(long ahora) {
      return acaba >= 0L ? (float)(ahora - acaba) / 1.0E9F : -1.0F;
   }

   private static float esperaGrieta() {
      return largoAlAcabar > 0.0F ? 1.5F : 0.0F;
   }

   private static float cubreAcabando(float f) {
      return cubreAlAcabar * (1.0F - avance((f - esperaGrieta()) / 10.0F));
   }

   private static float cubreAhora() {
      long ahora = System.nanoTime();
      float f = acabando(ahora);
      return f >= 0.0F ? cubreAcabando(f) : cubreAct(segundos(ahora));
   }

   private static float[] grave(float c) {
      float volumen = (0.4F + 0.6F * suave(c / 0.8F)) * (1.0F - suave((c - 2.6F) / 0.7F));
      return new float[]{volumen, 0.85F + 0.15F * suave(c / 2.2F)};
   }

   private static float[] volumenRetumbo() {
      if (desde >= 0L && acaba < 0L && hayTormenta) {
         float t = segundos(System.nanoTime());
         return t > 12.75722F ? null : grave(cubre(t));
      } else {
         return null;
      }
   }

   private static float[] volumenDespeja() {
      long ahora = System.nanoTime();
      float f = acabando(ahora);
      if (desde >= 0L && !(f < 0.0F)) {
         return f < esperaGrieta() ? new float[]{0.0F, 0.85F} : grave(cubreAcabando(f));
      } else {
         return null;
      }
   }

   private static float[] volumenMusicaCielo() {
      if (desde >= 0L && hayMusicaCielo) {
         float t = (float)(System.nanoTime() - inicioMusica) / 1.0E9F;
         float libre = suave((t - 47.22F) / 3.0F);
         return new float[]{suave((cubreAhora() - 2.6F) / 0.7F) * libre, 1.0F};
      } else {
         return null;
      }
   }

   public static void tick() {
      if (desde >= 0L) {
         long ahora = System.nanoTime();
         float f = acabando(ahora);
         if (f >= 0.0F) {
            if (despeja == null && f >= esperaGrieta() && cubreAlAcabar > 0.0F) {
               despeja = seguir(DESPEJA, false, CieloReviil::volumenDespeja);
            }

            if (f > esperaGrieta() + (cubreAlAcabar > 0.0F ? 10.0F : 0.0F)) {
               desde = -1L;
               acaba = -1L;
               pararSonidos();
            }
         } else {
            tickCabezazo(ahora);
            if (musicaCielo == null && hayMusicaCielo && (float)(ahora - inicioMusica) / 1.0E9F >= 47.22F) {
               musicaCielo = seguir(MUSICA_CIELO, true, CieloReviil::volumenMusicaCielo);
            }

            if (hayGrieta) {
               float tg = segGrieta(ahora);

               for (float m : GOLPES) {
                  if (cruzar(tg, 17.101412F + m, GRIETA_RAYO) && visible()) {
                     EfectosCliente.golpe(2.2F);
                  }
               }

               if (cruzar(tg, 20.94923F, GRIETA_ABRE) && visible()) {
                  EfectosCliente.golpe(3.4F);
               }

               sonadoGrieta = tg;
            }
         }
      }
   }

   private static boolean cruzar(float t, float m, SoundEvent evento) {
      if (sonadoGrieta < m && t >= m) {
         tocar(evento);
         return true;
      } else {
         return false;
      }
   }

   public static float[] niebla(float r, float g, float b) {
      if (!visible()) {
         return null;
      }

      float k = suave((cubreAhora() - 1.35F) / 0.9F);
      return k <= 0.0F ? null : new float[]{r + (0.06F - r) * k, g + (0.0F - g) * k, b + (0.006F - b) * k};
   }

   public static void dibujar(Matrix4f vista, float lejos) { }

   private static void pasada(Object sh, Matrix4f vista, float largo, float abre, float modo, float destello, float lejos) { }

   private static void cubo(float s, Object sh) { }

   private static void fijar(Object sh, String nombre, float... v) { }
}
