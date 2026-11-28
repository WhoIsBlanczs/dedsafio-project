package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.OrbEntity;

public class QuitarParedesGuiProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof OrbEntity) {
            if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
               CompatAccess.server(entityiterator)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                     "/fill ~25 ~25 ~ ~-25 ~-25 ~ minecraft:air replace minecraft:barrier"
                  );
            }

            if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
               CompatAccess.server(entityiterator)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                     "/fill ~ ~25 ~25 ~ ~-25 ~-25 minecraft:air replace minecraft:barrier"
                  );
            }

            if (entityiterator instanceof OrbEntity) {
               ((OrbEntity)entityiterator).setAnimation("entityeufonia.orb.idle");
            }
         }
      }
   }
}
