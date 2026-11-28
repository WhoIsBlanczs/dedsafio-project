package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.SapoEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class EngullidoSapoAlEstarActivoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double currentCount = CompatAccess.persistentData(entity).getDoubleOr("MontarJugadorSapo", 0.0);
         CompatAccess.persistentData(entity).putDouble("MontarJugadorSapo", currentCount + 1.0);
         SapoEntity nearestSapo = world.getEntitiesOfClass(SapoEntity.class, AABB.ofSize(new Vec3(x, y, z), 32.0, 32.0, 32.0), e -> true)
            .stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z)))
            .orElse(null);
         if (nearestSapo != null) {
            nearestSapo.setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
            CompatAccess.persistentData(entity).putBoolean("MostrarBarraEspaciadora", true);
            if (CompatAccess.persistentData(entity).getDoubleOr("MontarJugadorSapo", 0.0) >= 160.0
               && !(ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).GorgonaContadorDeEspacios >= 40.0)) {
               entity.startRiding(nearestSapo);
               if (entity instanceof LivingEntity livingEntity) {
                  livingEntity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()));
               }
            }
         }
      }
   }
}
