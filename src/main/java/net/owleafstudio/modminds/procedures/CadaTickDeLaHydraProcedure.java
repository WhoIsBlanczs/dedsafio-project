package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class CadaTickDeLaHydraProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player);
      }
   }

   public static void execute(Entity entity) {
      execute(null, entity);
   }

   private static void execute(@Nullable Event event, Entity entity) {
      if (entity != null && ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).EspaciosHydra >= 20.0) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()));
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.PANTALLA_GRIS_EFECTO.get()));
         }

         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.RED_SCREEN.get()));
         }

         double _setval = 0.0;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.EspaciosHydra = _setval;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
