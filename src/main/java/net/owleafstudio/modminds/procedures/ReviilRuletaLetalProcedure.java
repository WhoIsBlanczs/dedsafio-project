package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.ShootReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilRuletaLetalProcedure {
   private static final int WAVES = 5;
   private static final int DARTS_PER_WAVE = 12;
   private static final int LEAD_TICKS = 34;
   private static final int STAGGER_TICKS = 13;
   private static final float SPEED = 6.5F;
   private static final float DAMAGE = 7.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.setAnimation("attack_emote_2");
         BossFx.warningCircle(serverLevel, reviil, 11.0, 34);
         BossFx.alert(serverLevel, BossFx.centre(reviil), 96.0, "⚠ REVIIL: RULETA LETAL");
         BossFx.sound(serverLevel, BossFx.centre(reviil), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.8F, 1.25);
         BossFx.vortex(serverLevel, BossFx.centre(reviil), 7.0, 14, (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 0.05);

         for (int wave = 0; wave < 5; wave++) {
            int at = 34 + wave * 13;
            double phase = wave * Math.PI / 12.0;
            boolean opening = wave == 0;
            ModmindsMod.queueServerWork(at, () -> {
               if (reviil.isAlive()) {
                  fireRing(serverLevel, reviil, phase, opening);
               }
            });
         }

         ModmindsMod.queueServerWork(159, () -> reviil.releasePose());
      }
   }

   private static void fireRing(ServerLevel world, ReviilEntity reviil, double phase, boolean opening) {
      Vec3 muzzle = BossFx.mouth(reviil, 2.5, 2.5);
      BossFx.ring(world, muzzle, 3.0, 12, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 1, 0.3);
      BossFx.sound(world, muzzle, SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 2.0F, 0.6);
      if (opening) {
         BossFx.shake(world, BossFx.centre(reviil), 40.0, 16, 0.5);
      }

      for (int dart = 0; dart < 12; dart++) {
         double angle = phase + dart * Math.PI * 2.0 / 12.0;
         Vec3 direction = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Projectile arrow = dart(world, reviil);
         arrow.setPos(muzzle.x + direction.x * 2.0, muzzle.y, muzzle.z + direction.z * 2.0);
         arrow.shoot(direction.x, 0.0, direction.z, 6.5F, 0.0F);
         world.addFreshEntity(arrow);
      }
   }

   private static Projectile dart(Level world, ReviilEntity shooter) {
      AbstractArrow arrow = new ShootReviilEntity(ModmindsModEntities.SHOOT_REVIIL.get(), world);
      arrow.setOwner(shooter);
      arrow.setBaseDamage(shooter.phaseDamage(7.0F));
      ArrowCompat.setKnockback(arrow, 1);
      arrow.setSilent(true);
      arrow.setNoGravity(true);
      return arrow;
   }
}
