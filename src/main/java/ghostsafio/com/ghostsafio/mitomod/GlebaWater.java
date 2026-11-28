package com.ghostsafio.mitomod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class GlebaWater {
   private static final int INTERVAL = 10;
   public static final float DAMAGE = 2.0F;

   public static void tick(MinecraftServer server) {
      if (server.getTickCount() % 10 == 0) {
         ServerLevel level = server.getLevel(ModNave.RED_PLANET);
         if (level != null) {
            for (ServerPlayer player : level.players()) {
               for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(48.0))) {
                  if (!(e instanceof NautilusEntity)
                     && !(e instanceof CerebroEntity)
                     && e.isAlive()
                     && !(e instanceof ServerPlayer sp && (sp.isCreative() || sp.isSpectator()))
                     && e.isInWater()) {
                     e.invulnerableTime = 0;
                     e.hurt(level.damageSources().magic(), 2.0F);
                  }
               }
            }
         }
      }
   }
}
