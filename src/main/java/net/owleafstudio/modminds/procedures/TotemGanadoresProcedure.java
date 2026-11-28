package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.items.ItemHandlerHelper;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class TotemGanadoresProcedure {
   public static void execute(Entity sourceentity) {
      if (sourceentity != null) {
         double Premios = 0.0;
         Premios = Mth.nextInt(RandomSource.create(), 1, 5);
         if (Premios == 1.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.BUHO_TOTEM.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 1x Búho Tótem!"), false);
            }
         } else if (Premios == 2.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.NUTRITOTEM.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 1x Nutritótem!"), false);
            }
         } else if (Premios == 3.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.TOTEM_ESPACIAL.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 1x Tótem Espacial!"), false);
            }
         } else if (Premios == 4.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.TOTEM_EON_MARIPOSA.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 1x Tótem Eon Mariposa!"), false);
            }
         } else if (Premios == 5.0) {
            if (sourceentity instanceof Player _player) {
               ItemStack _setstack = new ItemStack((ItemLike)ModmindsModItems.TOTEMEON.get()).copy();
               _setstack.setCount(1);
               ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
            }

            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("§a¡Ganaste 1x Tótem Eon!"), false);
            }
         }
      }
   }
}
