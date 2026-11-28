package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class FlashbangoverlayMostrarEnElJuegoProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player);
      }
   }

   public static boolean execute(Entity entity) {
      return execute(null, entity);
   }

   private static boolean execute(@Nullable Event event, Entity entity) {
      return entity == null ? false : ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).FlashBang;
   }
}
