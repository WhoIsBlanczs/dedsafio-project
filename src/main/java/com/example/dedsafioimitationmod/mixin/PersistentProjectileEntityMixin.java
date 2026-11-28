package com.example.dedsafioimitationmod.mixin;

import com.example.dedsafioimitationmod.item.ModItems;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractArrow.class})
public abstract class PersistentProjectileEntityMixin {
   @Unique
   private final Set<Integer> glebaHits = new HashSet<>();

   @Inject(
      method = {"onHitEntity"},
      at = {@At("TAIL")}
   )
   private void glebanoideBounce(EntityHitResult hit, CallbackInfo ci) {
      AbstractArrow arrow = (AbstractArrow)(Object)this;
      Level world = arrow.level();
      if (!world.isClientSide() && !arrow.isRemoved()) {
         ItemStack weapon = arrow.getWeaponItem();
         if (weapon != null && weapon.is(ModItems.ARCO_GLEBANOIDE)) {
            this.glebaHits.add(hit.getEntity().getId());
            Entity owner = arrow.getOwner();
            Optional<LivingEntity> next = world.getEntitiesOfClass(
                  LivingEntity.class,
                  arrow.getBoundingBox().inflate(8.0),
                  e -> e.isAlive() && e != owner && e instanceof Enemy && !this.glebaHits.contains(e.getId())
               )
               .stream()
               .min(Comparator.comparingDouble(e -> e.distanceToSqr(arrow)));
            if (next.isEmpty()) {
               arrow.discard();
            } else {
               LivingEntity target = next.get();
               Vec3 aim = new Vec3(target.getX(), target.getY(0.5), target.getZ());
               Vec3 dir = aim.subtract(arrow.position()).normalize();
               double speed = Math.max(arrow.getDeltaMovement().length(), 1.5);
               arrow.setDeltaMovement(dir.scale(speed));
               arrow.needsSync = true;
            }
         }
      }
   }
}
