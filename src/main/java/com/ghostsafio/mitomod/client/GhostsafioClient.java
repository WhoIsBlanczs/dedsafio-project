package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.ModDino;
import com.ghostsafio.mitomod.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class GhostsafioClient implements ClientModInitializer {
   public void onInitializeClient() {
      SupernovaClient.register();
      EclipseClient.register();
      NaveClient.register();
      MobsClient.register();
   }
}
