package net.owleafstudio.modminds.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class ModmindsModEnchantments {
   public static final ResourceKey<Enchantment> ASPECTO_DE_SANGUIJUELA = ResourceKey.create(
      Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("modminds", "aspecto_de_sanguijuela")
   );

   private ModmindsModEnchantments() {
   }
}
