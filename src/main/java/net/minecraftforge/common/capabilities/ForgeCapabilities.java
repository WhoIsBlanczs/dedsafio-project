package net.minecraftforge.common.capabilities;

import net.minecraftforge.items.IItemHandler;

public final class ForgeCapabilities {
   public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>("net.minecraftforge.items.IItemHandler");

   private ForgeCapabilities() {
   }
}
