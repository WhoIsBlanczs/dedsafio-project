package net.owleafstudio.modminds.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAlHacerleClickDerechoProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (!ModmindsModVariables.MapVariables.get(world).ReviilMontar) {
            if (!sourceentity.level().isClientSide() && CompatAccess.server(sourceentity) != null) {
               CompatAccess.server(sourceentity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, sourceentity.position(), sourceentity.getRotationVector(), sourceentity.level() instanceof ServerLevel ? (ServerLevel)sourceentity.level() : null, 4, sourceentity.getName().getString(), sourceentity.getDisplayName(), sourceentity.level().getServer(), sourceentity),
                     "/tellraw @s [\"\",{\"text\":\"\\u2620\",\"bold\":true,\"obfuscated\":true,\"color\":\"dark_red\"},{\"text\":\" Aun no puedes controlar a \",\"bold\":true,\"color\":\"red\"},{\"text\":\"Reviil:\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"\\n\\n\\n\"},{\"text\":\">> Recuerda que Reviil aun no esta debilitado, intentalo mas tarde.\",\"color\":\"gray\"},{\"text\":\"\\n\\n\\n\"},{\"text\":\"\\u2620 Reviil \",\"color\":\"dark_red\"},{\"text\":\"noto tu presencia y te quito la vida.\",\"color\":\"red\"},{\"text\":\"\\n \"}]"
                  );
            }

            sourceentity.hurt(new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), 80.0F);
            if ((new Object() {
                  public boolean checkGamemode(Entity _ent) {
                     if (_ent instanceof ServerPlayer _serverPlayer) {
                        return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
                     } else {
                        return _ent.level().isClientSide() && _ent instanceof Player _player
                           ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
                              && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SURVIVAL
                           : false;
                     }
                  }
               })
               .checkGamemode(sourceentity)) {
               sourceentity.stopRiding();
            }
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilMontar) {
            if (!sourceentity.level().isClientSide() && CompatAccess.server(sourceentity) != null) {
               CompatAccess.server(sourceentity)
                  .getCommands()
                  .performPrefixedCommand(
                     net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, sourceentity.position(), sourceentity.getRotationVector(), sourceentity.level() instanceof ServerLevel ? (ServerLevel)sourceentity.level() : null, 4, sourceentity.getName().getString(), sourceentity.getDisplayName(), sourceentity.level().getServer(), sourceentity),
                     "/tellraw @s [\"\",{\"text\":\"\\u2620\",\"bold\":true,\"obfuscated\":true,\"color\":\"dark_red\"},{\"text\":\" Estás controlando a \",\"bold\":true,\"color\":\"red\"},{\"text\":\"Reviil:\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"\\n\\n\\n\"},{\"text\":\">> Presiona \",\"color\":\"gray\"},{\"text\":\"Click Izquierdo o Derecho\",\"color\":\"yellow\"},{\"text\":\" sin nada en la mano para realizar\\nel \",\"color\":\"gray\"},{\"text\":\"Ataque Principal.\",\"color\":\"gold\"},{\"text\":\"\\n\\n\\n\"},{\"text\":\"Utiliza a\",\"color\":\"red\"},{\"text\":\" \\u2620 Reviil \",\"color\":\"dark_red\"},{\"text\":\"para destruir los \",\"color\":\"red\"},{\"text\":\"Reactores\",\"color\":\"gold\"},{\"text\":\" antes de que\\nrecupere el control.\",\"color\":\"red\"},{\"text\":\"\\n \"}]"
                  );
            }

            if (entity instanceof ReviilEntity) {
               ((ReviilEntity)entity).setAnimation("empty");
            }
         }
      }
   }
}
