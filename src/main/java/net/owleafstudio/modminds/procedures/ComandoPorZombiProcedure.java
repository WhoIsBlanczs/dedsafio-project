package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ComandoPorZombiProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (!ModmindsModVariables.MapVariables.get(world).ZombificacionPorZombi) {
            ModmindsModVariables.MapVariables.get(world).ZombificacionPorZombi = true;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Zombificacion por zombie activada"), false);
            }
         } else {
            ModmindsModVariables.MapVariables.get(world).ZombificacionPorZombi = false;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Zombificacion por zombie desactivada"), false);
            }
         }
      }
   }
}
