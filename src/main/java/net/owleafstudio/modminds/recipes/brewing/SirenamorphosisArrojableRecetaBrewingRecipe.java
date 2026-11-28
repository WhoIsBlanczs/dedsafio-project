package net.owleafstudio.modminds.recipes.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class SirenamorphosisArrojableRecetaBrewingRecipe implements IBrewingRecipe {
   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new SirenamorphosisArrojableRecetaBrewingRecipe()));
   }

   @Override
   public boolean isInput(ItemStack input) {
      return Ingredient.of(Items.POTION).test(input);
   }

   @Override
   public boolean isIngredient(ItemStack ingredient) {
      return Ingredient.of(Items.GUNPOWDER).test(ingredient);
   }

   @Override
   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient) ? new ItemStack(Items.SPLASH_POTION) : ItemStack.EMPTY;
   }
}
