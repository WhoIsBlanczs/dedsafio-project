package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.FantasmaAmarilloEntity;
import net.owleafstudio.modminds.entity.FantasmaNegroEntity;
import net.owleafstudio.modminds.entity.FantasmaRojoEntity;

public class FantasmaAmarilloAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            YawRadians = Math.toRadians(entity.getYRot());
            PitchRadians = Math.toRadians(entity.getXRot());
            Speed = 0.35;
            Wpressd = 1.0;
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(YawRadians) * -1.0 * Math.cos(PitchRadians) * Speed * Wpressd,
                  Math.sin(PitchRadians) * -1.0 * Speed * Wpressd,
                  Math.cos(YawRadians) * Math.cos(PitchRadians) * Speed * Wpressd
               )
            );
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator == (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null) && entityiterator != null) {
                  CompatAccess.persistentData(entity).putDouble("AttackCooldown", CompatAccess.persistentData(entity).getDoubleOr("AttackCooldown", 0.0) + 1.0);
                  if (CompatAccess.persistentData(entity).getDoubleOr("AttackCooldown", 0.0) > 3.0) {
                     if (entity instanceof FantasmaRojoEntity) {
                        ((FantasmaRojoEntity)entity).setAnimation("attack");
                     }

                     if (entity instanceof FantasmaAmarilloEntity) {
                        ((FantasmaAmarilloEntity)entity).setAnimation("attack");
                     }

                     if (entity instanceof FantasmaNegroEntity) {
                        ((FantasmaNegroEntity)entity).setAnimation("attack");
                     }

                     ModmindsMod.queueServerWork(
                        10,
                        () -> {
                           entity.lookAt(
                              EntityAnchorArgument.Anchor.EYES, new Vec3(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ())
                           );
                           entityiterator.hurt(
                              new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.MOB_ATTACK), entity), 22.0F
                           );
                           CompatAccess.persistentData(entity).putDouble("AttackCooldown", 0.0);
                        }
                     );
                  }
               }
            }
         }
      }
   }
}
