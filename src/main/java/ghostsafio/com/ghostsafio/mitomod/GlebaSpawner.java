package com.ghostsafio.mitomod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.AABB;

public class GlebaSpawner {
   private static final int INTERVAL = 50;
   private static final int MAX_NAUTILUS = 8;
   private static final int MAX_CEREBRO = 14;
   private static final int SAFE_RADIUS = 48;

   public static void tick(MinecraftServer server) {
      if (server.getTickCount() % 50 == 0) {
         ServerLevel level = server.getLevel(ModNave.RED_PLANET);
         if (level != null && level.getDifficulty() != Difficulty.PEACEFUL) {
            RandomSource random = level.random;

            for (ServerPlayer player : level.players()) {
               if (!player.isSpectator()) {
                  AABB area = player.getBoundingBox().inflate(72.0);
                  int nautilus = level.getEntitiesOfClass(NautilusEntity.class, area).size();
                  int cerebros = level.getEntitiesOfClass(CerebroEntity.class, area).size();

                  for (int i = 0; i < 2; i++) {
                     if (nautilus + i < 8 && random.nextFloat() < 0.7F) {
                        trySpawn(level, player, ModMobs.NAUTILUS, true);
                     }
                  }

                  for (int i = 0; i < 3; i++) {
                     if (cerebros + i < 14 && random.nextFloat() < 0.8F) {
                        trySpawn(level, player, ModMobs.CEREBRO, false);
                     }
                  }
               }
            }
         }
      }
   }

   private static void trySpawn(ServerLevel level, ServerPlayer player, EntityType<? extends Mob> type, boolean flying) {
      RandomSource random = level.random;

      for (int attempt = 0; attempt < 10; attempt++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double dist = 18.0 + random.nextDouble() * 26.0;
         int x = (int)Math.floor(player.getX() + Math.cos(angle) * dist);
         int z = (int)Math.floor(player.getZ() + Math.sin(angle) * dist);
         if ((long)x * x + (long)z * z >= 2304L && level.hasChunkAt(new BlockPos(x, 64, z))) {
            int startY = Math.min(level.getMaxBuildHeight() - 2, (int)player.getY() + 24);
            int ground = Integer.MIN_VALUE;
            MutableBlockPos pos = new MutableBlockPos();

            for (int y = startY; y > level.getMinBuildHeight() + 1; y--) {
               pos.set(x, y, z);
               if (!level.getBlockState(pos).isAir()) {
                  boolean fluid = !level.getBlockState(pos).getFluidState().isEmpty();
                  pos.set(x, y + 1, z);
                  if (!fluid && level.getBlockState(pos).isAir()) {
                     ground = y + 1;
                  }
                  break;
               }
            }

            if (ground != Integer.MIN_VALUE) {
               int y = flying ? ground + 3 : ground;
               if (level.noCollision(type.getSpawnAABB(x + 0.5, y, z + 0.5))) {
                  type.spawn(level, new BlockPos(x, y, z), MobSpawnType.EVENT);
                  return;
               }
            }
         }
      }
   }
}
