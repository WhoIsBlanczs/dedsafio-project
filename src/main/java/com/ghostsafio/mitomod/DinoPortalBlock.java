package com.ghostsafio.mitomod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class DinoPortalBlock extends Block {
   private final boolean toDino;

   public DinoPortalBlock(Properties properties, boolean toDino) {
      super(properties);
      this.toDino = toDino;
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier efectos$, boolean pasando$) {
      if (!level.isClientSide() && entity instanceof ServerPlayer player) {
         ModDino.travel(player, this.toDino);
      }
   }
}
