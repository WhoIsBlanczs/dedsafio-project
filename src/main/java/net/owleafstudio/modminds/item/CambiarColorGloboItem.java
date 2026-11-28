package net.owleafstudio.modminds.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;

public class CambiarColorGloboItem extends Item {
   public CambiarColorGloboItem() {
      super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
   }

   public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
      return 0.0F;
   }
}
