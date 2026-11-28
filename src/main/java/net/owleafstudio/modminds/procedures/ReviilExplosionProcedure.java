package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilExplosionProcedure {
   private static final int RINGS = 9;
   private static final int CHARGES = 20;
   private static final double FIRST_RADIUS = 10.0;
   private static final double RADIUS_STEP = 5.0;
   private static final double REACH = 6.0;
   private static final float PEAK_DAMAGE = 22.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         double reach = reviil.phaseBlastRadius(6.0);
         Vec3 centro = reviil.isGiant() ? BossFx.aim(level, reviil, 200.0) : null;
         if (centro != null) {
            BossFx.miraA(reviil, centro);
         }

         reviil.setAnimation("emote.nova");
         ReviilFx.explosionCharge(level, reviil);
         pinta(level, reviil, centro, 10.0, reach, 0);
         int blastTick = ReviilFx.warningTicks(0);

         for (int ring = 0; ring < 9; ring++) {
            double radius = 10.0 + ring * 5.0;
            int ringIndex = ring;

            for (int charge = 0; charge < 20; charge++) {
               int chargeIndex = charge;
               int at = blastTick + charge * 2;
               ModmindsMod.queueServerWork(at, () -> detonateCharge(level, reviil, centro, radius, reach, ringIndex, chargeIndex));
            }

            if (ring + 1 < 9) {
               pinta(level, reviil, centro, radius + 5.0, reach, ring + 1);
            }

            blastTick += ReviilFx.warningTicks(ring + 1);
         }

         ModmindsMod.queueServerWork(blastTick + 20, () -> {
            if (reviil.isAlive()) {
               reviil.releasePose();
            }
         });
      }
   }

   private static void pinta(ServerLevel world, ReviilEntity reviil, @Nullable Vec3 centro, double radius, double depth, int ring) {
      if (centro == null) {
         ReviilFx.explosionRingTelegraph(world, reviil, radius, depth, ring);
      } else {
         ReviilFx.explosionRingTelegraphAt(world, centro, radius, depth, ring);
      }
   }

   private static void detonateCharge(ServerLevel world, ReviilEntity reviil, @Nullable Vec3 centro, double radius, double reach, int ring, int charge) {
      if (reviil.isAlive()) {
         double angle = charge * Math.PI * 2.0 / 20.0;
         Vec3 base = centro != null ? centro : BossFx.ground(reviil);
         Vec3 at = BossFx.groundAt(world, base.x + Math.cos(angle) * radius, base.z + Math.sin(angle) * radius);
         if (charge == 0) {
            if (centro == null) {
               ReviilFx.explosionRingBlast(world, reviil, radius, ring);
            } else {
               ReviilFx.explosionRingBlast(world, base, base.add(0.0, 2.4, 0.0), radius, ring);
            }
         }

         world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
         world.sendParticles(ParticleTypes.POOF, at.x, at.y + 0.4, at.z, 6, 0.5, 0.4, 0.5, 0.12);
         world.sendParticles(ModmindsModParticleTypes.SHOCKWAVE_RING.get(), at.x, at.y + 0.3, at.z, 2, 0.4, 0.2, 0.4, 0.0);
         world.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.9F, 0.75F + charge * 0.01F);
         BossFx.blastDamage(
            world, world.damageSources().mobAttack(reviil), at, reach, reviil.phaseDamage(22.0F), reviil.phaseDamage(2.0F), 1.0, 0.55, reviil::sting
         );
      }
   }
}
