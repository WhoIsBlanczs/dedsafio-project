package net.ruleta.objetos;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemColoreado extends Item {
   private final ChatFormatting color;

   public ItemColoreado(Properties propiedades, ChatFormatting color) {
      super(propiedades);
      this.color = color;
   }

   public Component getName(ItemStack pila) { return null; }
}
