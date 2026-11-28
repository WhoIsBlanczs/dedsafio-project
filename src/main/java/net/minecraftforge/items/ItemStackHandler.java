package net.minecraftforge.items;

import javax.annotation.Nonnull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemStackHandler implements IItemHandler {
   protected NonNullList<ItemStack> stacks;

   public ItemStackHandler() {
      this(1);
   }

   public ItemStackHandler(int size) {
      this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public ItemStackHandler(NonNullList<ItemStack> stacks) {
      this.stacks = stacks;
   }

   public void setSize(int size) {
      this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.validateSlotIndex(slot);
      this.stacks.set(slot, stack);
      this.onSlotChanged(slot);
   }

   @Override
   public int getSlots() {
      return this.stacks.size();
   }

   @Nonnull
   @Override
   public ItemStack getStackInSlot(int slot) {
      this.validateSlotIndex(slot);
      return (ItemStack)this.stacks.get(slot);
   }

   @Nonnull
   @Override
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         if (!this.isItemValid(slot, stack)) {
            return stack;
         } else {
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            int limit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
            if (!existing.isEmpty()) {
               if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                  return stack;
               } else {
                  int space = limit - existing.getCount();
                  if (space <= 0) {
                     return stack;
                  } else {
                     int toAdd = Math.min(space, stack.getCount());
                     if (!simulate) {
                        ItemStack copy = existing.copy();
                        copy.grow(toAdd);
                        this.stacks.set(slot, copy);
                        this.onSlotChanged(slot);
                     }

                     return toAdd >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toAdd);
                  }
               }
            } else {
               int toAdd = Math.min(limit, stack.getCount());
               if (!simulate) {
                  this.stacks.set(slot, stack.copyWithCount(toAdd));
                  this.onSlotChanged(slot);
               }

               return toAdd >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toAdd);
            }
         }
      }
   }

   @Nonnull
   @Override
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = (ItemStack)this.stacks.get(slot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int toExtract = Math.min(amount, existing.getCount());
            if (!simulate) {
               if (toExtract == existing.getCount()) {
                  this.stacks.set(slot, ItemStack.EMPTY);
               } else {
                  this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
               }

               this.onSlotChanged(slot);
            }

            return existing.copyWithCount(toExtract);
         }
      }
   }

   @Override
   public int getSlotLimit(int slot) {
      return 64;
   }

   @Override
   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   protected void validateSlotIndex(int slot) {
      if (slot < 0 || slot >= this.stacks.size()) {
         throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
      }
   }

   protected void onSlotChanged(int slot) {
      this.setChanged();
   }

   public void setChanged() {
   }

   public CompoundTag serializeNBT() {
      ListTag items = new ListTag();

      for (int i = 0; i < this.stacks.size(); i++) {
         ItemStack stack = (ItemStack)this.stacks.get(i);
         if (!stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Slot", i);
            tag.putInt("Count", stack.getCount());
            tag.putString("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            items.add(tag);
         }
      }

      CompoundTag nbt = new CompoundTag();
      nbt.put("Items", items);
      nbt.putInt("Size", this.stacks.size());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.setSize(nbt.getIntOr("Size", 0));
      ListTag items = nbt.getListOrEmpty("Items");

      for (int i = 0; i < items.size(); i++) {
         CompoundTag tag = items.getCompound(i);
         int slot = tag.getIntOr("Slot", 0);
         if (slot >= 0 && slot < this.stacks.size()) {
            Item item = (Item)BuiltInRegistries.ITEM.getValue(Identifier.parse(tag.getStringOr("id", "")));
            this.stacks.set(slot, item == null ? ItemStack.EMPTY : new ItemStack(item, tag.getIntOr("Count", 0)));
         }
      }

      this.setChanged();
   }
}
