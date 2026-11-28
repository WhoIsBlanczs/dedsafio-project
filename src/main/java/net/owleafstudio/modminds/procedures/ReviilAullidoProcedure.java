package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAullidoProcedure {
   private static final int CHARGE_TICKS = 45;
   private static final double RADIUS = 26.0;
   private static final double HASTA = 200.0;
   private static final double PASO = 4.0;
   private static final int TICKS_POR_PASO = 2;
   private static final float PEAK_DAMAGE = 38.0F;
   private static final float EDGE_DAMAGE = 5.0F;
   private static final int BLANCO_TICKS = 40;
   private static final int ZUMBIDO_TICKS = 150;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.setAnimation("emote.aullido");
         ReviilFx.howlCharge(serverLevel, reviil, 26.0, 45);
         ModmindsMod.queueServerWork(45, () -> {
            if (reviil.isAlive()) {
               ReviilFx.howlBlast(serverLevel, reviil, 26.0);
               Vec3 at = BossFx.ground(reviil);
               BossFx.onda(serverLevel, at, 200.0, 4.0, 2, radio -> {
                  BossFx.ring(serverLevel, at.add(0.0, 0.6, 0.0), radio, 28, BossFx.ALERT_DOT, 1, 0.7);
                  if (radio % 12.0 < 4.0) {
                     BossFx.groundCrack(serverLevel, BossFx.groundAt(serverLevel, at.x + radio, at.z), 6.0, 40);
                  }
               }, (jugador, alcance) -> howlAt(serverLevel, reviil, jugador));
               ModmindsMod.queueServerWork(20, () -> reviil.releasePose());
            }
         });
      }
   }

   private static void howlAt(ServerLevel world, ReviilEntity reviil, LivingEntity victim) {
      double dx = victim.getX() - reviil.getX();
      double dz = victim.getZ() - reviil.getZ();
      double distance = Math.max(1.0, Math.sqrt(dx * dx + dz * dz));
      double falloff = Math.max(0.18, 1.0 - distance / 26.0);
      Vec3 motion = victim.getDeltaMovement();
      victim.setDeltaMovement(motion.add(dx / distance * 1.1 * falloff, 0.5 * falloff, dz / distance * 1.1 * falloff));
      victim.needsSync = true;
      if (falloff > 0.35 && victim instanceof ServerPlayer jugador) {
         BossFx.flashea(jugador, falloff, 40, 150);
      } else {
         victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int)(60.0 * falloff) + 40, 0, false, true));
      }

      victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int)(100.0 * falloff) + 60, 1, false, true));
      victim.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(38.0F) * (float)falloff + reviil.phaseDamage(5.0F));
      reviil.sting(victim);
   }
}
