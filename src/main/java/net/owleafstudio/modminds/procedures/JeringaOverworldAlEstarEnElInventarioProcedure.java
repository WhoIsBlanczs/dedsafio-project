package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.init.ModmindsModItems;

@Mod.EventBusSubscriber
public class JeringaOverworldAlEstarEnElInventarioProcedure {
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
      if (entity != null
         && (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getDamageValue() >= 101
         && (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.JERINGA_OVERWORLD.get()
         && entity instanceof LivingEntity _entity) {
         ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.JERINGA_OVERWORLD_L_LENA.get()).copy();
         _setstack.setCount(1);
         _entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
         if (_entity instanceof Player _player) {
            _player.getInventory().setChanged();
         }
      }
   }
}
