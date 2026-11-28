package net.owleafstudio.modminds.procedures;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class AlternarAlmaTestProcedure {
   public static void execute(CommandContext<CommandSourceStack> arguments, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.getVariables((new Object() {
            public Entity getEntity() {
               try {
                  return EntityArgument.getEntity(arguments, "alma");
               } catch (CommandSyntaxException var2) {
                  var2.printStackTrace();
                  return null;
               }
            }
         }).getEntity()).orElse(new ModmindsModVariables.PlayerVariables()).TieneAlma) {
            boolean _setval = false;
            ModmindsModVariables.getVariables((new Object() {
               public Entity getEntity() {
                  try {
                     return EntityArgument.getEntity(arguments, "alma");
                  } catch (CommandSyntaxException var2) {
                     var2.printStackTrace();
                     return null;
                  }
               }
            }).getEntity()).ifPresent(capability -> {
               capability.TieneAlma = _setval;
               capability.syncPlayerVariables((new Object() {
                  public Entity getEntity() {
                     try {
                        return EntityArgument.getEntity(arguments, "alma");
                     } catch (CommandSyntaxException var2) {
                        var2.printStackTrace();
                        return null;
                     }
                  }
               }).getEntity());
            });
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Sin Alma"), false);
            }
         } else if (!ModmindsModVariables.getVariables((new Object() {
            public Entity getEntity() {
               try {
                  return EntityArgument.getEntity(arguments, "alma");
               } catch (CommandSyntaxException var2) {
                  var2.printStackTrace();
                  return null;
               }
            }
         }).getEntity()).orElse(new ModmindsModVariables.PlayerVariables()).TieneAlma) {
            boolean _setval = true;
            ModmindsModVariables.getVariables((new Object() {
               public Entity getEntity() {
                  try {
                     return EntityArgument.getEntity(arguments, "alma");
                  } catch (CommandSyntaxException var2) {
                     var2.printStackTrace();
                     return null;
                  }
               }
            }).getEntity()).ifPresent(capability -> {
               capability.TieneAlma = _setval;
               capability.syncPlayerVariables((new Object() {
                  public Entity getEntity() {
                     try {
                        return EntityArgument.getEntity(arguments, "alma");
                     } catch (CommandSyntaxException var2) {
                        var2.printStackTrace();
                        return null;
                     }
                  }
               }).getEntity());
            });
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Con Alma"), false);
            }
         }
      }
   }
}
