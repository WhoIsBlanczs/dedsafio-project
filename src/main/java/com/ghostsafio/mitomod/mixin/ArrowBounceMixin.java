package com.ghostsafio.mitomod.mixin;

import com.ghostsafio.mitomod.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class ArrowBounceMixin {
   private static final String BOUNCE_TAG = "ghostsafio_bounce_";
   private static final int MAX_BOUNCES = 3;
   private static final double RANGE = 8.0;

   @Inject(method = "onHitEntity", at = @At("TAIL"))
   private void ghostsafio$bounce(EntityHitResult result, CallbackInfo ci) {
      AbstractArrow arrow = (AbstractArrow)(Object)this;
      Level level = arrow.level();
      if (!level.isClientSide()) {
         ItemStack weapon = arrow.getWeaponItem();
         if (weapon != null && weapon.is(ModItems.ARCO_GLEBANOIDE)) {
            int bounces = 0;

            for (String tag : arrow.entityTags()) {
               if (tag.startsWith("ghostsafio_bounce_")) {
                  bounces = Integer.parseInt(tag.substring("ghostsafio_bounce_".length()));
               }
            }

            if (bounces < 3) {
               Entity hit = result.getEntity();
               AABB box = hit.getBoundingBox().inflate(8.0);
               LivingEntity next = null;
               double best = Double.MAX_VALUE;

               for (LivingEntity candidate : level.getEntitiesOfClass(
                  LivingEntity.class, box, e -> e instanceof Enemy && e.isAlive() && e != hit && e != arrow.getOwner()
               )) {
                  double distance = candidate.distanceToSqr(hit);
                  if (distance < best) {
                     best = distance;
                     next = candidate;
                  }
               }

               if (next != null) {
                  Vec3 dir = new Vec3(next.getX() - hit.getX(), next.getY(0.5) - hit.getY(0.5), next.getZ() - hit.getZ()).normalize();
                  double offset = hit.getBbWidth() / 2.0 + 0.5;
                  Vec3 start = new Vec3(hit.getX(), hit.getY(0.5), hit.getZ()).add(dir.scale(offset));
                  Arrow newArrow = new Arrow(level, start.x, start.y, start.z, new ItemStack(Items.ARROW), weapon);
                  newArrow.setOwner(arrow.getOwner());
                  newArrow.setBaseDamage(com.ghostsafio.mitomod.compat.Compat.danoBase(arrow));
                  newArrow.pickup = Pickup.DISALLOWED;
                  newArrow.addTag("ghostsafio_bounce_" + (bounces + 1));
                  double speed = Math.max(1.5, arrow.getDeltaMovement().length());
                  newArrow.shoot(dir.x, dir.y, dir.z, (float)speed, 0.0F);
                  level.addFreshEntity(newArrow);
               }
            }
         }
      }
   }
}
