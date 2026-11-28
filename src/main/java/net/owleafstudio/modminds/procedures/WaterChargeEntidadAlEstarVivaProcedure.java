package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;

public class WaterChargeEntidadAlEstarVivaProcedure {
   private static final float RADIUS = 10.0F;
   private static final int DURATION_TICKS = 170;
   private static final int AMPLIFIER = 2;

   public static void execute(Entity entity) {
      if (entity != null) {
         Level world = entity.level();
         if (world instanceof ServerLevel serverLevel) {
            BossFx.impactBurst(serverLevel, entity.position(), 3.0, BossStyle.HYDRA.flare);
            AreaEffectCloud pool = new AreaEffectCloud(net.minecraft.world.entity.EntityTypes.AREA_EFFECT_CLOUD, serverLevel);
            pool.setPos(entity.getX(), entity.getY(), entity.getZ());
            pool.setRadius(10.0F);
            pool.setDuration(170);
            pool.addEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 170, 2, false, true));
            pool.setCustomParticle((ParticleOptions)ModmindsModParticleTypes.HYDRA_PARTICULA.get());
            pool.addTag("harming_potion");
            serverLevel.addFreshEntity(pool);
         }

         if (!world.isClientSide()) {
            entity.discard();
         }
      }
   }
}
