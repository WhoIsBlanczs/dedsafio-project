package com.ghostsafio.mitomod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ghostsafio implements ModInitializer {
   public static final String MOD_ID = "ghostsafio";
   public static final Logger LOGGER = LoggerFactory.getLogger("ghostsafio");

   public void onInitialize() {
      ModSounds.initialize();
      ModItems.initialize();
      HeartHelper.registerEvents();
      PayloadTypeRegistry.playS2C().register(SupernovaPayload.TYPE, SupernovaPayload.CODEC);
      SupernovaManager.register();
      PayloadTypeRegistry.playS2C().register(EclipsePayload.TYPE, EclipsePayload.CODEC);
      EclipseManager.register();
      ModBlocks.register();
      ModPhora.register();
      GlebaFeatures.register();
      ModNave.register();
      ModMobs.register();
      ModDino.register();
      LOGGER.info("Hello Fabric world!");
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("ghostsafio", path);
   }
}
