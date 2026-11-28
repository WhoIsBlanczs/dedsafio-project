package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class OrbitaReviilProcedure {
   private static final int WINDUP_TICKS = 118;
   private static final int IMPACT_TICKS = 12;
   private static final int CIERRE_TICKS = 44;
   private static final double RADIUS = 20.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel var4 = BossFx.server(world);
         if (var4 instanceof ServerLevel) {
            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            reviil.setAnimation("emote.orbita.carga");
            ReviilFx.orbitWindup(var4, reviil, 20.0, 118);
            ModmindsMod.queueServerWork(106, () -> {
               reviil.setAnimation("emote.orbita.baja");
               ModmindsMod.queueServerWork(12, () -> {
                  ReviilFx.orbitBlast(var4, reviil, 20.0);
                  if (reviil.isAlive()) {
                     detonate(var4, reviil);
                  }
               });
               ModmindsMod.queueServerWork(44, () -> {
                  if (reviil.isAlive()) {
                     reviil.releasePose();
                  }
               });
            });
            return;
         }
      }
   }

   private static void detonate(ServerLevel world, ReviilEntity reviil) {
      Vec3 at = BossFx.ground(reviil);

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(at, at).inflate(22.0),
         e -> e != reviil && e.isAlive() && !(e instanceof ReviilEntity) && !reviil.hasPassenger(e)
      )) {
         double dx = victim.getX() - at.x;
         double dz = victim.getZ() - at.z;
         double distance = Math.sqrt(dx * dx + dz * dz);
         if (!(distance > 20.0 + victim.getBbWidth() * 0.5)) {
            float falloff = (float)Math.max(0.25, 1.0 - distance / 25.0);
            victim.hurt(world.damageSources().sonicBoom(reviil), 26.0F * falloff);
            double len = Math.max(distance, 0.35);
            victim.setDeltaMovement(dx / len * 1.15 * falloff, 1.0 * falloff + 0.25, dz / len * 1.15 * falloff);
            victim.needsSync = true;
         }
      }
   }
}
