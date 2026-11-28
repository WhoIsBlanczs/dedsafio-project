package net.owleafstudio.modminds.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TexturaGorgonaCongeladoAlFinalizarElEfectoProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).GorgonaContadorDeEspacios != 80.0) {
            entity.hurt(
               new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)),
               (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F) * 5.0F
            );
         }

         double _setval = 0.0;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.GorgonaContadorDeEspacios = _setval;
            capability.syncPlayerVariables(entity);
         });
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "attribute @p minecraft:generic.movement_speed base set 0.1"
               );
         }
      }
   }
}
