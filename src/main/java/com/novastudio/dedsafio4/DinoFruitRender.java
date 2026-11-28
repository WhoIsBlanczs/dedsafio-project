package com.novastudio.dedsafio4;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public final class DinoFruitRender {
   public static final String[] FRUITS = new String[]{"fruta_solaria", "arandano_nocturino", "huevo_de_eburia", "baya_uvina"};
   public static BlockEntityType<DinoFruitBlockEntity> TYPE;

   private DinoFruitRender() {
   }

   public static void registerType() {
      Block[] var0 = new Block[FRUITS.length];

      for (int var1 = 0; var1 < FRUITS.length; var1++) {
         var0[var1] = (Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", FRUITS[var1] + "_tree_fruit"));
      }

      TYPE = (BlockEntityType<DinoFruitBlockEntity>)Registry.register(
         BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath("nexo", "dino_fruit"), FabricBlockEntityTypeBuilder.create(DinoFruitBlockEntity::new, var0).build()
      );
   }
}
