package net.owleafstudio.modminds.procedures;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class JeringaEspacialAlEstarEnElInventarioProcedure {
   public static void execute(Entity entity) {
      if (entity != null
         && (entity instanceof LivingEntity _livEntx ? _livEntx.getOffhandItem() : ItemStack.EMPTY).getDamageValue() >= 101
         && (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.JERINGA_ESPACIAL.get()
         && entity instanceof LivingEntity _entity) {
         ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.JERINGA_ESPACIAL_LLENA.get()).copy();
         _setstack.setCount(1);
         _entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
         if (_entity instanceof Player _player) {
            _player.getInventory().setChanged();
         }
      }
   }
}
