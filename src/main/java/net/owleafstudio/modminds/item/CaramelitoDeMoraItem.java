package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.owleafstudio.modminds.procedures.ICaramelitoDeFresaAlJugadorDejarDeUsarloProcedure;

public class CaramelitoDeMoraItem extends Item {
   public CaramelitoDeMoraItem() {
      super(
         new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build())
      );
   }

   public boolean releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
      ICaramelitoDeFresaAlJugadorDejarDeUsarloProcedure.execute(entity);
      return false;
   }
}
