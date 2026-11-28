package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.NereidaEntity;
import net.owleafstudio.modminds.entity.TridenteProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class NereidaAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity.isInWater()) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ModmindsModEntities.NEREIDA_AGUA.get().spawn(_level, BlockPos.containing(x, y, z), EntitySpawnReason.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }
         } else if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            entity.lookAt(
               EntityAnchorArgument.Anchor.EYES,
               new Vec3(
                  (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                  (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY() + 1.0,
                  (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
               )
            );
            CompatAccess.persistentData(entity).putDouble("TridentNereida", CompatAccess.persistentData(entity).getDoubleOr("TridentNereida", 0.0) + 1.0);
            if (CompatAccess.persistentData(entity).getDoubleOr("TridentNereida", 0.0) == 80.0) {
               if (entity instanceof NereidaEntity) {
                  ((NereidaEntity)entity).setAnimation("throwground");
               }

               CompatAccess.persistentData(entity).putDouble("TridentNereida", 0.0);
               ModmindsMod.queueServerWork(
                  10,
                  () -> {
                     if (world instanceof ServerLevel projectileLevel) {
                        Projectile _entityToSpawn = (new Object() {
                           public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                              AbstractArrow entityToSpawn = new TridenteProyectilEntity(ModmindsModEntities.TRIDENTE_PROYECTIL.get(), level);
                              entityToSpawn.setOwner(shooter);
                              entityToSpawn.setBaseDamage(damage);
                              ArrowCompat.setKnockback(entityToSpawn, knockback);
                              entityToSpawn.setSilent(true);
                              return entityToSpawn;
                           }
                        }).getArrow(projectileLevel, entity, 7.0F, 1);
                        _entityToSpawn.setPos(entity.getX(), entity.getY() + 2.8, entity.getZ());
                        _entityToSpawn.shoot(
                           (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getX() - entity.getX(),
                           (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getY()
                              + 1.0
                              - (entity.getY() + 1.65),
                           (entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).getZ() - entity.getZ(),
                           2.2F,
                           0.0F
                        );
                        projectileLevel.addFreshEntity(_entityToSpawn);
                     }
                  }
               );
            }
         }
      }
   }
}
