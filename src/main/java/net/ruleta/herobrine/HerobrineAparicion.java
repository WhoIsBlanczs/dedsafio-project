package net.ruleta.herobrine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.ruleta.Ajustes;
import net.ruleta.Ruleta;

public final class HerobrineAparicion {
   private static int tick;

   private HerobrineAparicion() {
   }

   public static void alTick(MinecraftServer servidor) {
      if (++tick % Math.max(20L, Ajustes.ticks("herobrine.aparicion_seg")) == 0L) {
         MinecraftServer server = servidor;
         if (server != null && Ruleta.cambioActivoPublico("herobrine")) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
               if (p.isAlive() && !p.isSpectator() && !p.isCreative()) {
                  ServerLevel nivel = p.level();
                  if (nivel.getDifficulty() != Difficulty.PEACEFUL
                     && p.getRandom().nextInt(100) < Ajustes.entero("herobrine.aparicion_prob")
                     && nivel.getEntitiesOfClass(HerobrineEntity.class, new AABB(p.blockPosition()).inflate(96.0)).isEmpty()) {
                     aparecer(nivel, p);
                  }
               }
            }
         }
      }
   }

   static boolean aparecer(ServerLevel nivel, ServerPlayer p) {
      for (int i = 0; i < 20; i++) {
         double ang = p.getRandom().nextDouble() * Math.PI * 2.0;
         double r = 14.0 + p.getRandom().nextDouble() * 16.0;
         int x = (int)Math.floor(p.getX() + Math.cos(ang) * r);
         int z = (int)Math.floor(p.getZ() + Math.sin(ang) * r);
         BlockPos pos = buscarSuelo(nivel, x, (int)p.getY(), z);
         if (pos != null) {
            HerobrineEntity h = (HerobrineEntity)Herobrine.HEROBRINE.get().create(nivel, net.minecraft.world.entity.EntitySpawnReason.EVENT);
            if (h == null) {
               return false;
            }

            h.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, p.getRandom().nextFloat() * 360.0F, 0.0F);
            if (nivel.noCollision(h)) {
               h.finalizeSpawn(nivel, nivel.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);
               nivel.addFreshEntity(h);
               return true;
            }
         }
      }

      return false;
   }

   private static BlockPos buscarSuelo(ServerLevel nivel, int x, int y, int z) {
      for (int dy = 6; dy >= -8; dy--) {
         BlockPos pos = new BlockPos(x, y + dy, z);
         if (nivel.getBlockState(pos.below()).isSolid() && nivel.getBlockState(pos).isAir() && nivel.getBlockState(pos.above()).isAir()) {
            return pos;
         }
      }

      int sup = nivel.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
      BlockPos pos = new BlockPos(x, sup, z);
      return Math.abs(sup - y) < 24 && nivel.getBlockState(pos.below()).isSolid() ? pos : null;
   }
}
