package net.ruleta;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.ruleta.compat.ExplosionCentro;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;

public final class OndasExplosion {
   private static final double ALCANCE = 96.0;

   private OndasExplosion() {
   }

   @SubscribeEvent
   public static void alDetonar(ExplosionEvent.Detonate evento) {
      if (!evento.getLevel().isClientSide()) {
         Explosion explosion = evento.getExplosion();
         Vec3 c = ExplosionCentro.de(explosion);
         OndaPayload onda = new OndaPayload(c.x, c.y, c.z, radio(explosion.getDirectSourceEntity()));
         if (evento.getLevel() instanceof ServerLevel nivel) {
            Red.enviarCerca(nivel, c.x, c.y, c.z, 96.0, onda);
         }
      }
   }

   private static float radio(Entity causa) {
      if (causa instanceof NuclearCreeperEntity) {
         return 34.0F;
      } else if (causa instanceof Creeper creeper) {
         return creeper.isPowered() ? 22.0F : 15.0F;
      } else {
         return !(causa instanceof PrimedTnt) && !(causa instanceof MinecartTNT) ? 15.0F : 18.0F;
      }
   }
}
