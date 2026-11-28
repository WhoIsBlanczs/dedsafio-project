package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.Taxi23DeCuatroAsientosEntity;
import net.owleafstudio.modminds.entity.Taxi23Entity;

@Mod.EventBusSubscriber
public class CancelarCaidaDanoProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingAttackEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getSource(), event.getEntity());
      }
   }

   public static void execute(DamageSource damagesource, Entity entity) {
      execute(null, damagesource, entity);
   }

   private static void execute(@Nullable Event event, DamageSource damagesource, Entity entity) {
      if (damagesource != null
         && entity != null
         && damagesource.is(DamageTypes.FALL)
         && entity.isPassenger()
         && (entity.getVehicle() instanceof Taxi23Entity || entity.getVehicle() instanceof Taxi23DeCuatroAsientosEntity)) {
         if (event != null && event.isCancelable()) {
            event.setCanceled(true);
         } else if (event != null && event.hasResult()) {
            event.setResult(Event.Result.DENY);
         }
      }
   }
}
