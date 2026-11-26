package net.ruleta;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.compat.EventPriority;
import net.ruleta.compat.LivingDeathEvent;
import net.ruleta.compat.SubscribeEvent;

public final class Muertes {
   private Muertes() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void alMorir(LivingDeathEvent e) {
      if (!e.isCanceled() && e.getEntity() instanceof ServerPlayer p && p.getServer() != null) {
         Component mensaje = p.getCombatTracker().getDeathMessage();
         Red.enviarATodos(p.getServer(), new ReviilPayload("muerte", 1, mensaje, Component.empty()));
      }
   }
}
