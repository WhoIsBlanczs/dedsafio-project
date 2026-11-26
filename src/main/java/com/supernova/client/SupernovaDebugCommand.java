package com.supernova.client;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.ruleta.client.OpcionesCliente;

public class SupernovaDebugCommand {
   public static void registrar() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, acceso) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommands.literal(
                                    "supernovadebug"
                                 )
                                 .executes(c -> status(c)))
                              .then(ClientCommands.literal("flip").executes(c -> toggle(c, "flip"))))
                           .then(ClientCommands.literal("nocull").executes(c -> toggle(c, "nocull"))))
                        .then(ClientCommands.literal("nodepth").executes(c -> toggle(c, "nodepth"))))
                     .then(ClientCommands.literal("magenta").executes(c -> toggle(c, "magenta"))))
                  .then(ClientCommands.literal("nochain").executes(c -> toggle(c, "nochain")))
            )
         );
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, acceso) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommands.literal("supernovanubes").executes(c -> nubes(c, -1)))
                  .then(
                     ClientCommands.argument("calidad", IntegerArgumentType.integer(0, 3))
                        .executes(c -> nubes(c, IntegerArgumentType.getInteger(c, "calidad")))
                  )
            )
         );
   }

   private static int nubes(CommandContext<FabricClientCommandSource> c, int q) {
      if (q >= 0) {
         OpcionesCliente.supernovaNubes = q;
         OpcionesCliente.guardar();
      }

      String[] nombres = new String[]{"apagadas", "baja", "media", "alta"};
      ((FabricClientCommandSource)c.getSource())
         .sendFeedback(
            Component.literal(
               "Nubes de la supernova: "
                  + nombres[OpcionesCliente.supernovaNubes]
                  + " ("
                  + OpcionesCliente.supernovaNubes
                  + ").  Usa /supernovanubes 0-3: 0 apagadas, 1 baja, 2 media, 3 alta"
            )
         );
      return 1;
   }

   private static int toggle(CommandContext<FabricClientCommandSource> c, String what) {
      switch (what) {
         case "flip":
            SupernovaRenderHandler.dbgFlip = !SupernovaRenderHandler.dbgFlip;
            break;
         case "nocull":
            SupernovaRenderHandler.dbgNoCull = !SupernovaRenderHandler.dbgNoCull;
            break;
         case "nodepth":
            SupernovaRenderHandler.dbgNoDepth = !SupernovaRenderHandler.dbgNoDepth;
            break;
         case "magenta":
            SupernovaRenderHandler.dbgMagenta = !SupernovaRenderHandler.dbgMagenta;
            break;
         case "nochain":
            SupernovaRenderHandler.dbgNoChain = !SupernovaRenderHandler.dbgNoChain;
      }

      return status(c);
   }

   private static int status(CommandContext<FabricClientCommandSource> c) {
      String msg = "flip="
         + SupernovaRenderHandler.dbgFlip
         + "  nocull="
         + SupernovaRenderHandler.dbgNoCull
         + "  nodepth="
         + SupernovaRenderHandler.dbgNoDepth
         + "  magenta="
         + SupernovaRenderHandler.dbgMagenta
         + "  nochain="
         + SupernovaRenderHandler.dbgNoChain;
      ((FabricClientCommandSource)c.getSource()).sendFeedback(Component.literal(msg));
      return 1;
   }
}
