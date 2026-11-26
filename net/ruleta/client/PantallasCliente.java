package net.ruleta.client;

import net.minecraft.client.Minecraft;

public final class PantallasCliente {
   private PantallasCliente() {
   }

   public static void abrir(String nombre, String datos) {
      Minecraft mc = Minecraft.getInstance();
      switch (nombre) {
         case "ajustes":
            mc.setScreen(new PantallaAjustes(null));
            break;
         case "bunker_borrar":
            mc.setScreen(new PantallaBunkerBorrar(null, datos));
            break;
         case "bunker_borrar_act":
            if (mc.screen instanceof PantallaBunkerBorrar p) {
               p.actualizar(datos);
            }
      }
   }
}
