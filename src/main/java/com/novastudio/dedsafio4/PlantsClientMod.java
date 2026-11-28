package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

@Environment(EnvType.CLIENT)
public final class PlantsClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      for (String id : new String[]{"abedul_dino", "roble_quiu_dino"}) {
         Block block = (Block)BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("nexo", id));
      }

      DinoFruitClient.register();
      new DinoTreeInventoryClient().onInitializeClient();
   }
}
