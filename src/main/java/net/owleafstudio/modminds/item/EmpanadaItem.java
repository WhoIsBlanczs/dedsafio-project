package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class EmpanadaItem extends Item {
   public EmpanadaItem() {
      super(
         new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.3F).build())
      );
   }
}
