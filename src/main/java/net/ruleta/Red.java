package net.ruleta;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.ruleta.agujero.AgujeroPayload;
import net.ruleta.candado.Candados;
import net.ruleta.candado.CodigoAbrirPayload;
import net.ruleta.candado.CodigoEnviarPayload;
import net.ruleta.candado.CodigoRespuestaPayload;
import net.ruleta.estatua.PedestalRed;
import net.ruleta.tragamonedas.CasinoRed;

/** Red reducida: solo los paquetes que usan los mobs, ítems y bloques del catálogo. */
public final class Red {
   private Red() {
   }

   public static void registrar() {
      PayloadTypeRegistry.clientboundPlay().register(ReviilPayload.TYPE, ReviilPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(AgujeroPayload.TYPE, AgujeroPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CodigoAbrirPayload.TYPE, CodigoAbrirPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CodigoRespuestaPayload.TYPE, CodigoRespuestaPayload.CODEC);
      PayloadTypeRegistry.serverboundPlay().register(CodigoEnviarPayload.TYPE, CodigoEnviarPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(CodigoEnviarPayload.TYPE, (m, ctx) -> Candados.recibirCodigo(ctx.player(), m));
      CasinoRed.registrar();
      PedestalRed.registrar();
   }

   public static void enviar(ServerPlayer jugador, Object mensaje) {
      ServerPlayNetworking.send(jugador, (CustomPacketPayload)mensaje);
   }

   public static void enviarATodos(MinecraftServer server, Object mensaje) {
      for (ServerPlayer p : server.getPlayerList().getPlayers()) {
         ServerPlayNetworking.send(p, (CustomPacketPayload)mensaje);
      }
   }

   public static void enviarCerca(ServerLevel nivel, double x, double y, double z, double alcance, Object mensaje) {
      for (ServerPlayer p : PlayerLookup.around(nivel, new Vec3(x, y, z), alcance)) {
         ServerPlayNetworking.send(p, (CustomPacketPayload)mensaje);
      }
   }

   public static void enviarSeguidores(Entity entidad, Object mensaje) {
      for (ServerPlayer p : PlayerLookup.tracking(entidad)) {
         ServerPlayNetworking.send(p, (CustomPacketPayload)mensaje);
      }
      if (entidad instanceof ServerPlayer yo) {
         ServerPlayNetworking.send(yo, (CustomPacketPayload)mensaje);
      }
   }
}
