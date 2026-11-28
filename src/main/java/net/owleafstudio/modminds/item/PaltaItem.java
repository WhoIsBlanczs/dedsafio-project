package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.procedures.TeOtorgaDosCorazonesProcedure;

public class PaltaItem extends Item {
   public PaltaItem() {
      super(
         new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.5F).build())
      );
   }

   public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
      ItemStack retval = new ItemStack((ItemLike)ModmindsModItems.SEMILLA_DE_PALTA.get());
      super.finishUsingItem(itemstack, world, entity);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      TeOtorgaDosCorazonesProcedure.execute(entity);
      if (itemstack.isEmpty()) {
         return retval;
      } else {
         if (entity instanceof Player player && !player.getAbilities().instabuild && !player.getInventory().add(retval)) {
            player.drop(retval, false);
         }

         return itemstack;
      }
   }
}
