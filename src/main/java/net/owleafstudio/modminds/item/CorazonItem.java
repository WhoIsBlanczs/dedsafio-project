package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class CorazonItem extends Item {
   public CorazonItem() {
      super(
         new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON)
            .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build())
      );
   }
}
