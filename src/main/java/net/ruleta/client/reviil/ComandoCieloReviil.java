package net.ruleta.client.reviil;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import net.ruleta.client.OpcionesCliente;

public final class ComandoCieloReviil {
   private ComandoCieloReviil() {
   }

   public static void registrar() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, acceso) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal("cieloreviil")
                        .executes(c -> poner(c, !OpcionesCliente.reviilCielo)))
                     .then(ClientCommandManager.literal("on").executes(c -> poner(c, true))))
                  .then(ClientCommandManager.literal("off").executes(c -> poner(c, false)))
            )
         );
   }

   private static int poner(CommandContext<FabricClientCommandSource> c, boolean ver) {
      OpcionesCliente.reviilCielo = ver;
      OpcionesCliente.guardar();
      String msg = ver
         ? "Cielo del Momento Reviil: ACTIVADO (se ve normal)."
         : "Cielo del Momento Reviil: DESACTIVADO en tu cliente (no se dibuja el cielo, la grieta ni la cabeza). Los demás lo ven normal.";
      ((FabricClientCommandSource)c.getSource()).sendFeedback(Component.literal(msg));
      return 1;
   }
}
