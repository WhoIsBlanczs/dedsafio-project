package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.ColosoEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaDePulsoProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class ColosoAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
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

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null
            && (
               !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                  || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
            )) {
            CompatAccess.persistentData(entity)
               .putDouble("ProyectilCooldownColoso", CompatAccess.persistentData(entity).getDoubleOr("ProyectilCooldownColoso", 0.0) + 1.0);
            if ((entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) != null
               && CompatAccess.persistentData(entity).getDoubleOr("ProyectilCooldownColoso", 0.0) >= 80.0) {
               entity.lookAt(
                  EntityAnchorArgument.Anchor.EYES,
                  new Vec3(
                     (entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).getX(),
                     (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getY(),
                     (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getZ()
                  )
               );
               if (entity instanceof ColosoEntity) {
                  ((ColosoEntity)entity).setAnimation("shoot");
               }

               if (CompatAccess.persistentData(entity).getDoubleOr("ProyectilCooldownColoso", 0.0) >= 80.0) {
                  if (world instanceof ServerLevel projectileLevel) {
                     Projectile _entityToSpawn = (new Object() {
                        public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                           AbstractArrow entityToSpawn = new NaveEnemigaDePulsoProyectilEntity(ModmindsModEntities.NAVE_ENEMIGA_DE_PULSO_PROYECTIL.get(), level);
                           entityToSpawn.setOwner(shooter);
                           entityToSpawn.setBaseDamage(damage);
                           ArrowCompat.setKnockback(entityToSpawn, knockback);
                           entityToSpawn.setSilent(true);
                           return entityToSpawn;
                        }
                     }).getArrow(projectileLevel, entity, 18.0F, 0);
                     _entityToSpawn.setPos(x, y + 1.0, z + 1.0);
                     _entityToSpawn.shoot(
                        (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX() - x,
                        (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY() - y,
                        (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ() - z,
                        3.0F,
                        0.0F
                     );
                     projectileLevel.addFreshEntity(_entityToSpawn);
                  }

                  CompatAccess.persistentData(entity).putDouble("ProyectilCooldownColoso", 0.0);
               }
            }
         }
      }
   }
}
