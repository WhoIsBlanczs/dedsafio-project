package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class IntrusoAlEstarVivaProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            YawRadians = Math.toRadians(entity.getYRot());
            PitchRadians = Math.toRadians(entity.getXRot());
            Speed = 0.15;
            Wpressd = 1.0;
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(YawRadians) * -1.0 * Math.cos(PitchRadians) * Speed * Wpressd,
                  Math.sin(PitchRadians) * -1.0 * Speed * Wpressd,
                  Math.cos(YawRadians) * Math.cos(PitchRadians) * Speed * Wpressd
               )
            );
         }
      }
   }
}
