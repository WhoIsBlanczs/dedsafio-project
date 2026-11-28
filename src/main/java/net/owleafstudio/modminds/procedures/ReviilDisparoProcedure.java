package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.ShootReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilDisparoProcedure {
   private static final int DRAW_TICKS = 40;
   private static final int VOLLEY_TICKS = 65;
   private static final int SHOOT_TICKS = 110;
   private static final int RECOVER_TICKS = 145;

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         float projectileDamage = 1.0F;
         float projectileSpeed = 10.0F;
         if (entity instanceof ReviilEntity reviil) {
            reviil.setAnimation("emote.roulette.hability");
            if (world instanceof ServerLevel _fx) {
               ReviilFx.volleyCharge(_fx, reviil, 40);
            }

            if (entity instanceof LivingEntity _livingEntity && _livingEntity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
               _livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            }

            ModmindsMod.queueServerWork(110, () -> {
               if (reviil.isAlive()) {
                  reviil.setAnimation("emote.roulette.shoot.hability");
               }
            });
            ModmindsMod.queueServerWork(145, () -> {
               if (reviil.isAlive()) {
                  reviil.releasePose();
               }
            });
            if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
               for (int t = 40; t < 105; t++) {
                  ModmindsMod.queueServerWork(t, () -> {
                     if (world instanceof ServerLevel projectileLevel) {
                        Vec3 muzzle = BossFx.mouth(entity, 3.0, 4.0);
                        Projectile arrow = (new Object() {
                           public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                              AbstractArrow entityToSpawn = new ShootReviilEntity(ModmindsModEntities.SHOOT_REVIIL.get(), level);
                              entityToSpawn.setOwner(shooter);
                              entityToSpawn.setBaseDamage(damage);
                              ArrowCompat.setKnockback(entityToSpawn, knockback);
                              entityToSpawn.setSilent(true);
                              return entityToSpawn;
                           }
                        }).getArrow(projectileLevel, entity, projectileDamage, 1);
                        arrow.setPos(muzzle.x, muzzle.y, muzzle.z);
                        if (arrow instanceof AbstractArrow _a) {
                           _a.setNoGravity(true);
                        }

                        Entity target = entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null;
                        if (target != null) {
                           double dx = target.getX() - muzzle.x;
                           double dy = target.getY() + target.getBbHeight() * 0.5 - muzzle.y;
                           double dz = target.getZ() - muzzle.z;
                           arrow.shoot(dx, dy, dz, projectileSpeed, 0.0F);
                           projectileLevel.addFreshEntity(arrow);
                           ReviilFx.volleyMuzzle(projectileLevel, entity, muzzle);
                        }
                     }
                  });
               }
            }

            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
         }
      }
   }
}
