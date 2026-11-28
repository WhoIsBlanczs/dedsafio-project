package net.owleafstudio.modminds.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EspadaTikiItem extends SwordItem {
   private static final Tier TIER = new Tier() {
      public int getUses() {
         return 250;
      }

      public float getSpeed() {
         return 6.0F;
      }

      public float getAttackDamageBonus() {
         return 0.0F;
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
      }

      public int getEnchantmentValue() {
         return 2;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of();
      }
   };

   public EspadaTikiItem() {
      super(TIER, new Item.Properties().attributes(createAttributes(TIER, 3, -3.0F)));
   }
}
