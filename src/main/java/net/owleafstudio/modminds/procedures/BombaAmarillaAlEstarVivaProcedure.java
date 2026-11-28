package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class BombaAmarillaAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double sx = 0.0;
         double sy = 0.0;
         double sz = 0.0;
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.SPEED, Integer.MAX_VALUE, 6, true, false));
         }

         if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != null) {
            LivingEntity index2 = entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null;
            if (!(index2 instanceof LivingEntity) || !index2.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PROTECCION_CONTRA_LAS_BOMBAS.get()))) {
               YawRadians = Math.toRadians(entity.getYRot());
               PitchRadians = Math.toRadians(entity.getXRot());
               Speed = 0.4;
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

         if (world.getBlockState(BlockPos.containing(x + entity.getLookAngle().x, entity.getY() + 0.2, z + entity.getLookAngle().z))
               .canOcclude()
            && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 3.0F, Level.ExplosionInteraction.MOB);
            }

            sx = -3.0;

            for (int index0 = 0; index0 < 6; index0++) {
               sy = -3.0;

               for (int index1 = 0; index1 < 6; index1++) {
                  sz = -3.0;

                  for (int index2 = 0; index2 < 6; index2++) {
                     if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getBlock().getStateDefinition().getProperty("waterlogged") instanceof BooleanProperty _getbp19
                        && (Boolean)world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).getValue(_getbp19)) {
                        world.setBlock(BlockPos.containing(x + sx, y + sy, z + sz), Blocks.AIR.defaultBlockState(), 3);
                     }

                     sz++;
                  }

                  sy++;
               }

               sx++;
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         }
      }
   }
}
