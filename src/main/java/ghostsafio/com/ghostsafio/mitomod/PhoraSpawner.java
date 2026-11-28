package com.ghostsafio.mitomod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class PhoraSpawner {
   private static final int INTERVAL = 800;
   private static final int SKIFFS_PER_WAVE = 10;
   private static final int MAX_SKIFFS = 60;
   private static final int PODS_PER_WAVE = 2;
   private static final int MAX_PODS = 16;
   private static final double MIN_DIST = 8.0;
   private static final double MAX_DIST = 30.0;

   public static void tick(MinecraftServer server) {
      if (server.getTickCount() % 800 == 0) {
         for (ServerLevel level : server.getAllLevels()) {
            if (level.getDifficulty() != Difficulty.PEACEFUL) {
               List<ServerPlayer> inside = new ArrayList<>();

               for (ServerPlayer player : level.players()) {
                  if (!player.isSpectator() && CuboPhora.isInside(level, player.blockPosition())) {
                     inside.add(player);
                  }
               }

               if (!inside.isEmpty()) {
                  wave(level, inside, ModMobs.SKIFF_PHORA, SkiffPhoraEntity.class, 10, 60);
                  wave(level, inside, ModMobs.POD_PHORA, PodPhoraEntity.class, 2, 16);
               }
            }
         }
      }
   }

   private static <T extends Mob> void wave(ServerLevel level, List<ServerPlayer> inside, EntityType<T> type, Class<T> cls, int perWave, int maxAlive) {
      int alive = 0;

      for (ServerPlayer player : inside) {
         alive = Math.max(alive, level.getEntitiesOfClass(cls, player.getBoundingBox().inflate(96.0)).size());
      }

      int toSpawn = Math.min(perWave, Math.max(0, maxAlive - alive));
      RandomSource random = level.random;

      for (int i = 0; i < toSpawn; i++) {
         ServerPlayer anchor = inside.get(random.nextInt(inside.size()));
         trySpawn(level, anchor, random, type);
      }
   }

   private static <T extends Mob> void trySpawn(ServerLevel level, ServerPlayer anchor, RandomSource random, EntityType<T> type) {
      for (int attempt = 0; attempt < 16; attempt++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double dist = 8.0 + random.nextDouble() * 22.0;
         int x = (int)Math.floor(anchor.getX() + Math.cos(angle) * dist);
         int z = (int)Math.floor(anchor.getZ() + Math.sin(angle) * dist);
         if (level.hasChunkAt(new BlockPos(x, anchor.getBlockY(), z))) {
            int baseY = anchor.getBlockY();

            for (int dy = 6; dy >= -16; dy--) {
               int y = baseY + dy;
               BlockPos pos = new BlockPos(x, y, z);
               if (level.getBlockState(pos).isAir()) {
                  BlockPos below = pos.below();
                  if (!level.getBlockState(below).isAir()
                     && level.getBlockState(below).getFluidState().isEmpty()
                     && level.noCollision(type.getSpawnAABB(x + 0.5, y, z + 0.5))) {
                     int spawnY = y;

                     for (int up = 1; up <= 3 && level.getBlockState(pos.above(up)).isAir(); up++) {
                        spawnY = y + up;
                     }

                     if (spawnY != y) {
                        BlockPos spawnPos = new BlockPos(x, spawnY, z);
                        if (level.noCollision(type.getSpawnAABB(x + 0.5, spawnY, z + 0.5)) && CuboPhora.isInside(level, spawnPos)) {
                           T mob = (T)type.spawn(level, spawnPos, MobSpawnType.EVENT);
                           if (mob != null) {
                              mob.setYRot(random.nextFloat() * 360.0F);
                              mob.yBodyRot = mob.getYRot();
                              mob.yHeadRot = mob.getYRot();
                           }

                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
