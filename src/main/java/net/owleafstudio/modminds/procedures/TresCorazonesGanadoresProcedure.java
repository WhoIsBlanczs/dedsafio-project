package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.items.ItemHandlerHelper;

public class TresCorazonesGanadoresProcedure {
   public static void execute(Entity sourceentity) {
      if (sourceentity != null) {
         double Premios = 0.0;
         Premios = Mth.nextInt(RandomSource.create(), 1, 1);
         if (Premios == 1.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack(Items.GOLDEN_APPLE).copy();
               _setstack.setCount(7);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 7x Manzana dorada!"), false);
            }
         }
      }
   }
}
