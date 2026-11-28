package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.owleafstudio.modminds.procedures.PildoraDelExperimento110ElJugadorTerminaUsandoElItemProcedure;

public class PildoraDelExperimento110Item extends Item {
   public PildoraDelExperimento110Item() {
      super(
         new Item.Properties()
            .stacksTo(64)
            .rarity(Rarity.RARE)
            .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build())
      );
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      PildoraDelExperimento110ElJugadorTerminaUsandoElItemProcedure.execute(entity);
      return retval;
   }
}
