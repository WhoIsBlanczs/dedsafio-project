package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.client.gui.components.EditBox;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.OrbEntity;

public class SummonOrbsProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, HashMap guistate) {
      if (entity != null && guistate != null) {
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "execute at @e[type=modminds:orb,distance=..5] run summon "
                     + (guistate.containsKey("text:orbsummon") ? ((EditBox)guistate.get("text:orbsummon")).getValue() : "")
                     + " ~ ~-2 ~"
               );
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof OrbEntity && entityiterator instanceof OrbEntity) {
               ((OrbEntity)entityiterator).setAnimation("entityeufonia.orb.spawner_open");
            }
         }
      }
   }
}
