package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.item.RifleDePulsoBorg2Item;

public class RifleDePulsoBorgRecargarProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         ItemStack armaEnMano = entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY;
         if (armaEnMano.getItem() instanceof RifleDePulsoBorg2Item && entity instanceof Player _player) {
            int balasEnInventario = _player.getInventory().countItem(ModmindsModItems.BALA_DE_DILITIO.get());
            if (balasEnInventario <= 0) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§4No tienes balas suficientes para recargar."), true);
            } else {
               CompatAccess.itemTag(armaEnMano).putString("geckoAnim", "animation.armaborgverde.reload");
               int balasEnArma = armaEnMano.getMaxDamage() - armaEnMano.getDamageValue() - 1;
               int balasMaximas = armaEnMano.getMaxDamage() - 1;
               int balasGastadas = balasMaximas - balasEnArma;
               int balasARecargar = Math.min(balasGastadas, balasEnInventario);
               ItemStack balasAEliminar = new ItemStack((ItemLike)ModmindsModItems.BALA_DE_DILITIO.get());
               _player.getInventory().clearOrCountMatchingItems(p -> p.getItem() == balasAEliminar.getItem(), balasARecargar, _player.inventoryMenu.getCraftSlots());
               int nuevaDurabilidad = armaEnMano.getDamageValue() - balasARecargar;
               armaEnMano.setDamageValue(Math.max(0, nuevaDurabilidad));
               _player.getCooldowns().addCooldown(armaEnMano.getItem(), 50);
            }
         }
      }
   }
}
