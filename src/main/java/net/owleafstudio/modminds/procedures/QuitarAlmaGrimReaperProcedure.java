package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import net.owleafstudio.modminds.entity.GrimReaperEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class QuitarAlmaGrimReaperProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingAttackEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(
            event,
            event.getEntity().level(),
            event.getEntity().getX(),
            event.getEntity().getY(),
            event.getEntity().getZ(),
            event.getEntity(),
            event.getSource().getEntity()
         );
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      execute(null, world, x, y, z, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && (entity instanceof Player || entity instanceof ServerPlayer)
         && sourceentity instanceof GrimReaperEntity
         && ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).TieneAlma) {
         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ModmindsModEntities.ALMA_PERDIDA.get().spawn(_level, BlockPos.containing(x, y, z), EntitySpawnReason.MOB_SUMMONED);
            if (entityToSpawn != null) {
               entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
            }
         }

         boolean _setval = false;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.TieneAlma = _setval;
            capability.syncPlayerVariables(entity);
         });
         ModmindsMod.queueServerWork(
            3,
            () -> {
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (entityiterator instanceof AlmaPerdidaEntity) {
                     entity.setCustomName(Component.literal("Alma perdida de " + entity.getDisplayName().getString()));
                  }
               }
            }
         );
      }
   }
}
