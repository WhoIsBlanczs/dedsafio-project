package net.ruleta.client.tornado;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.tornado.TornadoPayload;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class TornadoCliente {
   private static final float[] NIEBLA = new float[]{0.31F, 0.39F, 0.48F};
   private static final int ANILLOS = 48;
   private static final int LADOS = 28;
   private static final SoundEvent S_LLUVIA = SoundEvents.WEATHER_RAIN_ABOVE;
   private static final SoundEvent S_VIENTO = sonido("tormenta.viento");
   private static final SoundEvent S_TORNADO = sonido("tormenta.tornado");
   private static final SoundEvent S_TORNADO_CERCA = sonido("tormenta.tornado_cerca");
   private static final SoundEvent S_ESCOMBROS = sonido("tormenta.escombros");
   private static TornadoCliente.Bucle lluvia;
   private static TornadoCliente.Bucle viento;
   private static final Map<Integer, TornadoCliente.Bucle[]> RUGIDOS = new HashMap<>();
   private static Object shaderCielo;
   private static Object shaderEmbudo;
   private static boolean cielo;
   private static final List<TornadoCliente.Torn> TORNADOS = new ArrayList<>();
   private static int[] atrapados = new int[0];
   private static int ticksAtrapado;
   private static float nivel;
   private static long ultimo;
   private static float rayo;
   private static long proximoRayo;
   private static long tickCliente;
   private static double[] pendiente;
   private static int tickPart;
   private static float semillaCapa;
   private static float tiempoCapa;

   private static SoundEvent sonido(String id) {
      return SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("ruleta", id));
   }

   private TornadoCliente() {
   }

   public static void shaderCielo(Object s) {
      shaderCielo = s;
   }

   public static void shaderEmbudo(Object s) {
      shaderEmbudo = s;
   }

   public static boolean atrapado() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return false;
      }

      int id = mc.player.getId();

      for (int a : atrapados) {
         if (a == id) {
            return true;
         }
      }

      return false;
   }

   public static void recibir(TornadoPayload p) {
      cielo = p.cielo();
      atrapados = p.atrapados() == null ? new int[0] : p.atrapados();
      long ahora = tickCliente;
      TORNADOS.removeIf(tx -> p.tornados().stream().noneMatch(nx -> nx.id() == tx.id));

      for (TornadoPayload.Torn n : p.tornados()) {
         TornadoCliente.Torn t = null;

         for (TornadoCliente.Torn x : TORNADOS) {
            if (x.id == n.id()) {
               t = x;
               break;
            }
         }

         if (t == null) {
            t = new TornadoCliente.Torn();
            t.id = n.id();
            TORNADOS.add(t);
         }

         t.dim = n.dimension();
         t.x = n.pos().x;
         t.y = n.pos().y;
         t.z = n.pos().z;
         t.radio = n.radio();
         t.alto = n.alto();
         t.vx = n.vx();
         t.vz = n.vz();
         t.edad = n.edad();
         t.recibido = ahora;
      }
   }

   public static void limpiar() {
      cielo = false;
      atrapados = new int[0];
      ticksAtrapado = 0;
      TORNADOS.clear();
      nivel = 0.0F;
      rayo = 0.0F;
   }

   public static float nivel() {
      return nivel;
   }

   public static float oscuridad() {
      return nivel * 0.2F;
   }

   public static boolean tapaCielo() {
      return nivel > 0.97F;
   }

   public static float cercania() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && !TORNADOS.isEmpty()) {
         double mejor = Double.MAX_VALUE;

         for (TornadoCliente.Torn t : TORNADOS) {
            if (t.dim.equals(mc.level.dimension().identifier().toString())) {
               double d = Math.hypot(mc.player.getX() - t.x, mc.player.getZ() - t.z) - t.radio;
               mejor = Math.min(mejor, d / (t.radio * 3.0 + 80.0));
            }
         }

         return mejor == Double.MAX_VALUE ? 0.0F : (float)Math.max(0.0, Math.min(1.0, 1.0 - mejor));
      } else {
         return 0.0F;
      }
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) { }

   private static boolean sonando(Minecraft mc, TornadoCliente.Bucle b) {
      return b != null && mc.getSoundManager().isActive(b);
   }

   private static void sonidosAmbiente(Minecraft mc) {
      if (nivel > 0.02F) {
         if (!sonando(mc, lluvia)) {
            lluvia = new TornadoCliente.Bucle(S_LLUVIA, 1.0F, () -> {
               Minecraft m = Minecraft.getInstance();
               boolean abierto = m.player != null && m.level != null && m.level.canSeeSky(m.player.blockPosition());
               boolean llueve = m.level != null && m.level.getRainLevel(1.0F) > 0.3F;
               return llueve ? 0.0 : nivel * (abierto ? 1.0F : 0.35F);
            }, () -> nivel > 0.001F, null);
            mc.getSoundManager().play(lluvia);
         }

         if (!sonando(mc, viento)) {
            viento = new TornadoCliente.Bucle(S_VIENTO, 1.0F, () -> {
               Minecraft m = Minecraft.getInstance();
               boolean abierto = m.player != null && m.level != null && m.level.canSeeSky(m.player.blockPosition());
               float mas = TORNADOS.isEmpty() ? 0.0F : 0.35F;
               return nivel * (0.45F + mas) * (abierto ? 1.0F : 0.4F);
            }, () -> nivel > 0.001F, null);
            mc.getSoundManager().play(viento);
         }
      }

      String dim = mc.level.dimension().identifier().toString();

      for (TornadoCliente.Torn t : TORNADOS) {
         if (t.dim.equals(dim)) {
            TornadoCliente.Bucle[] par = RUGIDOS.get(t.id);
            if (par == null || !sonando(mc, par[0])) {
               int id = t.id;
               BooleanSupplier vivo = () -> TORNADOS.stream().anyMatch(x -> x.id == id);
               Supplier<Vec3> donde = () -> fuenteSonido(id);
               par = new TornadoCliente.Bucle[]{
                  new TornadoCliente.Bucle(S_TORNADO, 1.0F, () -> 12.0, vivo, donde), new TornadoCliente.Bucle(S_TORNADO_CERCA, 1.0F, () -> 4.0, vivo, donde)
               };
               RUGIDOS.put(id, par);
               mc.getSoundManager().play(par[0]);
               mc.getSoundManager().play(par[1]);
            }
         }
      }

      RUGIDOS.keySet().removeIf(idx -> TORNADOS.stream().noneMatch(x -> x.id == idx));
   }

   private static Vec3 fuenteSonido(int id) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return null;
      }

      for (TornadoCliente.Torn t : TORNADOS) {
         if (t.id == id) {
            Vec3 p = posicion(t, 0.0F);
            double dx = mc.player.getX() - p.x;
            double dz = mc.player.getZ() - p.z;
            double d = Math.sqrt(dx * dx + dz * dz);
            double r0 = t.radio * 0.45;
            double y = Mth.clamp(mc.player.getY(), p.y + 4.0, p.y + t.alto * 0.4);
            if (!(d <= r0) && !(d < 0.001)) {
               return new Vec3(p.x + dx / d * r0, y, p.z + dz / d * r0);
            }

            return new Vec3(mc.player.getX(), y, mc.player.getZ());
         }
      }

      return null;
   }

   private static Vec3 posicion(TornadoCliente.Torn t, float pt) {
      float dt = Math.min(6.0F, (float)(tickCliente - t.recibido) + pt);
      return new Vec3(t.x + t.vx * dt, t.y, t.z + t.vz * dt);
   }

   @SubscribeEvent
   public static void alDibujarMundo(RenderLevelStageEvent e) { }

   private static float radioEn(float R, float v) {
      return R * (0.06F + 1.55F * v + 1.1F * (float)Math.pow(v, 5.0) + 0.3F * (float)Math.exp(-v * 18.0));
   }

   private static Vec3 ejeEn(TornadoCliente.Torn t, float v, float tiempo) {
      float R = t.radio;
      float v15 = (float)Math.pow(v, 1.5);
      double ox = Math.sin(v * 3.1 + tiempo * 0.55 + t.id * 1.7) * R * 0.3 * v15 + Math.sin(v * 7.0 - tiempo * 1.1) * R * 0.06 * v;
      double oz = Math.cos(v * 2.7 + tiempo * 0.47 + t.id * 2.3) * R * 0.3 * v15 + Math.cos(v * 6.0 - tiempo * 0.9) * R * 0.06 * v;
      double vel = Math.sqrt(t.vx * t.vx + t.vz * t.vz);
      if (vel > 1.0E-4) {
         double atras = t.alto * 0.14 * Math.pow(v, 1.3);
         ox -= t.vx / vel * atras;
         oz -= t.vz / vel * atras;
      }

      return new Vec3(ox, v * t.alto, oz);
   }

   private static void dibujarEmbudo(Object sh, TornadoCliente.Torn t, float pt, Vec3 cam, float tiempo) { }

   private static void cono(TornadoCliente.Torn t, double dx, double dy, double dz, float tiempo, boolean polvo, float escala) { }

   private static void vertice(Object b, double dx, double dy, double dz, float ex, float ey, float ez, float r, float ang, float u, float v) {
      float irregular = 1.0F
         + 0.0F
            * (
               0.1F * Mth.sin(ang * 5.0F + v * 9.0F + tiempoCapa * 0.9F + semillaCapa)
                  + 0.07F * Mth.sin(ang * 9.0F - v * 17.0F - tiempoCapa * 1.3F + semillaCapa * 2.0F)
                  + 0.04F * Mth.sin(ang * 17.0F + v * 31.0F + tiempoCapa * 2.1F)
            );
      float rr = r * irregular;
      float c = Mth.cos(ang);
      float s = Mth.sin(ang);
      b.addVertex((float)(dx + ex + c * rr), (float)(dy + ey), (float)(dz + ez + s * rr)).setUv(u, v).setColor(c * 0.5F + 0.5F, s * 0.5F + 0.5F, 0.0F, 1.0F);
   }

   private static void cubo(float s) { }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      if (!(nivel <= 0.0F)) {
         float k = nivel * 0.95F;
         e.setRed(Mth.lerp(k, e.getRed(), NIEBLA[0] * 0.85F));
         e.setGreen(Mth.lerp(k, e.getGreen(), NIEBLA[1] * 0.85F));
         e.setBlue(Mth.lerp(k, e.getBlue(), NIEBLA[2] * 0.85F));
      }
   }

   @SubscribeEvent
   public static void alNiebla(ViewportEvent.RenderFog e) { }

   @SubscribeEvent
   public static void alCamara(ViewportEvent.ComputeCameraAngles e) {
      if (ticksAtrapado > 0) {
         double t = ticksAtrapado + e.getPartialTick();
         float entra = (float)Math.min(1.0, t / 30.0);
         e.setRoll(e.getRoll() + entra * (14.0F * (float)Math.sin(t * 0.11) + 2.2F * (float)Math.sin(t * 1.7)));
         e.setPitch(e.getPitch() + entra * 2.0F * (float)Math.sin(t * 0.9 + 1.3));
         e.setYaw(e.getYaw() + entra * 1.5F * (float)Math.sin(t * 0.7));
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      limpiar();
   }

   private static final class Bucle extends AbstractTickableSoundInstance {
      private final DoubleSupplier volumen;
      private final BooleanSupplier vivo;
      private final Supplier<Vec3> fuente;

      Bucle(SoundEvent ev, float tono, DoubleSupplier volumen, BooleanSupplier vivo, Supplier<Vec3> fuente) {
         super(ev, SoundSource.WEATHER, RandomSource.create());
         this.volumen = volumen;
         this.vivo = vivo;
         this.fuente = fuente;
         this.looping = true;
         this.delay = 0;
         this.pitch = tono;
         this.volume = 0.0F;
         if (fuente == null) {
            this.relative = true;
            this.attenuation = Attenuation.NONE;
         } else {
            this.attenuation = Attenuation.LINEAR;
         }
      }

      public void tick() {
         if (!this.vivo.getAsBoolean()) {
            this.stop();
         } else {
            this.volume = (float)this.volumen.getAsDouble();
            if (this.fuente != null) {
               Vec3 p = this.fuente.get();
               if (p != null) {
                  this.x = p.x;
                  this.y = p.y;
                  this.z = p.z;
               }
            }
         }
      }

      public boolean canStartSilent() {
         return true;
      }
   }

   private static final class Torn {
      int id;
      String dim;
      double x;
      double y;
      double z;
      float radio;
      float alto;
      float vx;
      float vz;
      long recibido;
      int edad;
   }
}
