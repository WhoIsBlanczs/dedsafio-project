package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.OjoDeLimboEntity;

public class WaaaaaaaaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
         CompatAccess.persistentData(entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null)
            .putDouble(
               "OjoMatematica",
               CompatAccess.persistentData(entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getDoubleOr("OjoMatematica", 0.0) + 1.0
            );
         if (CompatAccess.persistentData(entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getDoubleOr("OjoMatematica", 0.0) >= 32.0) {
            CompatAccess.persistentData(entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).putDouble("OjoMatematica", 0.0);
         }

         if ((entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null) != null) {
            Vec3 _center = new Vec3(
               (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxxxxxx.getTarget() : null)
                  .level()
                  .clip(
                     new ClipContext(
                        (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxxxxx.getTarget() : null).getEyePosition(1.0F),
                        (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxxxx.getTarget() : null)
                           .getEyePosition(1.0F)
                           .add(
                              (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxxx.getTarget() : null)
                                 .getViewVector(1.0F)
                                 .scale(
                                    CompatAccess.persistentData(
                                          entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxx.getTarget() : null
                                       )
                                       .getDoubleOr("OjoMatematica", 0.0)
                                 )
                           ),
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        entity instanceof Mob _mobEntxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxx.getTarget() : null
                     )
                  )
                  .getBlockPos()
                  .getX(),
               (entity instanceof Mob _mobEntxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxx.getTarget() : null)
                  .level()
                  .clip(
                     new ClipContext(
                        (entity instanceof Mob _mobEntxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxx.getTarget() : null).getEyePosition(1.0F),
                        (entity instanceof Mob _mobEntxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxx.getTarget() : null)
                           .getEyePosition(1.0F)
                           .add(
                              (entity instanceof Mob _mobEntxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxx.getTarget() : null)
                                 .getViewVector(1.0F)
                                 .scale(
                                    CompatAccess.persistentData(entity instanceof Mob _mobEntxxxxxxxxxxx ? _mobEntxxxxxxxxxxx.getTarget() : null)
                                       .getDoubleOr("OjoMatematica", 0.0)
                                 )
                           ),
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        entity instanceof Mob _mobEntxxxxxxxxxx ? _mobEntxxxxxxxxxx.getTarget() : null
                     )
                  )
                  .getBlockPos()
                  .getY(),
               (entity instanceof Mob _mobEntxxxxxxxxx ? _mobEntxxxxxxxxx.getTarget() : null)
                  .level()
                  .clip(
                     new ClipContext(
                        (entity instanceof Mob _mobEntxxxxxxxx ? _mobEntxxxxxxxx.getTarget() : null).getEyePosition(1.0F),
                        (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null)
                           .getEyePosition(1.0F)
                           .add(
                              (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null)
                                 .getViewVector(1.0F)
                                 .scale(
                                    CompatAccess.persistentData(entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null)
                                       .getDoubleOr("OjoMatematica", 0.0)
                                 )
                           ),
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null
                     )
                  )
                  .getBlockPos()
                  .getZ()
            );

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(0.5), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof OjoDeLimboEntity) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _level) {
                     _level.sendParticles(
                        ParticleTypes.SMOKE,
                        entityiterator.getX(),
                        entityiterator.getY(),
                        entityiterator.getZ(),
                        5,
                        1.0,
                        1.0,
                        1.0,
                        1.0
                     );
                  }
               }
            }
         }
      }
   }
}
