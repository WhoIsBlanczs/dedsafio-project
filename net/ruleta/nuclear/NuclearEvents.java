package net.ruleta.nuclear;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;

public class NuclearEvents {
   @SubscribeEvent
   public static void onExplosionStart(ExplosionEvent.Start event) {
      if (!event.getLevel().isClientSide()) {
         if (event.getExplosion().getDirectSourceEntity() instanceof NuclearCreeperEntity creeper && event.getLevel() instanceof ServerLevel level) {
            event.setCanceled(true);
            NuclearExplosion.detonate(level, creeper);
         }
      }
   }

   public static void onServerTick(MinecraftServer servidor) {
      NuclearExplosion.tick();
   }

   public static void onServerStopped(MinecraftServer servidor) {
      NuclearExplosion.clear();
   }
}
