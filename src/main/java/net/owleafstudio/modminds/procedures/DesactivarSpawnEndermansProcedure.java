package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class DesactivarSpawnEndermansProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (!ModmindsModVariables.MapVariables.get(world).SpawnEndermans) {
            ModmindsModVariables.MapVariables.get(world).SpawnEndermans = true;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Spawn End Desactivada"), false);
            }
         } else {
            ModmindsModVariables.MapVariables.get(world).SpawnEndermans = false;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Spawn End Activa"), false);
            }
         }
      }
   }
}
