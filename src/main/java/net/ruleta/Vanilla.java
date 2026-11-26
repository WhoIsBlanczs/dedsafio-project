package net.ruleta;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class Vanilla {
   private Vanilla() {
   }

   public static boolean es(Block bloque) {
      ResourceLocation clave = BuiltInRegistries.BLOCK.getKey(bloque);
      String ns = clave.getNamespace();
      return "minecraft".equals(ns) || "ruleta".equals(ns);
   }
}
