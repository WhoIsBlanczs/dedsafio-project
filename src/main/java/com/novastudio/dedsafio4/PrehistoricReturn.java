package com.novastudio.dedsafio4;

import java.util.Optional;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.MinecraftServer;

public final class PrehistoricReturn {
   private PrehistoricReturn() {
   }

   public static boolean toBedOrSpawn(ServerPlayer player) {
      MinecraftServer server = player.level().getServer();
      BlockPos bed = com.novastudio.dedsafio4.compat.Compat.respawnPos(player);
      if (bed != null && com.novastudio.dedsafio4.compat.Compat.respawnDim(player) == Level.OVERWORLD) {
         ServerLevel overworld = server.overworld();
         overworld.getChunk(bed.getX() >> 4, bed.getZ() >> 4);
         BlockState state = overworld.getBlockState(bed);
         if (state.is(BlockTags.BEDS) && state.getBlock() instanceof BedBlock) {
            Optional<Vec3> stand = BedBlock.findStandUpPosition(
               net.minecraft.world.entity.EntityTypes.PLAYER, overworld, bed, (Direction)state.getValue(BedBlock.FACING), com.novastudio.dedsafio4.compat.Compat.respawnAngulo(player)
            );
            if (stand.isPresent()) {
               Vec3 pos = stand.get();
               com.novastudio.dedsafio4.compat.Compat.teleport(player, overworld, pos.x, pos.y, pos.z, com.novastudio.dedsafio4.compat.Compat.respawnAngulo(player), 0.0F);
               player.resetFallDistance();
               return true;
            }
         }
      }

      ServerLevel overworld = server.overworld();
      BlockPos spawn = com.novastudio.dedsafio4.compat.Compat.spawnMundo(overworld);
      overworld.getChunk(spawn.getX() >> 4, spawn.getZ() >> 4);
      BlockPos top = overworld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn);
      com.novastudio.dedsafio4.compat.Compat.teleport(player, overworld, top.getX() + 0.5, top.getY(), top.getZ() + 0.5, com.novastudio.dedsafio4.compat.Compat.anguloSpawnMundo(overworld), 0.0F);
      player.resetFallDistance();
      return false;
   }
}
