package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BombaRojaEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class BombaRojaAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double z, Entity entity) {
      if (entity != null) {
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.SPEED, Integer.MAX_VALUE, 6, true, false));
         }

         if (world.getBlockState(BlockPos.containing(x + entity.getLookAngle().x, entity.getY() + 0.2, z + entity.getLookAngle().z))
               .canOcclude()
            && entity instanceof BombaRojaEntity
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 3.0F, Level.ExplosionInteraction.MOB);
            }
         }

         if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != null) {
            LivingEntity var17 = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (!(var17 instanceof LivingEntity) || !var17.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PROTECCION_CONTRA_LAS_BOMBAS.get()))) {
               YawRadians = Math.toRadians(entity.getYRot());
               PitchRadians = Math.toRadians(entity.getXRot());
               Speed = 0.3;
               Wpressd = 1.0;
               entity.setDeltaMovement(
                  new Vec3(
                     Math.sin(YawRadians) * -1.0 * Math.cos(PitchRadians) * Speed * Wpressd,
                     Math.sin(PitchRadians) * -1.0 * Speed * Wpressd,
                     Math.cos(YawRadians) * Math.cos(PitchRadians) * Speed * Wpressd
                  )
               );
            }
         }
      }
   }
}
