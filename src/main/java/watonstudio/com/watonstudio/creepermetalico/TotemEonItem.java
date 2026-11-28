package com.watonstudio.creepermetalico;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemLore;

public class TotemEonItem extends Item {
   public TotemEonItem(Properties settings) {
      super(settings.stacksTo(1));
   }

   private static MutableComponent texto(String contenido, int color) {
      return Component.literal(contenido)
         .setStyle(Style.EMPTY.withColor(color).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false));
   }

   public static ItemStack crearTotem(Item item) {
      ItemStack stack = new ItemStack(item);
      stack.set(DataComponents.ITEM_NAME, texto("Tótem de Eón", 16755200));
      List<Component> lore = List.of(
         texto("Popea aunque esté en tu", 11184810),
         texto("inventario.", 11184810),
         texto("", 16777215),
         texto("Otorga ", 11184810).append(texto("Resistencia (Nivel Máx. 1s)", 16733695)),
         texto("al popear, evitando el InstaKill", 11184810),
         texto("si dos ataques se ejecutan", 11184810),
         texto("demasiado rápido.", 11184810)
      );
      stack.set(DataComponents.LORE, new ItemLore(lore, lore));
      return stack;
   }

   public ItemStack getDefaultInstance() {
      return crearTotem(this);
   }
}
