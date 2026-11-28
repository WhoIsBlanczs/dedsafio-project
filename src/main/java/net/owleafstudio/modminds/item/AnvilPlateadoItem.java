package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Item;

public class AnvilPlateadoItem extends Item {
   public AnvilPlateadoItem() {
      super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
   }

   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);

      for (ItemStack armorPiece : player.getArmorSlots$()) {
         if (armorPiece.isDamaged()) {
            int currentDamage = armorPiece.getDamageValue();
            int maxDamage = armorPiece.getMaxDamage();
            int repairAmount = (int)(maxDamage * 0.15);
            int newDamage = Math.max(0, currentDamage - repairAmount);
            armorPiece.setDamageValue(newDamage);
         }
      }

      itemStack.shrink(1);
      return InteractionResult.SUCCESS;
   }
}
