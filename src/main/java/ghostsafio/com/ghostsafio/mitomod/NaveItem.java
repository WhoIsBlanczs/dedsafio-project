package com.ghostsafio.mitomod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;

public class NaveItem extends Item {
   public NaveItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
         NaveEntity nave = new NaveEntity(ModNave.NAVE, serverLevel);
         nave.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, context.getRotation(), 0.0F);
         serverLevel.addFreshEntity(nave);
         Player player = context.getPlayer();
         if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
         }

         return InteractionResult.CONSUME;
      }
   }
}
