package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.WaterChargeEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class BolaDeHydraCuandoSeHaceClicConElBotonDerechoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         Level projectileLevel = entity.level();
         if (!projectileLevel.isClientSide()) {
            Projectile _entityToSpawn = (new Object() {
               public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                  AbstractArrow entityToSpawn = new WaterChargeEntity(ModmindsModEntities.WATER_CHARGE.get(), level);
                  entityToSpawn.setOwner(shooter);
                  entityToSpawn.setBaseDamage(damage);
                  ArrowCompat.setKnockback(entityToSpawn, knockback);
                  entityToSpawn.setSilent(true);
                  return entityToSpawn;
               }
            }).getArrow(projectileLevel, entity, 0.0F, 0);
            _entityToSpawn.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
            _entityToSpawn.shoot(entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z, 2.0F, 0.0F);
            projectileLevel.addFreshEntity(_entityToSpawn);
         }
      }
   }
}
