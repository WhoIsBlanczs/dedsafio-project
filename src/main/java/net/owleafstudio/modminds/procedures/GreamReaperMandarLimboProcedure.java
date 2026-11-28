package net.owleafstudio.modminds.procedures;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.GreamReaperPequenaEntity;

@Mod.EventBusSubscriber
public class GreamReaperMandarLimboProcedure {
   @SubscribeEvent
   public static void onEntityAttacked(LivingAttackEvent event) {
      if (event != null && event.getEntity() != null) {
         execute(event, event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(Entity entity, Entity sourceentity) {
      execute(null, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null && sourceentity instanceof GreamReaperPequenaEntity) {
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "execute as @p in modminds:limbo run tp ~ 88 ~"
               );
         }

         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "/tellraw @a [\"\",{\"text\":\"\\u231b\",\"bold\":true},{\"text\":\" "
                     + entity.getDisplayName().getString()
                     + "\",\"bold\":true,\"color\":\"#8C5999\"},{\"text\":\" ah sido enviado al Limbo\",\"color\":\"#B38AC5\"}]"
               );
         }

         if (!sourceentity.level().isClientSide() && CompatAccess.server(sourceentity) != null) {
            CompatAccess.server(sourceentity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, sourceentity.position(), sourceentity.getRotationVector(), sourceentity.level() instanceof ServerLevel ? (ServerLevel)sourceentity.level() : null, 4, sourceentity.getName().getString(), sourceentity.getDisplayName(), sourceentity.level().getServer(), sourceentity),
                  "/playsound modminds:entradoallimbo neutral @a ~ ~ ~ 100 1"
               );
         }
      }
   }
}
