package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.owleafstudio.modminds.block.LimboPortalBlock;
import net.owleafstudio.modminds.compat.CompatAccess;

public class LimboItem extends Item {
   public LimboItem() {
      super(new Item.Properties().rarity(Rarity.COMMON).durability(64));
   }

   public InteractionResult useOn(UseOnContext context) {
      Player entity = context.getPlayer();
      BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
      ItemStack itemstack = context.getItemInHand();
      Level world = context.getLevel();
      if (!entity.mayUseItemAt(pos, context.getClickedFace(), itemstack)) {
         return InteractionResult.FAIL;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         boolean success = false;
         if (world.isEmptyBlock(pos)) {
            LimboPortalBlock.portalSpawn(world, pos);
            itemstack.hurtAndBreak(1, entity, CompatAccess.slot(context.getHand()));
            success = true;
         }

         return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
      }
   }
}
