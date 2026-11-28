package net.minecraftforge.common.brewing;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BrewingRecipeRegistry {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final List<IBrewingRecipe> RECIPES = new ArrayList<>();

   public static void addRecipe(IBrewingRecipe recipe) {
      if (recipe != null && RECIPES.add(recipe)) {
         LOGGER.debug("Registered brewing recipe (inert on Fabric): {}", recipe);
      }
   }

   public static List<IBrewingRecipe> getRecipes() {
      return RECIPES;
   }

   public static ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      for (IBrewingRecipe recipe : RECIPES) {
         if (recipe.isInput(input) && recipe.isIngredient(ingredient)) {
            ItemStack out = recipe.getOutput(input, ingredient);
            if (!out.isEmpty()) {
               return out;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   private BrewingRecipeRegistry() {
   }
}
