package net.ruleta;

import java.util.function.ToDoubleFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public final class Mirada {
   private Mirada() {
   }

   public static <T extends Entity> T alQueMira(CommandSourceStack fuente, EntityType<T> tipo, ToDoubleFunction<T> radio, double alcance) {
      Entity quien = fuente.getEntity();
      if (quien == null) {
         return null;
      } else {
         Vec3 ojos = quien.getEyePosition();
         Vec3 dir = quien.getLookAngle();
         T mejor = null;
         double mejorT = Double.MAX_VALUE;

         for (T e : fuente.getLevel().getEntities(tipo, x -> true)) {
            Vec3 oc = e.position().subtract(ojos);
            double t = oc.dot(dir);
            double r = radio.applyAsDouble(e);
            if (!(t < -r) && !(t > alcance + r) && !(oc.lengthSqr() - t * t > r * r) && t < mejorT) {
               mejorT = t;
               mejor = e;
            }
         }

         return mejor;
      }
   }
}
