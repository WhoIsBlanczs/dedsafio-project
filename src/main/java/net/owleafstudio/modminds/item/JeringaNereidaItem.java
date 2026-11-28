package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.procedures.JeringaNereidaAlEstarEnElInventarioProcedure;
import net.owleafstudio.modminds.procedures.JeringaNereidaAlSerCreadoProcedure;

public class JeringaNereidaItem extends Item {
   public JeringaNereidaItem() {
      super(new Item.Properties().durability(101).rarity(Rarity.COMMON));
   }

   public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
      return 0.0F;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      JeringaNereidaAlSerCreadoProcedure.execute(world, itemstack);
   }

   public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      JeringaNereidaAlEstarEnElInventarioProcedure.execute(entity);
   }
}
