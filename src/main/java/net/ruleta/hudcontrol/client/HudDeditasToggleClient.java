package net.ruleta.hudcontrol.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class HudDeditasToggleClient implements ClientModInitializer {
   private static boolean visible = true;

   public void onInitializeClient() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, registryAccess) -> dispatcher.register(
               (LiteralArgumentBuilder)ClientCommandManager.literal("ruletahud")
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal("deditas")
                              .then(
                                 ClientCommandManager.literal("ocultar").executes(context -> setVisible((FabricClientCommandSource)context.getSource(), false))
                              ))
                           .then(ClientCommandManager.literal("mostrar").executes(context -> setVisible((FabricClientCommandSource)context.getSource(), true))))
                        .then(
                           ClientCommandManager.literal("alternar").executes(context -> setVisible((FabricClientCommandSource)context.getSource(), !visible))
                        )
                  )
            )
         );
   }

   public static boolean isVisible() {
      return visible;
   }

   private static int setVisible(FabricClientCommandSource source, boolean newValue) {
      visible = newValue;
      source.sendFeedback(Component.literal("HUD de deditas de Ruleta Fan Project " + (visible ? "mostrado." : "ocultado.")));
      return 1;
   }
}
