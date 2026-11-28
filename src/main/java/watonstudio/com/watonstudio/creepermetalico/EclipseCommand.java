package com.watonstudio.creepermetalico;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;

public final class EclipseCommand {
   private EclipseCommand() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, CommandSelection environment) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("eclipse").requires(source -> source.hasPermission(2)))
               .then(Commands.literal("on").executes(ctx -> {
                  CommandSourceStack source = (CommandSourceStack)ctx.getSource();
                  if (EclipseManager.start(source.getServer())) {
                     source.sendSuccess(() -> Component.literal("Eclipse activado."), true);
                     return 1;
                  } else {
                     source.sendFailure(Component.literal("El eclipse ya esta activo."));
                     return 0;
                  }
               })))
            .then(Commands.literal("off").executes(ctx -> {
               CommandSourceStack source = (CommandSourceStack)ctx.getSource();
               if (EclipseManager.stop(source.getServer())) {
                  source.sendSuccess(() -> Component.literal("Eclipse desactivado."), true);
                  return 1;
               } else {
                  source.sendFailure(Component.literal("El eclipse no esta activo."));
                  return 0;
               }
            }))
      );
   }
}
