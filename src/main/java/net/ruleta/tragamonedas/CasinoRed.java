package net.ruleta.tragamonedas;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class CasinoRed {
   private CasinoRed() {
   }

   public static void registrar() {
      PayloadTypeRegistry.playC2S().register(CasinoPedirPayload.TYPE, CasinoPedirPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(CasinoGuardarPayload.TYPE, CasinoGuardarPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(CasinoDatosPayload.TYPE, CasinoDatosPayload.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(CasinoPedirPayload.TYPE, (m, ctx) -> enviar(ctx.player()));
      ServerPlayNetworking.registerGlobalReceiver(CasinoGuardarPayload.TYPE, (m, ctx) -> guardar(ctx.player(), m.json()));
   }

   private static void enviar(ServerPlayer jugador) {
      if (jugador.hasPermissions(2)) {
         ServerPlayNetworking.send(jugador, new CasinoDatosPayload(ConfigTragamonedas.get().aJsonCompacto()));
      }
   }

   private static void guardar(ServerPlayer jugador, String json) {
      if (jugador.hasPermissions(2)) {
         ConfigTragamonedas c = ConfigTragamonedas.desdeJson(json);
         if (c != null) {
            ConfigTragamonedas.guardar(c);
            ServerPlayNetworking.send(jugador, new CasinoDatosPayload(c.aJsonCompacto()));
         }
      }
   }
}
