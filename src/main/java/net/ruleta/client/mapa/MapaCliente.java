package net.ruleta.client.mapa;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.dedsafio.DedsafioCielo;

public final class MapaCliente {
   public static final KeyMapping TECLA = new KeyMapping("key.ruletafanproject.mapa", Type.KEYSYM, 77, "key.categories.ruletafanproject");

   private MapaCliente() {
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         boolean prohibido = mc.level != null && DedsafioCielo.enDimension();
         if (prohibido && mc.screen instanceof PantallaMapa) {
            mc.setScreen(null);
         }

         while (TECLA.consumeClick()) {
            if (!prohibido && mc.level != null && mc.player != null && mc.screen == null) {
               mc.setScreen(new PantallaMapa());
            }
         }
      }
   }
}
