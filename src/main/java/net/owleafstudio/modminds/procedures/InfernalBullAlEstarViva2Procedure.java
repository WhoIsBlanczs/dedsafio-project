package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.InfernalBullEntity;

public class InfernalBullAlEstarViva2Procedure {
   public static void execute(LevelAccessor world, double y, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            entity.lookAt(
               EntityAnchorArgument.Anchor.EYES,
               new Vec3(
                  (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getX(),
                  entity.getY() + 1.0,
                  (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
               )
            );
            CompatAccess.persistentData(entity)
               .putDouble("InfernalBullV2Snti", CompatAccess.persistentData(entity).getDoubleOr("InfernalBullV2Snti", 0.0) + 1.0);
         }

         if (CompatAccess.persistentData(entity).getDoubleOr("InfernalBullV2Snti", 0.0) == 100.0) {
            if (entity instanceof InfernalBullEntity) {
               ((InfernalBullEntity)entity).setAnimation("roar");
            }

            if (entity instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
               _livingEntity12.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            }

            ModmindsMod.queueServerWork(49, () -> {
               if (entity instanceof InfernalBullEntity) {
                  ((InfernalBullEntity)entity).setAnimation("run");
               }

               if (entity instanceof LivingEntity _livingEntity14 && _livingEntity14.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                  _livingEntity14.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4);
               }

               if (entity instanceof LivingEntity _livingEntity15 && _livingEntity15.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                  _livingEntity15.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0);
               }

               if (entity instanceof LivingEntity _livingEntity16 && _livingEntity16.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
                  _livingEntity16.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(200.0);
               }
            });
         }

         if ((
               world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 1.0 + entity.getX(),
                           y + 1.1,
                           entity.getLookAngle().z * 1.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
                  || world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 2.0 + entity.getX(),
                           y + 1.1,
                           entity.getLookAngle().z * 2.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
                  || world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 1.0 + entity.getX(),
                           y + 1.2,
                           entity.getLookAngle().z * 1.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
                  || world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 2.0 + entity.getX(),
                           y + 1.2,
                           entity.getLookAngle().z * 2.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
                  || world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 1.0 + entity.getX(),
                           y + 1.5,
                           entity.getLookAngle().z * 1.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
                  || world.getBlockState(
                        BlockPos.containing(
                           entity.getLookAngle().x * 2.0 + entity.getX(),
                           y + 1.5,
                           entity.getLookAngle().z * 2.0 + entity.getZ()
                        )
                     )
                     .canOcclude()
            )
            && ((InfernalBullEntity)entity).animationprocedure.equals("run")) {
            ModmindsMod.queueServerWork(4, () -> {
               if (entity instanceof InfernalBullEntity) {
                  ((InfernalBullEntity)entity).setAnimation("stunt");
               }

               if (entity instanceof LivingEntity _livingEntity50 && _livingEntity50.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                  _livingEntity50.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
               }

               if (entity instanceof LivingEntity _livingEntity51 && _livingEntity51.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                  _livingEntity51.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0);
               }

               ModmindsMod.queueServerWork(200, () -> {
                  if (entity instanceof InfernalBullEntity) {
                     ((InfernalBullEntity)entity).setAnimation("empty");
                  }

                  if (entity instanceof LivingEntity _livingEntity53 && _livingEntity53.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                     _livingEntity53.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
                  }

                  if (entity instanceof LivingEntity _livingEntity54 && _livingEntity54.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                     _livingEntity54.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18.0);
                  }

                  if (entity instanceof LivingEntity _livingEntity55 && _livingEntity55.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
                     _livingEntity55.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(5.0);
                  }

                  CompatAccess.persistentData(entity).putDouble("InfernalBullV2Snti", 1.0);
               });
            });
         }

         if (((InfernalBullEntity)entity).animationprocedure.equals("stunt")) {
            if (entity instanceof LivingEntity _livingEntity60 && _livingEntity60.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
               _livingEntity60.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            }

            if (entity instanceof LivingEntity _livingEntity61 && _livingEntity61.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
               _livingEntity61.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0);
            }

            if (entity instanceof LivingEntity _livingEntity62 && _livingEntity62.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)) {
               _livingEntity62.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(0.0);
            }
         }
      }
   }
}
