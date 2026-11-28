package com.watonstudio.creepermetalico;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CreeperQteMod implements ModInitializer {
   public void onInitialize() {
      PayloadTypeRegistry.clientboundPlay().register(CreeperQteStatePayload.ID, CreeperQteStatePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CreeperQteVisualPayload.ID, CreeperQteVisualPayload.CODEC);
      PayloadTypeRegistry.serverboundPlay().register(CreeperQteInputPayload.ID, CreeperQteInputPayload.CODEC);
      CreeperQteManager.register();
   }
}
