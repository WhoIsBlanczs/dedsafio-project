package com.novastudio.dedsafio4;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PortalWaterBlock extends Block {
   public PortalWaterBlock() {
      super(
         BlockBehaviour.Properties.of()
            .mapColor(MapColor.WATER)
            .noCollision()
            .noOcclusion()
            .replaceable()
            .noLootTable()
            .pushReaction(PushReaction.POPPED)
            .strength(100.0F)
            .lightLevel(state -> 8)
            .sound(SoundType.EMPTY)
            .isViewBlocking((state, level, pos, caja$) -> false)
            .isSuffocating((state, level, pos) -> false)
      );
   }

   protected boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
      return adjacent.is(this);
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier efectos$, boolean pasando$) {
      entity.resetFallDistance();
      if (!level.isClientSide() && entity instanceof ServerPlayer player && !player.isOnPortalCooldown()) {
         player.setPortalCooldown(200);
         CommandSourceStack source = player.createCommandSourceStack();

         try {
            if (player.level().dimension() == PrehistoricContent.WORLD) {
               PrehistoricContent.leave(source);
            } else {
               PrehistoricContent.teleport(source);
            }
         } catch (CommandSyntaxException var8) {
         }

         player.resetFallDistance();
      }
   }
}
