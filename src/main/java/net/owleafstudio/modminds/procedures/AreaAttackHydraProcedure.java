package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class AreaAttackHydraProcedure {
   private static final int STEPS = 19;
   private static final double FIRST_DISTANCE = 10.0;
   private static final double STEP_DISTANCE = 5.0;
   private static final int STEP_TICKS = 7;
   private static final float BLAST_RADIUS = 5.5F;
   private static final float PEAK_DAMAGE = 16.0F;
   private static final float EDGE_DAMAGE = 4.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof HydraEntity hydra) {
         hydra.setAnimation("area_attack");
         if (world instanceof ServerLevel level) {
            BossPresentation.windup(level, entity, BossStyle.HYDRA);
            ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            double dirX = CompatAccess.persistentData(entity).getDoubleOr("AreaAttackHydraX", 0.0);
            double dirZ = CompatAccess.persistentData(entity).getDoubleOr("AreaAttackHydraZ", 0.0);
            BossFx.warningBeam(level, entity, BossFx.ground(entity), Math.atan2(dirZ, dirX), 100.0, 11.0, 133);
            BossPresentation.announce(level, entity, BossStyle.HYDRA);

            for (int step = 0; step < 19; step++) {
               int index = step;
               double distance = 10.0 + step * 5.0;
               ModmindsMod.queueServerWork(step * 7, () -> detonate(level, hydra, dirX, dirZ, distance, index));
            }
         }
      }
   }

   private static void detonate(ServerLevel world, LivingEntity entity, double dirX, double dirZ, double distance, int index) {
      if (!entity.isRemoved()) {
         Vec3 at = BossFx.groundAt(world, dirX * distance + entity.getX(), dirZ * distance + entity.getZ());
         if (index == 0) {
            BossFx.smash(world, at, 10.0, 1.2, BossStyle.HYDRA.flare);
         }

         BossPresentation.sweep(world, entity, at, BossStyle.HYDRA);
         BossFx.impactBurst(world, at, 5.5, BossStyle.HYDRA.flare);
         BossFx.blastDamage(world, world.damageSources().mobAttack(entity), at, 5.5, 16.0F, 4.0F, 1.0, 0.45);
      }
   }
}
