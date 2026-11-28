package net.owleafstudio.modminds.recipes.brewing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.component.DataComponents;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModItems;
import net.owleafstudio.modminds.init.ModmindsModPotions;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class SirenamorphosisRecetaBrewingRecipe implements IBrewingRecipe {
   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new SirenamorphosisRecetaBrewingRecipe()));
   }

   @Override
   public boolean isInput(ItemStack input) {
      Item inputItem = input.getItem();
      return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION)
         && ((PotionContents)input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).is(Potions.WATER);
   }

   @Override
   public boolean isIngredient(ItemStack ingredient) {
      return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)ModmindsModItems.RABANAGUA.get())}).test(ingredient);
   }

   @Override
   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient)
         ? PotionContents.createItemStack(input.getItem(), CompatAccess.potion(ModmindsModPotions.SIRENAMORPHOSI))
         : ItemStack.EMPTY;
   }
}
