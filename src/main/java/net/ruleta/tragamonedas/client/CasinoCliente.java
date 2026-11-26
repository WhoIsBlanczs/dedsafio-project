package net.ruleta.tragamonedas.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.ruleta.tragamonedas.CasinoDatosPayload;
import net.ruleta.tragamonedas.ConfigTragamonedas;

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
         if (mc.screen instanceof PantallaCasino pantalla) {
            pantalla.alRecibir(c);
         } else {
            mc.setScreenAndShow(new PantallaCasino(mc.screen, c));
         }
      }
   }
}
