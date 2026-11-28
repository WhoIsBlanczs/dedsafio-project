package net.ruleta;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class Vanilla {
   private Vanilla() {
   }

   public static boolean es(Block bloque) {
      Identifier clave = BuiltInRegistries.BLOCK.getKey(bloque);
      String ns = clave.getNamespace();
      return "minecraft".equals(ns) || "ruletafanproject".equals(ns);
   }
}
