package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class RayoProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         double X = 0.0;
         double Y = 0.0;
         double Z = 0.0;
         double Grow = 0.0;
         double TrackZ = 0.0;
         double TrackY = 0.0;
         double TrackX = 0.0;
         if (entity instanceof HydraEntity && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            ModmindsMod.queueServerWork(140, () -> {
               ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
               ModmindsModVariables.MapVariables.get(world).syncData(world);
            });
            CompatAccess.persistentData(entity).putDouble("hydrabeamcooldown", CompatAccess.persistentData(entity).getDoubleOr("hydrabeamcooldown", 0.0) + 1.0);
            TrackX = (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getX() - entity.getX();
            TrackY = (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getY()
               - entity.getY()
               + (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getBbHeight() * 0.45
               - entity.getBbHeight() * 0.45;
            TrackZ = (entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).getZ() - entity.getZ();
            if (entity instanceof HydraEntity) {
               ((HydraEntity)entity).setAnimation("beam_attack");
            }

            ServerLevel var29 = BossFx.server(world);
            if (var29 instanceof ServerLevel) {
               BossPresentation.windup(var29, entity, BossStyle.HYDRA);
               BossPresentation.announce(var29, entity, BossStyle.HYDRA);
            }

            ModmindsMod.queueServerWork(20, () -> {
               if (entity instanceof HydraEntity) {
                  ((HydraEntity)entity).setAnimation("beam_attack2");
               }
            });
            ModmindsMod.queueServerWork(
               22,
               () -> {
                  for (int beamTick = 0; beamTick < 18; beamTick++) {
                     int step = beamTick;
                     ModmindsMod.queueServerWork(
                        beamTick,
                        () -> {
                           if (world instanceof ServerLevel _fxx && entity instanceof Mob _mob && _mob.getTarget() != null) {
                              Entity _beamTarget = _mob.getTarget();
                              BossFx.beam(
                                 _fxx,
                                 BossFx.centre(entity),
                                 new Vec3(
                                    _beamTarget.getX(), _beamTarget.getY() + _beamTarget.getBbHeight() * 0.5, _beamTarget.getZ()
                                 ),
                                 ParticleTypes.NAUTILUS,
                                 14,
                                 0.15 + step * 0.04
                              );
                           }
                        }
                     );
                  }

                  if (world instanceof ServerLevel _fx) {
                     BossFx.sound(_fx, BossFx.centre(entity), SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 2.2F, 0.8);
                  }
               }
            );
            Grow = 1.0;

            for (int index0 = 0; index0 < 20; index0++) {
               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(
                     ParticleTypes.BUBBLE,
                     entity.getX() + TrackX * Grow,
                     entity.getY() + entity.getBbHeight() * 0.45 + TrackY * Grow,
                     entity.getZ() + TrackZ * Grow,
                     10,
                     0.15,
                     0.15,
                     0.15,
                     0.0
                  );
               }

               Grow -= 0.05;
               if (CompatAccess.persistentData(entity).getDoubleOr("hydrabeamcooldown", 0.0) == 20.0) {
                  Vec3 _center;
                  Vec3 var39 = _center = new Vec3(
                     (entity instanceof Mob _mobEntxxxxxxx ? _mobEntxxxxxxx.getTarget() : null).getX(),
                     (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getY(),
                     (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getZ()
                  );

                  for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7.0), e -> true)
                     .stream()
                     .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                     .toList()) {
                     entityiterator.hurt(
                        new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN)), 16.0F
                     );
                     if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
                        CompatAccess.server(entityiterator)
                           .getCommands()
                           .performPrefixedCommand(
                              net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                              "revolver @p"
                           );
                     }
                  }

                  CompatAccess.persistentData(entity).putDouble("hydrabeamcooldown", 0.0);
               }
            }
         }
      }
   }
}
