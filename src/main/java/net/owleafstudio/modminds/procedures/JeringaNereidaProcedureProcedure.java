package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.ItemCompat;
import net.owleafstudio.modminds.entity.NereidaAguaEntity;
import net.owleafstudio.modminds.entity.NereidaEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

@Mod.EventBusSubscriber
public class JeringaNereidaProcedureProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.JERINGA_NEREIDA.get()
         && (sourceentity.isUnderWater() && sourceentity.isUnderWater() || entity instanceof NereidaEntity || entity instanceof NereidaAguaEntity)) {
         ItemStack _ist = sourceentity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY;
         if (ItemCompat.hurt(_ist, -1)) {
            _ist.shrink(1);
            _ist.setDamageValue(0);
         }
      }
   }
}
