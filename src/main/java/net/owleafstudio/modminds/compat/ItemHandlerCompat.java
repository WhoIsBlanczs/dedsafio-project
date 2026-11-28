package net.owleafstudio.modminds.compat;

import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public final class ItemHandlerCompat {
   private ItemHandlerCompat() {
   }

   public static LazyOptional<IItemHandler> itemHandler(Object holder) {
      if (holder instanceof ItemStack stack) {
         DataComponentType<ItemContainerContents> component = ItemBundleComponent.componentFor(stack);
         if (component != null) {
            return LazyOptional.of(() -> new ItemHandlerCompat.BundleHandler(stack, component));
         }
      }

      return LazyOptional.empty();
   }

   private static final class BundleHandler extends ItemStackHandler {
      private final ItemStack owner;
      private final DataComponentType<ItemContainerContents> component;

      BundleHandler(ItemStack owner, DataComponentType<ItemContainerContents> component) {
         super(27);
         this.owner = owner;
         this.component = component;
         ItemContainerContents contents = (ItemContainerContents)owner.get(component);
         if (contents != null) {
            NonNullList<ItemStack> target = this.stacks;
            contents.copyInto(target);
         }
      }

      @Override
      public void setSize(int size) {
      }

      @Override
      public int getSlotLimit(int slot) {
         return 64;
      }

      @Override
      public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
         return stack.getItem() != this.owner.getItem();
      }

      @Override
      public void setChanged() {
         this.owner.set(this.component, ItemContainerContents.fromItems(this.stacks));
      }
   }
}
