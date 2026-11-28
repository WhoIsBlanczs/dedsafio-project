package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ComandoBloqueadorProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.MapVariables.get(world).ComandoBloqueador) {
            ModmindsModVariables.MapVariables.get(world).ComandoBloqueador = false;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Quemadura Desactivada"), false);
            }
         } else {
            ModmindsModVariables.MapVariables.get(world).ComandoBloqueador = true;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Quemadura Activa"), false);
            }
         }
      }
   }
}
