package net.owleafstudio.modminds.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.procedures.JeringaDelLimboAlEstarEnElInventarioProcedure;
import net.owleafstudio.modminds.procedures.JeringaDelLimboAlSerCreadoProcedure;

public class JeringaDelLimboItem extends Item {
   public JeringaDelLimboItem() {
      super(new Item.Properties().durability(101).rarity(Rarity.COMMON));
   }

   public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
      return 0.0F;
   }

   public void onCraftedBy(ItemStack itemstack, Player entity) {
      super.onCraftedBy(itemstack, entity);
      JeringaDelLimboAlSerCreadoProcedure.execute(world, itemstack);
   }

   public void inventoryTick(ItemStack itemstack, net.minecraft.server.level.ServerLevel world, Entity entity, net.minecraft.world.entity.EquipmentSlot ranura$) {
      super.inventoryTick(itemstack, world, entity, ranura$);
      JeringaDelLimboAlEstarEnElInventarioProcedure.execute(entity);
   }
}
