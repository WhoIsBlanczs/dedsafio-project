package com.ghostsafio.mitomod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ghostsafio implements ModInitializer {
   public static final String MOD_ID = "ghostsafio";
   public static final Logger LOGGER = LoggerFactory.getLogger("ghostsafio");

   public void onInitialize() {
      ModSounds.initialize();
      ModItems.initialize();
      HeartHelper.registerEvents();
      PayloadTypeRegistry.clientboundPlay().register(SupernovaPayload.TYPE, SupernovaPayload.CODEC);
      SupernovaManager.register();
      PayloadTypeRegistry.clientboundPlay().register(EclipsePayload.TYPE, EclipsePayload.CODEC);
      EclipseManager.register();
      ModBlocks.register();
      ModPhora.register();
      GlebaFeatures.register();
      ModNave.register();
      ModMobs.register();
      ModDino.register();
      LOGGER.info("Hello Fabric world!");
   }

   public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("ghostsafio", path);
   }
}
