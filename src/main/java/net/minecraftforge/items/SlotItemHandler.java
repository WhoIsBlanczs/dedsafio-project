package net.minecraftforge.items;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
   private static final Container DUMMY = new SimpleContainer(1);
   private final IItemHandler itemHandler;

   public SlotItemHandler(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition) {
      super(DUMMY, slotIndex, xPosition, yPosition);
      this.itemHandler = itemHandler;
   }

   public IItemHandler getItemHandler() {
      return this.itemHandler;
   }

   public boolean mayPlace(ItemStack stack) {
      return this.itemHandler.isItemValid(this.index, stack);
   }

   public int getMaxStackSize() {
      return Math.min(this.itemHandler.getSlotLimit(this.index), 64);
   }

   public int getMaxStackSize(ItemStack stack) {
      return Math.min(Math.max(this.getMaxStackSize(), 1), stack.getMaxStackSize());
   }

   public ItemStack getItem() {
      return this.itemHandler.getStackInSlot(this.index);
   }

   public boolean hasItem() {
      return !this.getItem().isEmpty();
   }

   public void set(ItemStack stack) {
      if (this.itemHandler instanceof ItemStackHandler handler) {
         handler.setStackInSlot(this.index, stack);
      }

      this.setChanged();
   }

   public ItemStack remove(int amount) {
      return this.itemHandler.extractItem(this.index, amount, false);
   }

   public void onTake(Player player, ItemStack stack) {
      this.setChanged();
   }

   public void setChanged() {
      if (this.itemHandler instanceof ItemStackHandler handler) {
         handler.setChanged();
      }
   }

   public boolean mayPickup(Player player) {
      ItemStack stack = this.getItem();
      return !stack.isEmpty() && super.mayPickup(player);
   }
}
