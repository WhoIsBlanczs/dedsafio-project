package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class SpawnFuncionProcedure {
   @SubscribeEvent
   public static void onEntitySpawned(EntityJoinLevelEvent event) {
      execute(event, event.getLevel(), event.getEntity());
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null
         && ModmindsModVariables.MapVariables.get(world).SpawnEndermans
         && entity.level().dimension() == Level.END
         && entity instanceof EnderMan) {
         if (event != null && event.isCancelable()) {
            event.setCanceled(true);
         } else if (event != null && event.hasResult()) {
            event.setResult(Event.Result.DENY);
         }
      }
   }
}
