package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.compat.CompatAccess;

public class TexturaGorgonaCongeladoAlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setDeltaMovement(new Vec3(0.0, -0.35, 0.0));
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "attribute @p minecraft:generic.movement_speed base set 0"
               );
         }
      }
   }
}
