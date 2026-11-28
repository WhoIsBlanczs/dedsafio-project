package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.SapoEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

@Mod.EventBusSubscriber
public class AtraerJugadorAlSapoProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null
         && entity instanceof LivingEntity livingEntity
         && livingEntity.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()))
         && !(entity.getVehicle() instanceof SapoEntity)) {
         SapoEntity nearestSapo = world.getEntitiesOfClass(SapoEntity.class, AABB.ofSize(new Vec3(x, y, z), 32.0, 32.0, 32.0), e -> true)
            .stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z)))
            .orElse(null);
         if (nearestSapo != null) {
            CompatAccess.persistentData(entity).putBoolean("MostrarBarraEspaciadora", true);
            double targetY = nearestSapo.getY() + 3.0;
            double dx = nearestSapo.getX() - x;
            double dy = targetY - y;
            double dz = nearestSapo.getZ() - z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > 0.0) {
               double factor = 0.1;
               double velX = dx / distance * factor;
               double velY = dy / distance * factor;
               double velZ = dz / distance * factor;
               entity.setDeltaMovement(new Vec3(velX, velY, velZ));
            }
         }
      }
   }
}
