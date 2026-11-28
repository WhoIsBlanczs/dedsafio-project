package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.SapoEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class SapoComportamientoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity sapoEntity) {
      if (sapoEntity != null && sapoEntity instanceof SapoEntity) {
         double cooldown = CompatAccess.persistentData(sapoEntity).getDoubleOr("SapoCooldown", 0.0);
         if (cooldown > 0.0) {
            CompatAccess.persistentData(sapoEntity).putDouble("SapoCooldown", cooldown - 1.0);
         } else if (!sapoEntity.getPassengers().isEmpty()) {
            if (sapoEntity instanceof Mob) {
               Vec3 wanderTarget = ((SapoEntity)sapoEntity).getRandomGroundPos();
               ((Mob)sapoEntity).getNavigation().moveTo(wanderTarget.x, wanderTarget.y, wanderTarget.z, 1.1);
            }
         } else {
            Entity tempTarget = null;
            if (sapoEntity instanceof Mob) {
               tempTarget = ((Mob)sapoEntity).getTarget();
            }

            if (tempTarget != null) {
               Entity target = tempTarget;
               if (!(target.getVehicle() instanceof SapoEntity)) {
                  double distSq = sapoEntity.distanceToSqr(target);
                  if (distSq > 256.0) {
                     CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", 0.0);
                  } else if (distSq > 36.0 && distSq <= 256.0) {
                     if (sapoEntity instanceof Mob) {
                        ((Mob)sapoEntity).getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1.1);
                     }

                     CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", 0.0);
                  } else {
                     if (sapoEntity instanceof Mob) {
                        sapoEntity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(target.getX(), target.getY(), target.getZ()));
                     }

                     double contador = CompatAccess.persistentData(sapoEntity).getDoubleOr("SapoGigante_IA", 0.0);
                     if (target.isVehicle()) {
                        CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", 0.0);
                     } else {
                        if (contador < 10.0) {
                           ((SapoEntity)sapoEntity).setAnimation("entityeufonia.frog_big.attack_1");
                        } else if (contador >= 10.0 && contador < 90.0) {
                           ((SapoEntity)sapoEntity).setAnimation("entityeufonia.frog_big.attack_2");
                           if (target instanceof LivingEntity livingTarget
                              && !livingTarget.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()))) {
                              livingTarget.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()), 999999, 1, false, false));
                           }
                        } else if (contador >= 90.0 && contador < 100.0) {
                           ((SapoEntity)sapoEntity).setAnimation("entityeufonia.frog_big.attack_3");
                           if (target instanceof LivingEntity livingTarget) {
                              livingTarget.removeEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()));
                           }
                        } else if (contador >= 100.0) {
                           int[] spaceCount = new int[]{0};
                           ModmindsModVariables.getVariables(target).ifPresent(cap -> spaceCount[0] = (int)cap.SapoContadorDeEspacios);
                           if (spaceCount[0] >= 80) {
                              if (target instanceof LivingEntity) {
                                 ((LivingEntity)target).removeEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()));
                              }

                              CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", 0.0);
                              ModmindsModVariables.getVariables(target).ifPresent(cap -> {
                                 cap.SapoContadorDeEspacios = 0.0;
                                 cap.syncPlayerVariables(target);
                              });
                              CompatAccess.persistentData(sapoEntity).putDouble("SapoCooldown", 120.0);
                              ((SapoEntity)sapoEntity).setAnimation("entityeufonia.frog_big.idle");
                           } else {
                              target.startRiding(sapoEntity);
                              CompatAccess.persistentData(target).putBoolean("MostrarBarraEspaciadora", false);
                           }

                           CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", 0.0);
                           return;
                        }

                        CompatAccess.persistentData(sapoEntity).putDouble("SapoGigante_IA", contador + 1.0);
                     }
                  }
               }
            }
         }
      }
   }
}
