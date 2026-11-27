package net.ruleta.client.cosmeticos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.cosmeticos.EstadoPayload;
import org.joml.Vector3f;

public final class CosmeticosCliente {
   public static final Map<UUID, CosmeticosCliente.Estado> ESTADOS = new HashMap<>();
   private static final Map<String, Integer> COLORES = Map.ofEntries(
      Map.entry("argentina", 7711451),
      Map.entry("bolivia", 13970206),
      Map.entry("brasil", 39995),
      Map.entry("chile", 13970206),
      Map.entry("colombia", 16568598),
      Map.entry("costa_rica", 13504806),
      Map.entry("cuba", 13571115),
      Map.entry("ecuador", 16768256),
      Map.entry("el_salvador", 1001391),
      Map.entry("espana", 12978974),
      Map.entry("guatemala", 4822992),
      Map.entry("honduras", 29647),
      Map.entry("mexico", 13504806),
      Map.entry("nicaragua", 26566),
      Map.entry("panama", 13766708),
      Map.entry("paraguay", 13970206),
      Map.entry("peru", 14225443),
      Map.entry("puerto_rico", 15532032),
      Map.entry("republica_dominicana", 13504806),
      Map.entry("uruguay", 14504),
      Map.entry("venezuela", 13571115)
   );

   private CosmeticosCliente() {
   }

   public static void recibir(EstadoPayload m) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level == null) {
         long ahora = 0L;
      } else {
         mc.level.getGameTime();
      }

      CosmeticosCliente.Estado e = ESTADOS.computeIfAbsent(m.id(), k -> new CosmeticosCliente.Estado());
      e.aura = m.aura();
      if (m.vacio()) {
         ESTADOS.remove(m.id());
      }
   }

   public static void registrar() {
   }

   private static void particula(Minecraft mc, Player p, Random r, ParticleOptions tipo, float escala, int vida) {
      double a = r.nextDouble() * Math.PI * 2.0;
      double d = 0.45 + r.nextDouble() * 0.6;
      Particle part = mc.particleEngine
         .createParticle(
            tipo,
            p.getX() + Math.cos(a) * d,
            p.getY() + 0.1 + r.nextDouble() * 1.9,
            p.getZ() + Math.sin(a) * d,
            (r.nextDouble() - 0.5) * 0.004,
            0.004,
            (r.nextDouble() - 0.5) * 0.004
         );
      if (part != null) {
         part.scale(escala);
         part.setLifetime(vida);
      }
   }

   public static final class Chispas {
      @SubscribeEvent
      public static void alTick(TickEvent.ClientTickEvent e) { }

      @SubscribeEvent
      public static void alSalirDelMundo(ClientPlayerNetworkEvent.LoggingOut e) {
         CosmeticosCliente.ESTADOS.clear();
      }
   }

   public static final class Estado {
      public String aura = "";
   }
}
