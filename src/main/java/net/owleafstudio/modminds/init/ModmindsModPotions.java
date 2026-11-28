package net.owleafstudio.modminds.init;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class ModmindsModPotions {
   public static final ResourceKey<Potion> SIRENAMORPHOSI = key("sirenamorphosi");
   public static final ResourceKey<Potion> LECHE_POCION = key("leche_pocion");

   private static ResourceKey<Potion> key(String name) {
      return ResourceKey.create(Registries.POTION, Identifier.fromNamespaceAndPath("modminds", name));
   }

   private ModmindsModPotions() {
   }
}
