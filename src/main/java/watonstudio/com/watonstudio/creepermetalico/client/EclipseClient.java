package com.watonstudio.creepermetalico.client;

import com.watonstudio.creepermetalico.EclipsePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;

@Environment(EnvType.CLIENT)
public class EclipseClient implements ClientModInitializer {
   public void onInitializeClient() {
      registerAll();
   }

   public static void registerAll() {
      EclipseSkyRenderer.registerShader();
      ClientPlayNetworking.registerGlobalReceiver(EclipsePayload.ID, (payload, context) -> ClientEclipse.apply(payload.on(), payload.age()));
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> ClientEclipse.tick());
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> ClientEclipse.reset());
   }
}
