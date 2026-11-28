package net.ruleta.estatua;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class PedestalRed {
   private PedestalRed() {
   }

   public static void registrar() {
      PayloadTypeRegistry.serverboundPlay().register(PedestalPedirPayload.TYPE, PedestalPedirPayload.CODEC);
      PayloadTypeRegistry.serverboundPlay().register(PedestalGuardarPayload.TYPE, PedestalGuardarPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(PedestalDatosPayload.TYPE, PedestalDatosPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(PedestalPedirPayload.TYPE, (m, ctx) -> enviar(ctx.player()));
      ServerPlayNetworking.registerGlobalReceiver(PedestalGuardarPayload.TYPE, (m, ctx) -> guardar(ctx.player(), m.json()));
   }

   private static void enviar(ServerPlayer jugador) {
      if (net.ruleta.compat.Compat.permiso(jugador, 2)) {
         ServerPlayNetworking.send(jugador, new PedestalDatosPayload(ConfigPedestal.get().aJsonCompacto()));
      }
   }

   private static void guardar(ServerPlayer jugador, String json) {
      if (net.ruleta.compat.Compat.permiso(jugador, 2)) {
         ConfigPedestal c = ConfigPedestal.desdeJson(json);
         if (c != null) {
            ConfigPedestal.guardar(c);
            ServerPlayNetworking.send(jugador, new PedestalDatosPayload(c.aJsonCompacto()));
         }
      }
   }
}
