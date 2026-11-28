package com.watonstudio.creepermetalico;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class CreeperQteMod implements ModInitializer {
   public void onInitialize() {
      PayloadTypeRegistry.playS2C().register(CreeperQteStatePayload.ID, CreeperQteStatePayload.CODEC);
      PayloadTypeRegistry.playS2C().register(CreeperQteVisualPayload.ID, CreeperQteVisualPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(CreeperQteInputPayload.ID, CreeperQteInputPayload.CODEC);
      CreeperQteManager.register();
   }
}
