package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ItemsClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      ItemLore.register();
      NewItemsLore.register();
   }
}
