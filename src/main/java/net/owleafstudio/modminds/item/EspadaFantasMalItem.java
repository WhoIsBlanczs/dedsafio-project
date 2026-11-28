package net.owleafstudio.modminds.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class EspadaFantasMalItem extends SwordItem {
   public EspadaFantasMalItem() {
      super(new Tier() {
         public int getUses() {
            return 250;
         }

         public float getSpeed() {
            return 4.0F;
         }

         public float getAttackDamageBonus() {
            return 4.0F;
         }

         public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
         }

         public int getEnchantmentValue() {
            return 2;
         }

         public Ingredient getRepairIngredient() {
            return Ingredient.of(new ItemStack[]{new ItemStack((ItemLike)ModmindsModItems.LINGOTE_DE_HIERRO_REFORZADO.get())});
         }
      }, new Item.Properties());
   }
}
