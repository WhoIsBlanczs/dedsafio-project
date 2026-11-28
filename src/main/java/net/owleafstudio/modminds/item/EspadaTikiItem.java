package net.owleafstudio.modminds.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EspadaTikiItem extends Item {
   private static final net.minecraft.world.item.ToolMaterial TIER = new net.minecraft.world.item.ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 250, 6.0F, 0.0F, 2, net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath("modminds", "sin_reparacion")));

   public EspadaTikiItem() {
      super(new Item.Properties().sword(TIER, 3, -3.0F));
   }
}
