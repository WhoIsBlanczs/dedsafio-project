package net.ruleta.client.meteoritos;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.DoubleUnaryOperator;
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
import net.ruleta.meteoritos.MeteoritoEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class MeteoritosCliente {
   private static final SoundEvent VUELO = sonido("meteoritos.vuelo");
   private static final SoundEvent IMPACTO = sonido("meteoritos.impacto");
   private static final SoundEvent AMBIENTE = sonido("meteoritos.ambiente");
   private static final float ENTRA = 5.0F;
   private static final float SALE = 4.0F;
   private static final float RADIO = 4.2F;
   private static final float ESTELA = 30.0F;
   private static final float VIDA = 16.0F;
   private static final float IMPACTO_VIDA = 24.0F;
   private static final int MAX_BOCANADAS = 3600;
   private static Object shader;
   private static long desde = -1L;
   private static long acaba = -1L;
   private static final Map<Integer, SoundInstance> SILBIDOS = new HashMap<>();
   private static final Map<Integer, Vec3> ORIGEN = new HashMap<>();
   private static final Map<Integer, MeteoritosCliente.Estela> ESTELAS = new HashMap<>();
   private static final List<MeteoritosCliente.Estela> FIJAS = new ArrayList<>();
   private static final Set<Integer> SUELTOS = new HashSet<>();
   private static final double ALTURA_SUELTA = 50.0;
   private static final float VIDA_FUNDA = 2.5F;
   private static final float VIDA_HUMO = 16.0F;
   private static final List<MeteoritosCliente.Bocanada> HUMOS = new ArrayList<>();
   private static final List<MeteoritosCliente.Impacto> IMPACTOS = new ArrayList<>();
   private static SoundInstance ambiente;
   private static float destello;
   private static float polvo;
   private static long destelloEn;
   private static float camY;

   private MeteoritosCliente() {
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
            desde = ahora - valor * 1000000000L;
            acaba = -1L;
         }
      } else if (desde >= 0L && acaba < 0L) {
         acaba = ahora;
      }
   }

   public static void impacto(int tamano, String donde) { }

   public static void limpiar() {
      desde = -1L;
      acaba = -1L;
      HUMOS.clear();
      IMPACTOS.clear();
      SILBIDOS.clear();
      ORIGEN.clear();
      ESTELAS.clear();
      FIJAS.clear();
      SUELTOS.clear();
      if (ambiente != null) {
         Minecraft.getInstance().getSoundManager().stop(ambiente);
         ambiente = null;
      }

      destello = 0.0F;
      polvo = 0.0F;
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

   public static float fuerza() {
      if (desde < 0L) {
         return 0.0F;
      }

      float f = suave(segundos() / 5.0F);
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

   public static boolean tapaCielo() {
      Minecraft mc = Minecraft.getInstance();
      return shader != null && mc.level != null && mc.level.dimensionType().hasSkyLight() && fuerza() > 0.02F;
   }

   private static float fadeDestello() {
      return (float)Math.exp(-((float)(System.nanoTime() - destelloEn) / 1.0E9F) / 0.35F);
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) { }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      limpiar();
   }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      float k = fuerza() * 0.97F;
      if (!(k <= 0.0F)) {
         float fase = suave(segundos() / 14.0F);
         float r = Mth.lerp(polvo, Mth.lerp(fase, 0.62F, 0.66F), 0.68F);
         float g = Mth.lerp(polvo, Mth.lerp(fase, 0.1F, 0.04F), 0.12F);
         float b = Mth.lerp(polvo, Mth.lerp(fase, 0.12F, 0.04F), 0.08F);
         e.setRed(Mth.lerp(k, e.getRed(), r));
         e.setGreen(Mth.lerp(k, e.getGreen(), g));
         e.setBlue(Mth.lerp(k, e.getBlue(), b));
      }
   }

   @SubscribeEvent
   public static void alDibujar(RenderLevelStageEvent e) { }

   private static Object usar(float modo, float t, float semilla, boolean sumar) { return null; }

   private static void cielo(float lejos, float t, float fuerza) { }

   private static float r0(float tam) {
      return 4.2F * tam;
   }

   private static void meteorito(MeteoritoEntity m, Vec3 cam, float pt, float t, Camera camara) { }

   private static void humo(MeteoritosCliente.Estela e, Vec3 cam, float t, boolean bruma) { }

   private static void funda(MeteoritosCliente.Estela e, Vec3 cam, float t) { }

   private static float frac(float x) {
      return x - (float)Math.floor(x);
   }

   private static double smooth01(double x) {
      double c = Math.max(0.0, Math.min(1.0, x));
      return c * c * (3.0 - 2.0 * c);
   }

   private static void esfera(Vector3f c, float r, int nu, int nv, float sem) { }

   private static double irregular(double th, double ph, float sem) {
      double x = Math.sin(ph) * Math.cos(th);
      double y = Math.cos(ph);
      double z = Math.sin(ph) * Math.sin(th);
      return 1.0;
   }

   private static Vector3f puntoRoca(double th, double ph, float r, float sem) {
      double k = r * irregular(th, ph, sem);
      return new Vector3f((float)(Math.sin(ph) * Math.cos(th) * k), (float)(Math.cos(ph) * k), (float)(Math.sin(ph) * Math.sin(th) * k));
   }

   private static void vertEsfera(Object b, Vector3f c, float r, int i, int j, int nu, int nv, float sem) {
      double th = (double)i / nu * Math.PI * 2.0;
      double ph = Math.min(Math.max((double)j / nv * Math.PI, 0.001), 3.1405926535897932);
      Vector3f p = puntoRoca(th, ph, r, sem);
      double e = 0.001;
      Vector3f dth = puntoRoca(th + e, ph, r, sem).sub(puntoRoca(th - e, ph, r, sem));
      Vector3f dph = puntoRoca(th, ph + e, r, sem).sub(puntoRoca(th, ph - e, r, sem));
      Vector3f n = dth.cross(dph, new Vector3f());
      if (n.lengthSquared() < 1.0E-12F) {
         n.set(p);
      }

      n.normalize();
      if (n.dot(p) < 0.0F) {
         n.negate();
      }

      b.addVertex(c.x + p.x, c.y + p.y, c.z + p.z).setColor(n.x * 0.5F + 0.5F, n.y * 0.5F + 0.5F, n.z * 0.5F + 0.5F, 1.0F).setUv((float)i / nu, (float)j / nv);
   }

   private static void tubo(
      Vector3f cabeza, Vector3f dir, float largo, int nu, int nv, float potencia, float escalaUV, DoubleUnaryOperator radio, DoubleUnaryOperator alfa
   ) { }

   private static void vertTubo(
      Object b,
      Vector3f cabeza,
      Vector3f a,
      Vector3f e1,
      Vector3f e2,
      float largo,
      float potencia,
      float escalaUV,
      DoubleUnaryOperator radio,
      DoubleUnaryOperator alfa,
      int i,
      int j,
      int nu,
      int nv
   ) { }

   private static void tapas(Vector3f cabeza, Vector3f cola, float largo, float rm) { }

   private static void gota(Vector3f cabeza, Vector3f dir, float largo, float ancho) { }

   private static void cinta(Vector3f cabeza, Vector3f dir, float largo, float ancho, float alfa) { }

   private static void cartel(Vector3f c, float r, Camera cam, float cr, float cg, float cb, float a) { }

   private static void plano(float r, float g, float bl, float a, boolean sumar) { }

   private static final class Bocanada {
      final double x0;
      final double y0;
      final double z0;
      final float vx;
      final float vy;
      final float vz;
      final float tam0;
      final float tam1;
      final float calor0;
      final float osc;
      final float tauCalor;
      final float semilla;
      final float opacidad;
      final float dura;
      int met = -1;
      long fin = 0L;
      boolean estela;
      final long nace = System.nanoTime();

      Bocanada(
         double x,
         double y,
         double z,
         float vx,
         float vy,
         float vz,
         float tam0,
         float tam1,
         float calor0,
         float osc,
         float tauCalor,
         float semilla,
         float opacidad,
         float dura
      ) {
         this.dura = dura;
         this.x0 = x;
         this.y0 = y;
         this.z0 = z;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         this.tam0 = tam0;
         this.tam1 = tam1;
         this.calor0 = calor0;
         this.osc = osc;
         this.tauCalor = tauCalor;
         this.semilla = semilla;
         this.opacidad = opacidad;
      }

      float edad() {
         return (float)(System.nanoTime() - this.nace) / 1.0E9F;
      }

      void aniadir(List<float[]> lista, Vec3 cam) {
         float e = this.edad();
         float alfa = Mth.clamp(e / 0.15F, 0.0F, 1.0F) * (1.0F - MeteoritosCliente.suave((e - this.dura * 0.55F) / (this.dura * 0.45F))) * this.opacidad;
         if (!(alfa <= 0.004F)) {
            if (this.estela) {
               float g = Mth.clamp(e / 6.0F, 0.0F, 1.0F);
               float f = 0.55F * Mth.clamp(e / 10.0F, 0.0F, 1.0F);
               float ap = Mth.clamp(e / 0.06F, 0.0F, 1.0F);
               if (this.fin != 0L) {
                  ap *= 1.0F - MeteoritosCliente.suave((float)(System.nanoTime() - this.fin) / 3.0E9F);
               }

               if (!(ap <= 0.004F)) {
                  lista.add(
                     new float[]{
                        (float)(this.x0 + this.vx * e - cam.x),
                        (float)(this.y0 + this.vy * e - cam.y),
                        (float)(this.z0 + this.vz * e - cam.z),
                        this.tam0 + (this.tam1 - this.tam0) * (float)Math.sqrt(g),
                        f,
                        this.semilla,
                        0.0F,
                        ap * this.opacidad,
                        1.0F
                     }
                  );
               }
            } else {
               float tam = this.tam0 + (this.tam1 - this.tam0) * (1.0F - (float)Math.exp(-e / 6.5));
               float calor = this.calor0 * (float)Math.exp(-e / this.tauCalor);
               float oscuro = Math.min(1.0F, this.osc + 0.45F * MeteoritosCliente.suave((e - 4.0F) / 18.0F));
               lista.add(
                  new float[]{
                     (float)(this.x0 + this.vx * e - cam.x),
                     (float)(this.y0 + this.vy * e - cam.y),
                     (float)(this.z0 + this.vz * e - cam.z),
                     tam,
                     calor,
                     this.semilla,
                     oscuro,
                     alfa
                  }
               );
            }
         }
      }
   }

   private static final class Estela {
      final Vec3 origen;
      Vec3 fin;
      final float r;
      final float semilla;
      long fija = -1L;

      Estela(Vec3 origen, float r, float semilla) {
         this.origen = origen;
         this.fin = origen;
         this.r = r;
         this.semilla = semilla;
      }
   }

   private static final class Impacto {
      final Vec3 centro;
      final long nace = System.nanoTime();
      final float escala;
      final float semilla;
      final float lx;
      final float lz;
      final float[][] domo;
      final float[][] suelo;
      final float[][] columna;
      final float[][] brasas;

      Impacto(Vec3 centro, float escala) {
         this.centro = centro;
         this.escala = Math.max(0.5F, escala);
         Random r = new Random(Double.doubleToLongBits(centro.x * 31.0 + centro.z * 17.0) ^ System.nanoTime());
         this.semilla = r.nextFloat() * 50.0F;
         double inc = r.nextDouble() * Math.PI * 2.0;
         this.lx = (float)Math.cos(inc) * 0.28F;
         this.lz = (float)Math.sin(inc) * 0.28F;
         this.domo = new float[20][];

         for (int i = 0; i < this.domo.length; i++) {
            double u = 0.1 + 0.9 * r.nextDouble();
            double th = r.nextDouble() * Math.PI * 2.0;
            double sq = Math.sqrt(1.0 - u * u);
            float fr = (float)Math.cbrt(r.nextDouble());
            this.domo[i] = new float[]{(float)(sq * Math.cos(th)), (float)u, (float)(sq * Math.sin(th)), fr, 14.0F + r.nextFloat() * 10.0F, r.nextFloat()};
         }

         this.suelo = new float[26][];

         for (int i = 0; i < this.suelo.length; i++) {
            this.suelo[i] = new float[]{
               r.nextFloat() * 6.283F, 0.3F + 0.7F * (float)Math.sqrt(r.nextFloat()), r.nextFloat() * 3.0F, 16.0F + r.nextFloat() * 12.0F, r.nextFloat()
            };
         }

         this.columna = new float[24][];

         for (int i = 0; i < this.columna.length; i++) {
            this.columna[i] = new float[]{
               (i + r.nextFloat()) / 24.0F, r.nextFloat() * 6.283F, (float)Math.sqrt(r.nextFloat()), 8.0F + r.nextFloat() * 6.0F, r.nextFloat()
            };
         }

         this.brasas = new float[70][];

         for (int i = 0; i < this.brasas.length; i++) {
            double th = r.nextDouble() * Math.PI * 2.0;
            double d = 4.0 + 66.0 * Math.sqrt(r.nextDouble());
            this.brasas[i] = new float[]{
               (float)(Math.cos(th) * d),
               1.0F + r.nextFloat() * 30.0F,
               (float)(Math.sin(th) * d),
               1.0F + r.nextFloat() * 3.0F,
               r.nextFloat() * 6.28F,
               0.5F + r.nextFloat()
            };
         }
      }

      float edad() {
         return (float)(System.nanoTime() - this.nace) / 1.0E9F;
      }

      private float alfa(float e) {
         return Mth.clamp(e / 0.2F, 0.0F, 1.0F) * (1.0F - MeteoritosCliente.suave((e - 13.0F) / 11.0F));
      }

      void fondo(Vec3 cam, Camera camara, float t) { }

      void bocanadas(List<float[]> lista, Vec3 cam) {
         float e = this.edad();
         float s = this.escala;
         float al = this.alfa(e);
         if (!(al <= 0.004F)) {
            float cx = (float)(this.centro.x - cam.x);
            float cy = (float)(this.centro.y - cam.y);
            float cz = (float)(this.centro.z - cam.z);
            float calor = (float)Math.exp(-e / 9.0);
            float osc = 0.04F + 0.32F * MeteoritosCliente.suave(e / 20.0F);
            float radio = 6.0F + 20.0F * s * (1.0F - (float)Math.exp(-e / 3.2F));

            for (float[] q : this.domo) {
               float rr = radio * q[3];
               lista.add(
                  new float[]{
                     cx + q[0] * rr,
                     cy + 2.0F + q[1] * rr * 0.55F + e * 0.2F,
                     cz + q[2] * rr,
                     q[4] * s * (0.55F + 0.02F * e),
                     calor * (1.0F - 0.5F * q[3]),
                     q[5],
                     osc,
                     al * 0.16F
                  }
               );
            }

            float rg = 6.0F + 28.0F * s * (1.0F - (float)Math.exp(-e / 2.4F));

            for (float[] q : this.suelo) {
               float rr = rg * q[1];
               lista.add(
                  new float[]{
                     cx + Mth.cos(q[0] + e * 0.03F) * rr,
                     cy + 2.0F + q[2] * s * 0.6F,
                     cz + Mth.sin(q[0] + e * 0.03F) * rr,
                     q[3] * s * (0.5F + 0.02F * e),
                     calor * 0.5F,
                     q[4],
                     osc + 0.05F,
                     al * 0.17F
                  }
               );
            }

            float alto = 0.0F;

            for (float[] q : new float[0][]) {
               float hh = alto * q[0];
               float ancho = (3.0F + 12.0F * q[0]) * s * q[2];
               float giro = q[1] + e * (0.15F + 0.15F * q[0]);
               lista.add(
                  new float[]{
                     cx + this.lx * hh + Mth.cos(giro) * ancho,
                     cy + hh,
                     cz + this.lz * hh + Mth.sin(giro) * ancho,
                     q[3] * s * (0.9F + 0.9F * q[0]),
                     calor * (1.0F - 0.55F * q[0]),
                     q[4],
                     0.1F + 0.5F * q[0] * q[0],
                     al * 0.58F
                  }
               );
            }

            float fb = al * (1.0F - MeteoritosCliente.suave((e - 12.0F) / 10.0F)) * Mth.clamp(e / 0.8F, 0.0F, 1.0F);
            if (fb > 0.01F) {
               for (float[] q : this.brasas) {
                  float par = 0.55F + 0.45F * Mth.sin(e * 5.0F * q[5] + q[4]);
                  float alt = q[1] + q[3] * e;
                  lista.add(
                     new float[]{
                        cx + q[0] + Mth.sin(e * 0.7F + q[4]) * 2.0F,
                        cy + alt,
                        cz + q[2] + Mth.cos(e * 0.6F + q[4]) * 2.0F,
                        0.3F * (0.6F + 0.6F * q[5]),
                        1.0F,
                        q[4] * 0.1F,
                        0.0F,
                        fb * par
                     }
                  );
               }
            }
         }
      }

      private static void anilloSuelo(float cx, float cy, float cz, float r0, float r1, float a) { }
   }
}
