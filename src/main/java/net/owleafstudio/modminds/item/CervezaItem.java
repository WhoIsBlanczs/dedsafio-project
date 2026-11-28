package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.owleafstudio.modminds.procedures.CervezaElJugadorTerminaUsandoElItemProcedure;

public class CervezaItem extends Item {
   public CervezaItem() {
      super(
         new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.UNCOMMON)
            .food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.4F).alwaysEdible().build())
      );
   }

   public ItemUseAnimation getUseAnimation(ItemStack itemstack) {
      return ItemUseAnimation.DRINK;
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      CervezaElJugadorTerminaUsandoElItemProcedure.execute(entity);
      return retval;
   }
}
