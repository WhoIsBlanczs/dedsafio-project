package net.ruleta.dedsafio;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AfterPlayerChange;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.playa.ConstructorPlaya;
import net.ruleta.playa.MapaPlaya;

public final class EscenaServidor {
   private static final double MAXIMO = 312.0;
   private static long inicioNs = -1L;
   private static boolean finHecho = false;
   private static final double FIN_CANCION = 303.3;

   private EscenaServidor() {
   }

   public static void registrar() {
      PayloadTypeRegistry.playS2C().register(EscenaPayload.TYPE, EscenaPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(DulceAzarAccionPayload.TYPE, DulceAzarAccionPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(DulceAzarAccionPayload.TYPE, (m, ctx) -> PanelDulceAzar.recibir(ctx.player(), m));
      ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((AfterPlayerChange)(jugador, origen, destino) -> alEntrar(jugador));
      ServerPlayConnectionEvents.JOIN.register((Join)(manejador, emisor, servidor) -> alEntrar(manejador.getPlayer()));
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)servidor -> {
         inicioNs = -1L;
         ComandoDedsafio.reiniciar();
         OlaServidor.reiniciar();
      });
      ServerTickEvents.END_SERVER_TICK.register(ComandoDedsafio::tick);
      ProteccionDedsafio.registrar();
      ConstructorPlaya.registrar();
      MapaPlaya.registrar();
   }

   public static void enviar(ServerPlayer p, EscenaPayload m) {
      ServerPlayNetworking.send(p, m);
   }

   public static boolean corriendo() {
      if (inicioNs < 0L) {
         return false;
      } else if ((System.nanoTime() - inicioNs) / 1.0E9 > 312.0) {
         inicioNs = -1L;
         return false;
      } else {
         return true;
      }
   }

   public static void ojo(ServerPlayer p) {
      ServerPlayNetworking.send(p, new EscenaPayload(2, 0.0F));
   }

   public static void vigilarFin(MinecraftServer servidor) {
      if (inicioNs >= 0L && !finHecho && (System.nanoTime() - inicioNs) / 1.0E9 >= 303.3) {
         finHecho = true;
         OlaServidor.invocarFinal(servidor);
      }
   }

   private static boolean enDimension(ServerPlayer p) {
      return p.level().dimension().equals(ComandoDedsafio.DIMENSION);
   }

   public static int iniciar(MinecraftServer servidor) {
      inicioNs = System.nanoTime();
      finHecho = false;
      int n = 0;

      for (ServerPlayer p : servidor.getPlayerList().getPlayers()) {
         if (enDimension(p)) {
            ServerPlayNetworking.send(p, new EscenaPayload(1, 0.0F));
            n++;
         }
      }

      return n;
   }

   public static int parar(MinecraftServer servidor) {
      inicioNs = -1L;
      finHecho = true;
      int n = 0;

      for (ServerPlayer p : servidor.getPlayerList().getPlayers()) {
         if (enDimension(p)) {
            ServerPlayNetworking.send(p, new EscenaPayload(0, 0.0F));
            n++;
         }
      }

      return n;
   }

   private static void alEntrar(ServerPlayer jugador) {
      if (enDimension(jugador) && jugador.getServer() != null) {
         OlaServidor.alEntrar(jugador.getServer(), jugador);
      }

      if (corriendo() && enDimension(jugador)) {
         ServerPlayNetworking.send(jugador, new EscenaPayload(1, (float)((System.nanoTime() - inicioNs) / 1.0E9)));
      }
   }
}
