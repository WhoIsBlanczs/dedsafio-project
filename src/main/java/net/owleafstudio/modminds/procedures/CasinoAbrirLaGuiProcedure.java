package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CasinoAbrirLaGuiProcedure {
   @SubscribeEvent
   public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
      if (event.getHand() == event.getEntity().getUsedItemHand()) {
         execute(event);
      }
   }

   public static void execute() {
      execute(null);
   }

   private static void execute(@Nullable Event event) {
   }
}
