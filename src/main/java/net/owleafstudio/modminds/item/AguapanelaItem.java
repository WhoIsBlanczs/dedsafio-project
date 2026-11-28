package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class AguapanelaItem extends Item {
   public AguapanelaItem() {
      super(
         new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.COMMON)
            .food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build())
      );
   }

   public ItemUseAnimation getUseAnimation(ItemStack itemstack) {
      return ItemUseAnimation.DRINK;
   }
}
