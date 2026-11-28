package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

public class AlaDeMurcielagoWardenItem extends Item {
   public AlaDeMurcielagoWardenItem() {
      super(new Item.Properties().stacksTo(64).rarity(Rarity.RARE));
   }

   public int getUseDuration(ItemStack itemstack) {
      return 32;
   }

   public InteractionResult use(Level world, Player entity, InteractionHand hand) {
      InteractionResult ar = super.use(world, entity, hand);
      entity.startUsingItem(hand);
      return ar;
   }
}
