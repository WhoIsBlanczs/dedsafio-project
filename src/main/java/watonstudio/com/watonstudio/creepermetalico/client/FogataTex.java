package com.watonstudio.creepermetalico.client;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class FogataTex {
   private FogataTex() {
   }

   public static ResourceLocation get(String name) {
      if (name != null && !name.isBlank() && !name.equalsIgnoreCase("steve")) {
         String textura = name.toLowerCase(Locale.ROOT);
         return ResourceLocation.fromNamespaceAndPath("fogata", "textures/fx/" + textura + ".png");
      } else {
         return ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");
      }
   }
}
