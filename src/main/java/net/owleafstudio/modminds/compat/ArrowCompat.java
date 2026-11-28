package net.owleafstudio.modminds.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.Vec3;

public final class ArrowCompat {
   private static final String KEY = "modminds:arrow_knockback";

   private ArrowCompat() {
   }

   public static void setKnockback(Entity entity, int value) {
      if (entity != null && value > 0) {
         CompatAccess.persistentData(entity).putInt("modminds:arrow_knockback", value);
      }
   }

   public static int getKnockback(Entity entity) {
      return entity == null ? 0 : CompatAccess.persistentData(entity).getIntOr("modminds:arrow_knockback", 0);
   }

   public static void apply(Entity arrow, Entity target) {
      if (arrow instanceof AbstractArrow abstractArrow && target != null) {
         int knockback = getKnockback(abstractArrow);
         if (knockback > 0) {
            Vec3 motion = abstractArrow.getDeltaMovement();
            double length = motion.length();
            if (!(length < 1.0E-4)) {
               double scale = knockback * 0.2;
               target.push(motion.x / length * scale, motion.y / length * scale * 0.5 + 0.1, motion.z / length * scale);
               target.needsSync = true;
            }
         }
      }
   }
}
