package com.watonstudio.creepermetalico.client;

import com.watonstudio.creepermetalico.net.OpenMenuS2C;
import com.watonstudio.creepermetalico.net.StartFxS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class FogataClient implements ClientModInitializer {
   public void onInitializeClient() {
      ClientPlayNetworking.registerGlobalReceiver(
         OpenMenuS2C.ID, (payload, ctx) -> ctx.client().execute(() -> ctx.client().gui.setScreen(new FogataScreen(payload.names(), payload.pos())))
      );
      ClientPlayNetworking.registerGlobalReceiver(
         StartFxS2C.ID, (payload, ctx) -> ctx.client().execute(() -> FogataFx.add(payload.pos(), payload.rojo(), payload.name()))
      );
      FogataFx.init();
   }
}
