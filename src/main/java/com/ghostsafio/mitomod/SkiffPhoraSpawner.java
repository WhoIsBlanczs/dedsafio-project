package com.ghostsafio.mitomod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;

public class SkiffPhoraSpawner {
   private static final int INTERVAL = 800;
   private static final int PER_WAVE = 10;
   private static final int MAX_ALIVE = 60;
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
                  int alive = 0;

                  for (ServerPlayer player : inside) {
                     alive = Math.max(alive, level.getEntitiesOfClass(SkiffPhoraEntity.class, player.getBoundingBox().inflate(96.0)).size());
                  }

                  int toSpawn = Math.min(10, Math.max(0, 60 - alive));
                  RandomSource random = level.getRandom();

                  for (int i = 0; i < toSpawn; i++) {
                     ServerPlayer anchor = inside.get(random.nextInt(inside.size()));
                     trySpawn(level, anchor, random);
                  }
               }
            }
         }
      }
   }

   private static void trySpawn(ServerLevel level, ServerPlayer anchor, RandomSource random) {
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
                     && level.noCollision(ModMobs.SKIFF_PHORA.getSpawnAABB(x + 0.5, y, z + 0.5))
                     && CuboPhora.isInside(level, pos)) {
                     SkiffPhoraEntity mob = (SkiffPhoraEntity)ModMobs.SKIFF_PHORA.spawn(level, pos, EntitySpawnReason.EVENT);
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
