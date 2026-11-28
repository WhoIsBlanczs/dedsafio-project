package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.WildFireEntity;

public class WildFireAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null
            && (
               !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                  || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
            )) {
            CompatAccess.persistentData(entity).putDouble("WildfireShoot", CompatAccess.persistentData(entity).getDoubleOr("WildfireShoot", 0.0) + 1.0);
            entity.lookAt(
               EntityAnchorArgument.Anchor.EYES,
               new Vec3(
                  (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                  (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                  (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
               )
            );
            if (CompatAccess.persistentData(entity).getDoubleOr("WildfireShoot", 0.0) >= 200.0) {
               if ((entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null) != null
                  && (
                     !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                        || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                  )) {
                  if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
                     CompatAccess.server(entity)
                        .getCommands()
                        .performPrefixedCommand(
                           net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                           "/attribute @s minecraft:generic.movement_speed base set 0"
                        );
                  }

                  entity.lookAt(
                     EntityAnchorArgument.Anchor.EYES,
                     new Vec3(
                        (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX(),
                        (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY(),
                        (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ()
                     )
                  );
                  if (world instanceof ServerLevel projectileLevel) {
                     Projectile _entityToSpawn = (new Object() {
                        public Projectile getFireball(Level level, Entity shooter) {
                           AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                           entityToSpawn.setOwner(shooter);
                           return entityToSpawn;
                        }
                     }).getFireball(projectileLevel, entity);
                     _entityToSpawn.setPos(
                        entity.getLookAngle().x * 2.0 + entity.getX(), y + 2.0, entity.getLookAngle().z * 2.0 + entity.getZ()
                     );
                     _entityToSpawn.shoot(
                        (entity instanceof Mob _mobEntxxxxxxxxxx ? _mobEntxxxxxxxxxx.getTarget() : null).getX() - x,
                        (entity instanceof Mob _mobEntxxxxxxxxx ? _mobEntxxxxxxxxx.getTarget() : null).getY() + 0.6 - y,
                        (entity instanceof Mob _mobEntxxxxxxxx ? _mobEntxxxxxxxx.getTarget() : null).getZ() - z,
                        3.0F,
                        0.0F
                     );
                     projectileLevel.addFreshEntity(_entityToSpawn);
                  }

                  if (world instanceof ServerLevel projectileLevel) {
                     Projectile _entityToSpawn = (new Object() {
                        public Projectile getFireball(Level level, Entity shooter) {
                           AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                           entityToSpawn.setOwner(shooter);
                           return entityToSpawn;
                        }
                     }).getFireball(projectileLevel, entity);
                     _entityToSpawn.setPos(
                        entity.getLookAngle().x * 2.0 + entity.getX(), y + 3.0, entity.getLookAngle().z * 2.0 + entity.getZ()
                     );
                     _entityToSpawn.shoot(
                        (entity instanceof Mob _mobEntxxxxxxxxxx ? _mobEntxxxxxxxxxx.getTarget() : null).getX() - x,
                        (entity instanceof Mob _mobEntxxxxxxxxx ? _mobEntxxxxxxxxx.getTarget() : null).getY() + 0.6 - y,
                        (entity instanceof Mob _mobEntxxxxxxxx ? _mobEntxxxxxxxx.getTarget() : null).getZ() - z,
                        3.0F,
                        0.0F
                     );
                     projectileLevel.addFreshEntity(_entityToSpawn);
                  }

                  if (world instanceof ServerLevel projectileLevel) {
                     Projectile _entityToSpawn = (new Object() {
                        public Projectile getFireball(Level level, Entity shooter) {
                           AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                           entityToSpawn.setOwner(shooter);
                           return entityToSpawn;
                        }
                     }).getFireball(projectileLevel, entity);
                     _entityToSpawn.setPos(
                        entity.getLookAngle().x * 2.0 + entity.getX(), y + 1.0, entity.getLookAngle().z * 2.0 + entity.getZ()
                     );
                     _entityToSpawn.shoot(
                        (entity instanceof Mob _mobEntxxxxxxxxxx ? _mobEntxxxxxxxxxx.getTarget() : null).getX() - x,
                        (entity instanceof Mob _mobEntxxxxxxxxx ? _mobEntxxxxxxxxx.getTarget() : null).getY() + 0.6 - y,
                        (entity instanceof Mob _mobEntxxxxxxxx ? _mobEntxxxxxxxx.getTarget() : null).getZ() - z,
                        3.0F,
                        0.0F
                     );
                     projectileLevel.addFreshEntity(_entityToSpawn);
                  }
               }

               ModmindsMod.queueServerWork(
                  20,
                  () -> {
                     if ((entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null) != null
                        && (
                           !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                              || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                        )) {
                        entity.lookAt(
                           EntityAnchorArgument.Anchor.EYES,
                           new Vec3(
                              (entity instanceof Mob _mobEntxxxxxxxxxx ? _mobEntxxxxxxxxxx.getTarget() : null).getX(),
                              (entity instanceof Mob _mobEntxxxxxxxxx ? _mobEntxxxxxxxxx.getTarget() : null).getY(),
                              (entity instanceof Mob _mobEntxxxxxxxx ? _mobEntxxxxxxxx.getTarget() : null).getZ()
                           )
                        );
                        if (world instanceof ServerLevel projectileLevel) {
                           Projectile _entityToSpawn = (new Object() {
                              public Projectile getFireball(Level level, Entity shooter) {
                                 AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                 entityToSpawn.setOwner(shooter);
                                 return entityToSpawn;
                              }
                           }).getFireball(projectileLevel, entity);
                           _entityToSpawn.setPos(
                              entity.getLookAngle().x * 2.0 + entity.getX(),
                              y + 2.0,
                              entity.getLookAngle().z * 2.0 + entity.getZ()
                           );
                           _entityToSpawn.shoot(
                              (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX() - x,
                              (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY() + 0.6 - y,
                              (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ() - z,
                              3.0F,
                              0.0F
                           );
                           projectileLevel.addFreshEntity(_entityToSpawn);
                        }

                        if (world instanceof ServerLevel projectileLevel) {
                           Projectile _entityToSpawn = (new Object() {
                              public Projectile getFireball(Level level, Entity shooter) {
                                 AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                 entityToSpawn.setOwner(shooter);
                                 return entityToSpawn;
                              }
                           }).getFireball(projectileLevel, entity);
                           _entityToSpawn.setPos(
                              entity.getLookAngle().x * 2.0 + entity.getX(),
                              y + 3.0,
                              entity.getLookAngle().z * 2.0 + entity.getZ()
                           );
                           _entityToSpawn.shoot(
                              (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX() - x,
                              (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY() + 0.6 - y,
                              (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ() - z,
                              3.0F,
                              0.0F
                           );
                           projectileLevel.addFreshEntity(_entityToSpawn);
                        }

                        if (world instanceof ServerLevel projectileLevel) {
                           Projectile _entityToSpawn = (new Object() {
                              public Projectile getFireball(Level level, Entity shooter) {
                                 AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                 entityToSpawn.setOwner(shooter);
                                 return entityToSpawn;
                              }
                           }).getFireball(projectileLevel, entity);
                           _entityToSpawn.setPos(
                              entity.getLookAngle().x * 2.0 + entity.getX(),
                              y + 1.0,
                              entity.getLookAngle().z * 2.0 + entity.getZ()
                           );
                           _entityToSpawn.shoot(
                              (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX() - x,
                              (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY() + 0.6 - y,
                              (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ() - z,
                              3.0F,
                              0.0F
                           );
                           projectileLevel.addFreshEntity(_entityToSpawn);
                        }
                     }

                     ModmindsMod.queueServerWork(
                        20,
                        () -> {
                           if ((entity instanceof Mob _mobEntxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxx.getTarget() : null) != null
                              && (
                                 !world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true).isEmpty()
                                    || !world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64.0, 64.0, 64.0), e -> true)
                                       .isEmpty()
                              )) {
                              if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
                                 CompatAccess.server(entity)
                                    .getCommands()
                                    .performPrefixedCommand(
                                       net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                                       "/attribute @s minecraft:generic.movement_speed base set 0.35"
                                    );
                              }

                              entity.lookAt(
                                 EntityAnchorArgument.Anchor.EYES,
                                 new Vec3(
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxx.getTarget() : null).getX(),
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxx.getTarget() : null).getY(),
                                    (entity instanceof Mob _mobEntxxxxxxxxxxx ? _mobEntxxxxxxxxxxx.getTarget() : null).getZ()
                                 )
                              );
                              if (world instanceof ServerLevel projectileLevelxxx) {
                                 Projectile _entityToSpawnxxx = (new Object() {
                                    public Projectile getFireball(Level level, Entity shooter) {
                                       AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                       entityToSpawn.setOwner(shooter);
                                       return entityToSpawn;
                                    }
                                 }).getFireball(projectileLevelxxx, entity);
                                 _entityToSpawnxxx.setPos(
                                    entity.getLookAngle().x * 2.0 + entity.getX(),
                                    y + 2.0,
                                    entity.getLookAngle().z * 2.0 + entity.getZ()
                                 );
                                 _entityToSpawnxxx.shoot(
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxx.getTarget() : null).getX() - x,
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxx.getTarget() : null).getY()
                                       + 0.6
                                       - y,
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxx.getTarget() : null).getZ() - z,
                                    3.0F,
                                    0.0F
                                 );
                                 projectileLevelxxx.addFreshEntity(_entityToSpawnxxx);
                              }

                              if (world instanceof ServerLevel projectileLevelxx) {
                                 Projectile _entityToSpawnxx = (new Object() {
                                    public Projectile getFireball(Level level, Entity shooter) {
                                       AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                       entityToSpawn.setOwner(shooter);
                                       return entityToSpawn;
                                    }
                                 }).getFireball(projectileLevelxx, entity);
                                 _entityToSpawnxx.setPos(
                                    entity.getLookAngle().x * 2.0 + entity.getX(),
                                    y + 3.0,
                                    entity.getLookAngle().z * 2.0 + entity.getZ()
                                 );
                                 _entityToSpawnxx.shoot(
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxxx.getTarget() : null).getX() - x,
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxxx.getTarget() : null).getY()
                                       + 0.6
                                       - y,
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxx.getTarget() : null).getZ() - z,
                                    3.0F,
                                    0.0F
                                 );
                                 projectileLevelxx.addFreshEntity(_entityToSpawnxx);
                              }

                              if (world instanceof ServerLevel projectileLevelx) {
                                 Projectile _entityToSpawnx = (new Object() {
                                    public Projectile getFireball(Level level, Entity shooter) {
                                       AbstractHurtingProjectile entityToSpawn = new LargeFireball(net.minecraft.world.entity.EntityTypes.FIREBALL, level);
                                       entityToSpawn.setOwner(shooter);
                                       return entityToSpawn;
                                    }
                                 }).getFireball(projectileLevelx, entity);
                                 _entityToSpawnx.setPos(
                                    entity.getLookAngle().x * 2.0 + entity.getX(),
                                    y + 1.0,
                                    entity.getLookAngle().z * 2.0 + entity.getZ()
                                 );
                                 _entityToSpawnx.shoot(
                                    (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX() - x,
                                    (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY() + 0.6 - y,
                                    (entity instanceof Mob _mobEntxxxxxxxxxxxxxxx ? _mobEntxxxxxxxxxxxxxxx.getTarget() : null).getZ() - z,
                                    3.0F,
                                    0.0F
                                 );
                                 projectileLevelx.addFreshEntity(_entityToSpawnx);
                              }
                           }
                        }
                     );
                  }
               );
               CompatAccess.persistentData(entity).putDouble("WildfireShoot", 0.0);
            }
         }

         if ((entity instanceof LivingEntity _livEntx ? _livEntx.getHealth() : -1.0F) >= 124.0F
            && (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) <= 150.0F) {
            if (entity instanceof WildFireEntity animatable) {
               animatable.setTexture("wildfirebase");
            }
         } else if ((entity instanceof LivingEntity _livEntxxx ? _livEntxxx.getHealth() : -1.0F) >= 98.0F
            && (entity instanceof LivingEntity _livEntxx ? _livEntxx.getHealth() : -1.0F) <= 124.0F) {
            if (entity instanceof WildFireEntity animatable) {
               animatable.setTexture("wildfire1");
            }
         } else if ((entity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getHealth() : -1.0F) >= 72.0F
            && (entity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getHealth() : -1.0F) <= 89.0F) {
            if (entity instanceof WildFireEntity animatable) {
               animatable.setTexture("wildfire2");
            }
         } else if ((entity instanceof LivingEntity _livEntxxxxxxx ? _livEntxxxxxxx.getHealth() : -1.0F) >= 46.0F
            && (entity instanceof LivingEntity _livEntxxxxxx ? _livEntxxxxxx.getHealth() : -1.0F) <= 72.0F) {
            if (entity instanceof WildFireEntity animatable) {
               animatable.setTexture("wildfire3");
            }
         } else if ((entity instanceof LivingEntity _livEntxxxxxxxxx ? _livEntxxxxxxxxx.getHealth() : -1.0F) >= 20.0F
            && (entity instanceof LivingEntity _livEntxxxxxxxx ? _livEntxxxxxxxx.getHealth() : -1.0F) <= 46.0F
            && entity instanceof WildFireEntity animatable) {
            animatable.setTexture("wildfire4");
         }
      }
   }
}
