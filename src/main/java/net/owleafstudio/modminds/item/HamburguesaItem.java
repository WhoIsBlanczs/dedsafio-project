package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class HamburguesaItem extends Item {
   public HamburguesaItem() {
      super(
         new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON)
            .food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.3F).alwaysEdible().build())
      );
   }
}
