package com.novastudio.dedsafio4;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public final class PortalWater {
   public static Block BLOCK;

   private PortalWater() {
   }

   public static void register() {
      BLOCK = (Block)Registry.register(BuiltInRegistries.BLOCK, com.novastudio.dedsafio4.compat.Ids.bloque(Identifier.fromNamespaceAndPath("nexo", "agua_prehistorica")), new PortalWaterBlock());
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", "cubo_agua_prehistorica")), new PortalBucketItem());
   }
}
