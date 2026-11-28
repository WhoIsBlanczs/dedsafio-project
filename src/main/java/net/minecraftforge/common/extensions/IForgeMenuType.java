package net.minecraftforge.common.extensions;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraftforge.network.NetworkHooks;

public interface IForgeMenuType {
   static <T extends AbstractContainerMenu> MenuType<T> create(IForgeMenuType.IContainerFactory<T> factory) {
      return new ExtendedScreenHandlerType(
         (windowId, inventory, data) -> factory.create(windowId, inventory, NetworkHooks.asBuffer(data)), ByteBufCodecs.BYTE_ARRAY
      );
   }

   @FunctionalInterface
   public interface IContainerFactory<T extends AbstractContainerMenu> {
      T create(int var1, Inventory var2, FriendlyByteBuf var3);
   }
}
