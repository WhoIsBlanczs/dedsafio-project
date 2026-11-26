package net.ruleta.dedsafio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class OlaServidor {
   public static final double DANO = 2.0;
   public static final int DURACION_TICKS = 110;
   public static final int ESPERA_OJO_TICKS = 30;
   private static final double SUBIDA_POR_TICK = 0.24;
   private static final int TICKS_SUBIENDO = 90;
   private static final double GROSOR = 80.0;
   private static final double NIVEL_AGUA = -2.2;
   public static final float ALTURA = 28.0F;
   public static final float LARGO = 158.0F;
   public static final float VELOCIDAD = 5.5F;
   private static OlaServidor.Ola ola;
   public static final float FINAL_X = -38.5F;
   public static final float FINAL_LARGO = 210.0F;
   public static final float FINAL_Z = 95.0F;
   private static final Map<UUID, OlaServidor.Tragado> tragados = new HashMap<>();

   private OlaServidor() {
   }

   public static void reiniciar() {
      ola = null;
      tragados.clear();
   }

   public static int invocarFinal(MinecraftServer servidor) {
      OlaServidor.Ola o = new OlaServidor.Ola(-38.5F, 95.0F, 28.0F, 210.0F, 5.5F, servidor.getTickCount());
      o.real = true;
      return lanzar(servidor, o);
   }

   private static int lanzar(MinecraftServer servidor, OlaServidor.Ola nueva) {
      ola = nueva;
      int n = 0;

      for (ServerPlayer p : servidor.getPlayerList().getPlayers()) {
         if (p.level().dimension().equals(nueva.dim)) {
            enviarOla(p, 0.0F);
            n++;
         }
      }

      return n;
   }

   public static int parar(MinecraftServer servidor) {
      int n = 0;
      if (ola != null) {
         for (ServerPlayer p : servidor.getPlayerList().getPlayers()) {
            EscenaServidor.enviar(p, new EscenaPayload(5, 0.0F));
            n++;
         }
      }

      ola = null;
      return n;
   }

   public static void alSalir(ServerPlayer p) {
      if (ola != null) {
         EscenaServidor.enviar(p, new EscenaPayload(5, 0.0F));
      }
   }

   private static void enviarOla(ServerPlayer p, float transcurrido) {
      OlaServidor.Ola o = ola;
      if (o != null) {
         EscenaServidor.enviar(p, new EscenaPayload(3, transcurrido, o.altura, o.largo, o.velocidad, o.x, o.z0, o.baseY, o.yaw));
      }
   }

   public static void alEntrar(MinecraftServer servidor, ServerPlayer p) {
      if (ola != null && p.level().dimension().equals(ola.dim)) {
         enviarOla(p, (float)(servidor.getTickCount() - ola.tick0) / 20.0F);
      }
   }

   public static void tick(MinecraftServer servidor) {
      long ahora = servidor.getTickCount();
      OlaServidor.Ola o = ola;
      if (o != null && tragados.isEmpty()) {
         ServerLevel dim = servidor.getLevel(o.dim);
         if (dim == null || dim.players().isEmpty()) {
            parar(servidor);
            return;
         }
      }

      if (o != null) {
         double avance = o.avance(ahora);
         if (avance > (o.real ? 430.0 : 770.0)) {
            ola = null;
         } else {
            ServerLevel nivel = servidor.getLevel(o.dim);
            double suelo = Float.isNaN(o.baseY) ? -2.2 : o.baseY;
            boolean fuera = !o.dim.equals(ComandoDedsafio.DIMENSION);
            if (nivel != null) {
               for (ServerPlayer p : nivel.players()) {
                  if (!p.isSpectator() && !o.tragados.contains(p.getUUID()) && !tragados.containsKey(p.getUUID())) {
                     double rx = p.getX() - o.x;
                     double rz = p.getZ() - o.z0;
                     double fw = rx * o.dirX() + rz * o.dirZ();
                     double lado = Math.abs(-rx * o.dirZ() + rz * o.dirX());
                     double atras = avance - fw;
                     if (lado <= o.largo / 2.0 && fw >= 0.0 && atras >= 0.0 && atras <= 80.0 && p.getY() < suelo + o.altura - 2.0) {
                        o.tragados.add(p.getUUID());
                        tragar(p, o.real);
                     }
                  }
               }
            }
         }
      }

      if (!tragados.isEmpty()) {
         List<UUID> terminados = new ArrayList<>();

         for (Entry<UUID, OlaServidor.Tragado> e : tragados.entrySet()) {
            ServerPlayer p = servidor.getPlayerList().getPlayer(e.getKey());
            OlaServidor.Tragado t = e.getValue();
            if (p == null || t.vuelve && !p.level().dimension().equals(ComandoDedsafio.DIMENSION)) {
               if (p != null) {
                  p.setNoGravity(false);
               }

               terminados.add(e.getKey());
            } else {
               t.ticks++;
               if (t.ticks <= 110) {
                  p.setNoGravity(true);
                  p.setDeltaMovement(new Vec3(0.0, t.vuelve ? 0.0 : 0.06, -0.04));
                  p.needsSync = true;
                  p.fallDistance = 0.0F;
                  p.setAirSupply(p.getMaxAirSupply());
               } else if (!t.vuelve) {
                  p.setNoGravity(false);
                  p.setDeltaMovement(Vec3.ZERO);
                  terminados.add(e.getKey());
               } else if (t.ticks == 111) {
                  EscenaServidor.ojo(p);
               } else if (t.ticks >= 140) {
                  p.fallDistance = 0.0F;
                  p.setNoGravity(false);
                  p.setDeltaMovement(Vec3.ZERO);
                  ComandoDedsafio.devolver(servidor, p);
                  terminados.add(e.getKey());
               }
            }
         }

         for (UUID id : terminados) {
            tragados.remove(id);
         }
      }
   }

   private static void tragar(ServerPlayer p, boolean vuelve) {
      float dano = 2.0F;
      if (dano > 0.0F) {
         p.hurt(p.level().damageSources().generic(), dano);
      }

      p.setAirSupply(p.getMaxAirSupply());
      OlaServidor.Tragado tr = new OlaServidor.Tragado(p.getY());
      tr.vuelve = vuelve;
      tragados.put(p.getUUID(), tr);
      EscenaServidor.enviar(p, new EscenaPayload(4, 0.0F, 5.5F, vuelve ? 1.0F : 0.0F, 0.0F, 0.0F, 0.0F));
   }

   private static final class Ola {
      final float x;
      final float z0;
      final float altura;
      final float largo;
      final float velocidad;
      final long tick0;
      ResourceKey<Level> dim = ComandoDedsafio.DIMENSION;
      float yaw = 180.0F;
      float baseY = Float.NaN;
      boolean real;
      final Set<UUID> tragados = new HashSet<>();

      Ola(float x, float z0, float altura, float largo, float velocidad, long tick0) {
         this.x = x;
         this.z0 = z0;
         this.altura = altura;
         this.largo = largo;
         this.velocidad = velocidad;
         this.tick0 = tick0;
      }

      double avance(long tickAhora) {
         return this.velocidad * (float)(tickAhora - this.tick0) / 20.0;
      }

      double dirX() {
         return -Math.sin(Math.toRadians(this.yaw));
      }

      double dirZ() {
         return Math.cos(Math.toRadians(this.yaw));
      }
   }

   private static final class Tragado {
      int ticks;
      final double y0;
      boolean vuelve = true;

      Tragado(double y0) {
         this.y0 = y0;
      }
   }
}
