package net.ruleta.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class LivingEntityUseItemEvent {
   private LivingEntityUseItemEvent() {
   }

   public static final class Finish extends LivingEntityEvento {
      private final ItemStack objeto;
      private ItemStack resultado;

      public Finish(LivingEntity entidad, ItemStack objeto, ItemStack resultado) {
         super(entidad);
         this.objeto = objeto;
         this.resultado = resultado;
      }

      public ItemStack getItem() {
         return this.objeto;
      }

      public ItemStack getResultStack() {
         return this.resultado;
      }

      public void setResultStack(ItemStack resultado) {
         this.resultado = resultado;
      }
   }
}
