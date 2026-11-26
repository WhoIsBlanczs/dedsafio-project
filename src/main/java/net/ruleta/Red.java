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
import net.ruleta.bunker.BunkerPayload;
import net.ruleta.bunker.Bunkers;
import net.ruleta.candado.Candados;
import net.ruleta.candado.CodigoAbrirPayload;
import net.ruleta.candado.CodigoEnviarPayload;
import net.ruleta.candado.CodigoRespuestaPayload;
import net.ruleta.cosmeticos.EstadoPayload;
import net.ruleta.estatua.PedestalRed;
import net.ruleta.tornado.TornadoPayload;
import net.ruleta.tragamonedas.CasinoRed;

public final class Red {
   public static volatile Red.ManejadorCliente cliente = m -> {};

   private Red() {
   }

   public static void registrar() {
      PayloadTypeRegistry.playS2C().register(RuletaPayload.TYPE, RuletaPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(ReviilPayload.TYPE, ReviilPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(CambiosPayload.TYPE, CambiosPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(AgujeroPayload.TYPE, AgujeroPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(OndaPayload.TYPE, OndaPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(BunkerPayload.TYPE, BunkerPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(AjustesPayload.TYPE, AjustesPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(AbrirPantallaPayload.TYPE, AbrirPantallaPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(TornadoPayload.TYPE, TornadoPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(ZonaAvisoPayload.TYPE, ZonaAvisoPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(CodigoAbrirPayload.TYPE, CodigoAbrirPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(CodigoRespuestaPayload.TYPE, CodigoRespuestaPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(EstadoPayload.TYPE, EstadoPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(LanzarPayload.TYPE, LanzarPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(MomentoPayload.TYPE, MomentoPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(DesactivarPayload.TYPE, DesactivarPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(AjustePayload.TYPE, AjustePayload.CODEC);
      PayloadTypeRegistry.playC2S().register(BunkerAccionPayload.TYPE, BunkerAccionPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(CodigoEnviarPayload.TYPE, CodigoEnviarPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(LanzarPayload.TYPE, (m, ctx) -> Ruleta.lanzar(ctx.player(), m));
      ServerPlayNetworking.registerGlobalReceiver(MomentoPayload.TYPE, (m, ctx) -> MomentosReviil.recibir(ctx.player(), m));
      ServerPlayNetworking.registerGlobalReceiver(DesactivarPayload.TYPE, (m, ctx) -> Ruleta.desactivarCambio(ctx.player(), m.id()));
      ServerPlayNetworking.registerGlobalReceiver(AjustePayload.TYPE, (m, ctx) -> Ajustes.recibir(ctx.player(), m));
      ServerPlayNetworking.registerGlobalReceiver(BunkerAccionPayload.TYPE, (m, ctx) -> Bunkers.accion(ctx.player(), m.accion(), m.clave()));
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

   public interface ManejadorCliente {
      void recibir(Object var1);
   }
}
