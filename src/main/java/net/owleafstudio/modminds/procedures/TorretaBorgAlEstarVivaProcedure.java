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
import net.owleafstudio.modminds.entity.NaveEnemigaCortaProyectilEntity;
import net.owleafstudio.modminds.entity.TorretaBorgEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class TorretaBorgAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null
         && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null
         && (
            !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
               || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
         )) {
         CompatAccess.persistentData(entity).putDouble("TorretaBorg", CompatAccess.persistentData(entity).getDoubleOr("TorretaBorg", 0.0) + 1.0);
         entity.lookAt(
            EntityAnchorArgument.Anchor.EYES,
            new Vec3(
               (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
               (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
               (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
            )
         );
         if (CompatAccess.persistentData(entity).getDoubleOr("TorretaBorg", 0.0) >= 2.6) {
            if (entity instanceof TorretaBorgEntity) {
               ((TorretaBorgEntity)entity).setAnimation("shoot");
            }

            if (world instanceof ServerLevel projectileLevel) {
               Projectile _entityToSpawn = (new Object() {
                  public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                     AbstractArrow entityToSpawn = new NaveEnemigaCortaProyectilEntity(ModmindsModEntities.NAVE_ENEMIGA_CORTA_PROYECTIL.get(), level);
                     entityToSpawn.setOwner(shooter);
                     entityToSpawn.setBaseDamage(damage);
                     ArrowCompat.setKnockback(entityToSpawn, knockback);
                     entityToSpawn.setSilent(true);
                     return entityToSpawn;
                  }
               }).getArrow(projectileLevel, entity, 5.0F, 0);
               _entityToSpawn.setPos(
                  entity.getLookAngle().x * 2.0 + entity.getX(), y + 1.0, entity.getLookAngle().z * 2.0 + entity.getZ()
               );
               _entityToSpawn.shoot(
                  (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getX() - x,
                  (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getY() + 0.6 - y,
                  (entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).getZ() - z,
                  3.0F,
                  0.0F
               );
               projectileLevel.addFreshEntity(_entityToSpawn);
            }

            CompatAccess.persistentData(entity).putDouble("TorretaTiki", 0.0);
         }
      }
   }
}
