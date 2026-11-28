package net.ruleta.tragamonedas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class DeditaItem extends Item {
   private final ChatFormatting color;

   public DeditaItem(Properties propiedades) {
      this(propiedades, ChatFormatting.GOLD);
   }

   public DeditaItem(Properties propiedades, ChatFormatting color) {
      super(propiedades);
      this.color = color;
   }

   public Component getName(ItemStack pila) {
      return Component.translatable(this.getDescriptionId(pila)).withStyle(this.color);
   }
}
