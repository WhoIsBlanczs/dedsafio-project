package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.procedures.JeringaNetherAlEstarEnElInventarioProcedure;
import net.owleafstudio.modminds.procedures.JeringaNetherAlSerCreadoProcedure;

public class JeringaNetherItem extends Item {
   public JeringaNetherItem() {
      super(new Item.Properties().durability(101).rarity(Rarity.COMMON));
   }

   public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
      return 0.0F;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      JeringaNetherAlSerCreadoProcedure.execute(world, itemstack);
   }

   public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      JeringaNetherAlEstarEnElInventarioProcedure.execute(entity);
   }
}
