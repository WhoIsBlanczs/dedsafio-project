package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class DinosClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      DinoClient.register();
      MemeTrapClient.register();
   }
}
