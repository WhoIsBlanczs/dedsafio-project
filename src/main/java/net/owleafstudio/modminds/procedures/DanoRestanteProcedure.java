package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.init.ModmindsModItems;

@Mod.EventBusSubscriber
public class DanoRestanteProcedure {
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
               == ModmindsModItems.ARMA_CORTA_ESPACIAL.get()
            && sourceentity instanceof Player _player
            && !_player.level().isClientSide()) {
            net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + " §4❤"), true);
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.RIFLE_DE_PULSO_BORG_2.get()
            && sourceentity instanceof Player _player
            && !_player.level().isClientSide()) {
            net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + " §4❤"), true);
         }

         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.CERBATANA_DE_GOBLIN.get()
            && sourceentity instanceof Player _player
            && !_player.level().isClientSide()) {
            net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) + " §4❤"), true);
         }
      }
   }
}
