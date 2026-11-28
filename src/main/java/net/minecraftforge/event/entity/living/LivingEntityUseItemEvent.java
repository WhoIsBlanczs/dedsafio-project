package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LivingEntityUseItemEvent extends LivingEvent {
   private ItemStack item;

   public LivingEntityUseItemEvent(LivingEntity entity, ItemStack item) {
      super(entity);
      this.item = item;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public ItemStack getItemStack() {
      return this.item;
   }

   public void setItemStack(ItemStack item) {
      this.item = item;
   }

   public static class Finish extends LivingEntityUseItemEvent {
      public Finish(LivingEntity entity, ItemStack item) {
         super(entity, item);
      }
   }
}
