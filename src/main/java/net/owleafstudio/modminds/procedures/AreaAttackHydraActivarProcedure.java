package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class AreaAttackHydraActivarProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      ModmindsModVariables.MapVariables.get(world).HydraAtaques = 3.0;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof HydraEntity && (entityiterator instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            entityiterator.lookAt(
               EntityAnchorArgument.Anchor.EYES,
               new Vec3(
                  (entityiterator instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                  (entityiterator instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                  (entityiterator instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
               )
            );
            CompatAccess.persistentData(entityiterator).putDouble("AreaAttackHydraX", entityiterator.getLookAngle().x);
            CompatAccess.persistentData(entityiterator).putDouble("AreaAttackHydraY", entityiterator.getY());
            CompatAccess.persistentData(entityiterator).putDouble("AreaAttackHydraZ", entityiterator.getLookAngle().z);
         }
      }
   }
}
