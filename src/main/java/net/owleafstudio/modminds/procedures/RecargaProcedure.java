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
import net.owleafstudio.modminds.item.CerbatanaDeGoblinItem;

public class RecargaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         ItemStack armaEnMano = entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY;
         if (armaEnMano.getItem() instanceof CerbatanaDeGoblinItem && entity instanceof Player _player) {
            int dardosEnInventario = _player.getInventory().countItem(ModmindsModItems.DARDOS_TIKIS.get());
            if (dardosEnInventario <= 0) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§4No tienes dardos suficientes para recargar."), true);
            } else {
               CompatAccess.itemTag(armaEnMano).putString("geckoAnim", "animation.cerbatana.reload");
               int dardosEnArma = armaEnMano.getMaxDamage() - armaEnMano.getDamageValue() - 1;
               int dardosMaximos = armaEnMano.getMaxDamage() - 1;
               int dardosGastados = dardosMaximos - dardosEnArma;
               int dardosARecargar = Math.min(dardosGastados, dardosEnInventario);
               ItemStack dardosAEliminar = new ItemStack((ItemLike)ModmindsModItems.DARDOS_TIKIS.get());
               _player.getInventory().clearOrCountMatchingItems(p -> p.getItem() == dardosAEliminar.getItem(), dardosARecargar, _player.inventoryMenu.getCraftSlots());
               int nuevaDurabilidad = armaEnMano.getDamageValue() - dardosARecargar;
               armaEnMano.setDamageValue(Math.max(0, nuevaDurabilidad));
               _player.getCooldowns().addCooldown(armaEnMano.getItem(), 38);
            }
         }
      }
   }
}
