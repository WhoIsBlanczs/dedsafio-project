package com.novastudio.dedsafio4;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public final class PrehistoricMod implements ModInitializer {
   public void onInitialize() {
      PrehistoricContent.register();
      PortalWater.register();
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
   }

   private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("nexo")
            .then(
               Commands.literal("dimensions")
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dino").requires(source -> com.novastudio.dedsafio4.compat.Compat.permiso(source, 2)))
                           .then(
                              Commands.literal("enviar")
                                 .then(Commands.argument("jugadores", EntityArgument.players()).executes(ctx -> each(ctx, true)))
                           ))
                        .then(
                           Commands.literal("sacar")
                              .then(Commands.argument("jugadores", EntityArgument.players()).executes(ctx -> each(ctx, false)))
                        )
                  )
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dino").requires(source -> com.novastudio.dedsafio4.compat.Compat.permiso(source, 2)))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("dimension")
                     .then(Commands.literal("entrar").executes(ctx -> PrehistoricContent.teleport((CommandSourceStack)ctx.getSource()))))
                  .then(Commands.literal("salir").executes(ctx -> PrehistoricContent.leave((CommandSourceStack)ctx.getSource())))
            )
      );
   }

   private static int each(CommandContext<CommandSourceStack> ctx, boolean enter) throws CommandSyntaxException {
      int count = 0;

      for (ServerPlayer player : EntityArgument.getPlayers(ctx, "jugadores")) {
         CommandSourceStack source = ((CommandSourceStack)ctx.getSource()).withEntity(player);
         count += enter ? PrehistoricContent.teleport(source) : PrehistoricContent.leave(source);
      }

      return count;
   }
}
