package com.watonstudio.creepermetalico;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;

public class EclipseMod implements ModInitializer {
   public static final String MOD_ID = "eclipse";

   public void onInitialize() {
      registerAll();
   }

   public static void registerAll() {
      PayloadTypeRegistry.playS2C().register(EclipsePayload.ID, EclipsePayload.CODEC);
      CommandRegistrationCallback.EVENT.register(EclipseCommand::register);
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, server) -> {
         if (EclipseManager.isActive() && ServerPlayNetworking.canSend(handler, EclipsePayload.ID)) {
            sender.sendPacket(EclipseManager.currentPayload(server));
         }
      });
      ServerLifecycleEvents.SERVER_STARTED.register((ServerStarted)server -> EclipseManager.reset());
      ServerLifecycleEvents.SERVER_STOPPING.register(EclipseManager::onServerStopping);
   }
}
