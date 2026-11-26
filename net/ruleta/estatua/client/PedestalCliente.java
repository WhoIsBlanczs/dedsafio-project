package net.ruleta.estatua.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.ruleta.estatua.ConfigPedestal;
import net.ruleta.estatua.PedestalDatosPayload;

@Environment(EnvType.CLIENT)
public final class PedestalCliente {
   private PedestalCliente() {
   }

   public static void registrar() {
      ClientPlayNetworking.registerGlobalReceiver(PedestalDatosPayload.TYPE, (m, ctx) -> recibir(m.json()));
   }

   private static void recibir(String json) {
      ConfigPedestal c = ConfigPedestal.desdeJson(json);
      if (c != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.screen instanceof PantallaPedestal pantalla) {
            pantalla.alRecibir(c);
         } else {
            mc.setScreen(new PantallaPedestal(mc.screen, c));
         }
      }
   }
}
