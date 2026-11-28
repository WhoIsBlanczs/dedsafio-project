package com.ghostsafio.mitomod.client;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class NaveCommands {
   public static void register() {
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, registryAccess) -> {
               dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommands.literal("nave_giro")
                     .then(ClientCommands.argument("signo", FloatArgumentType.floatArg(-1.0F, 1.0F)).executes(ctx -> {
                        NaveRenderer.tiltSign = FloatArgumentType.getFloat(ctx, "signo") >= 0.0F ? 1.0F : -1.0F;
                        ((FabricClientCommandSource)ctx.getSource()).sendFeedback(Component.literal("nave_giro = " + NaveRenderer.tiltSign));
                        return 1;
                     }))
               );
               dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommands.literal("nave_vuelta")
                     .then(ClientCommands.argument("grados", FloatArgumentType.floatArg(-360.0F, 360.0F)).executes(ctx -> {
                        NaveRenderer.rollOffset = FloatArgumentType.getFloat(ctx, "grados");
                        ((FabricClientCommandSource)ctx.getSource()).sendFeedback(Component.literal("nave_vuelta = " + NaveRenderer.rollOffset));
                        return 1;
                     }))
               );
               dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommands.literal("nave_yaw")
                     .then(ClientCommands.argument("grados", FloatArgumentType.floatArg(-360.0F, 360.0F)).executes(ctx -> {
                        NaveRenderer.yawOffset = FloatArgumentType.getFloat(ctx, "grados");
                        ((FabricClientCommandSource)ctx.getSource()).sendFeedback(Component.literal("nave_yaw = " + NaveRenderer.yawOffset));
                        return 1;
                     }))
               );
               dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommands.literal("nave_probar")
                     .then(ClientCommands.argument("grados", FloatArgumentType.floatArg(-1.0F, 90.0F)).executes(ctx -> {
                        NaveRenderer.previewTilt = FloatArgumentType.getFloat(ctx, "grados");
                        ((FabricClientCommandSource)ctx.getSource())
                           .sendFeedback(Component.literal("nave_probar = " + NaveRenderer.previewTilt + "  (-1 = apagado)"));
                        return 1;
                     }))
               );
            }
         );
   }
}
