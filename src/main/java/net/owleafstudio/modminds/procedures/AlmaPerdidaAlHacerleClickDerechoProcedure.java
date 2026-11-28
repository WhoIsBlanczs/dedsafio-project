package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class AlmaPerdidaAlHacerleClickDerechoProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         boolean Cooldown = false;
         if (ModmindsModVariables.getVariables(sourceentity).orElse(new ModmindsModVariables.PlayerVariables()).TieneAlma) {
            if (sourceentity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Ya tienes alma"), true);
            }
         } else if (!ModmindsModVariables.getVariables(sourceentity).orElse(new ModmindsModVariables.PlayerVariables()).TieneAlma
            && !CompatAccess.persistentData(entity).getBooleanOr("CooldownAlma", false)) {
            CompatAccess.persistentData(entity).putBoolean("CooldownAlma", true);
            boolean _setval = true;
            ModmindsModVariables.getVariables(sourceentity).ifPresent(capability -> {
               capability.TieneAlma = _setval;
               capability.syncPlayerVariables(sourceentity);
            });
            if (entity instanceof AlmaPerdidaEntity) {
               ((AlmaPerdidaEntity)entity).setAnimation("disapear");
            }

            ModmindsMod.queueServerWork(30, () -> {
               if (!entity.level().isClientSide()) {
                  entity.discard();
               }

               CompatAccess.persistentData(entity).putBoolean("CooldownAlma", false);
            });
         }
      }
   }
}
