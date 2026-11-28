package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class ArepaDeCarneItem extends Item {
   public ArepaDeCarneItem() {
      super(
         new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(9).saturationModifier(0.3F).build())
      );
   }
}
