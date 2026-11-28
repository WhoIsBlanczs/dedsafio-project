package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public final class LinternaDynamicLight {
   private static final BlockState LIGHT_STATE = (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
   private static ClientLevel lightWorld;
   private static BlockPos lightPos;
   private static BlockState previousState;

   private LinternaDynamicLight() {
   }

   public static void tick(Minecraft client) {
      if (client.level != null && client.player != null) {
         if (lightWorld != null && lightWorld != client.level) {
            removeLight();
         }

         Player player = client.player;
         boolean lit = isLit(player.getMainHandItem()) || isLit(player.getOffhandItem());
         if (!lit) {
            removeLight();
         } else {
            BlockPos wanted = player.blockPosition();
            if (lightPos != null && lightPos.equals(wanted) && lightWorld == client.level) {
               if (lightWorld.getBlockState(lightPos).equals(LIGHT_STATE)) {
                  return;
               }

               clearTracking();
            } else {
               removeLight();
            }

            BlockPos position = findAirPosition(client.level, wanted);
            if (position != null) {
               BlockState original = client.level.getBlockState(position);
               lightWorld = client.level;
               lightPos = position;
               previousState = original;
               client.level.setBlock(lightPos, LIGHT_STATE, 3);
            }
         }
      } else {
         removeLight();
      }
   }

   private static boolean isLit(ItemStack stack) {
      return stack.getItem() instanceof LinternaItem item && item.isLit();
   }

   private static BlockPos findAirPosition(ClientLevel world, BlockPos base) {
      BlockPos[] candidates = new BlockPos[]{base, base.above()};

      for (BlockPos candidate : candidates) {
         if (world.getBlockState(candidate).isAir()) {
            return candidate.immutable();
         }
      }

      return null;
   }

   private static void removeLight() {
      if (lightWorld != null && lightPos != null && previousState != null && lightWorld.getBlockState(lightPos).equals(LIGHT_STATE)) {
         lightWorld.setBlock(lightPos, previousState, 3);
      }

      clearTracking();
   }

   private static void clearTracking() {
      lightWorld = null;
      lightPos = null;
      previousState = null;
   }
}
