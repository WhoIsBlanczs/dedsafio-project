package com.example.dedsafioimitationmod;

import com.example.dedsafioimitationmod.entity.ModEntities;
import com.example.dedsafioimitationmod.item.ModItems;
import com.example.dedsafioimitationmod.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DedsafioImitationMod implements ModInitializer {
   public static final String MOD_ID = "dedsafioimitationmod";
   public static final Logger LOGGER = LoggerFactory.getLogger("dedsafioimitationmod");

   public void onInitialize() {
      LOGGER.info("Hello Fabric world!");
      ModItems.register();
      ModEntities.register();
      ModSounds.registerSounds();
   }

   public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("dedsafioimitationmod", path);
   }
}
