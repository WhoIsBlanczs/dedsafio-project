package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.FantasmaAmarilloEntity;
import net.owleafstudio.modminds.entity.FantasmaNegroEntity;
import net.owleafstudio.modminds.entity.FantasmaRojoEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

@Mod.EventBusSubscriber
public class EspadaFantasMalAlEntidadSerGolpeadaConLaHerramientaProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingAttackEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ESPADA_FANTAS_MAL.get()
            && !(entity instanceof FantasmaAmarilloEntity)
            && !(entity instanceof FantasmaNegroEntity)
            && !(entity instanceof FantasmaRojoEntity)) {
            if (event != null && event.isCancelable()) {
               event.setCanceled(true);
            } else if (event != null && event.hasResult()) {
               event.setResult(Event.Result.DENY);
            }
         }

         if ((entity instanceof FantasmaAmarilloEntity || entity instanceof FantasmaNegroEntity || entity instanceof FantasmaRojoEntity)
            && (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
               != ModmindsModItems.ESPADA_FANTAS_MAL.get()) {
            if (event != null && event.isCancelable()) {
               event.setCanceled(true);
            } else if (event != null && event.hasResult()) {
               event.setResult(Event.Result.DENY);
            }
         }
      }
   }
}
