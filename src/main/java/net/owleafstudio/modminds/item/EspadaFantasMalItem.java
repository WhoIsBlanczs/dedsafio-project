package net.owleafstudio.modminds.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class EspadaFantasMalItem extends Item {
   private static final net.minecraft.world.item.ToolMaterial TIER = new net.minecraft.world.item.ToolMaterial(
      BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 4.0F, 4.0F, 2,
      TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "repairs/espada_fantasmal"))
   );

   public EspadaFantasMalItem() {
      super(new Item.Properties().sword(TIER, -TIER.attackDamageBonus(), 0.0F));
   }
}
