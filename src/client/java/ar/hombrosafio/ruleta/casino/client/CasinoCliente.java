package ar.hombrosafio.ruleta.casino.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import ar.hombrosafio.ruleta.casino.CasinoDatosPayload;
import ar.hombrosafio.ruleta.casino.ConfigTragamonedas;

@Environment(EnvType.CLIENT)
public final class CasinoCliente {
   private CasinoCliente() {
   }

   public static void registrar() {
      ClientPlayNetworking.registerGlobalReceiver(CasinoDatosPayload.TYPE, (m, ctx) -> recibir(m.json()));
   }

   private static void recibir(String json) {
      ConfigTragamonedas c = ConfigTragamonedas.desdeJson(json);
      if (c != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.gui.screen() instanceof PantallaCasino pantalla) {
            pantalla.alRecibir(c);
         } else {
            mc.gui.setScreen(new PantallaCasino(mc.gui.screen(), c));
         }
      }
   }
}
