package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;

public final class DinosMod implements ModInitializer {
   public void onInitialize() {
      DinoContent.register();
      NexoTab.register();
      MemeTrap.register();
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
         for (String id : new String[]{"raptor", "pterodactilo", "t_rex", "sauropodo_morado"}) {
            entries.accept((ItemLike)BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("nexo", id + "_spawn_egg")));
         }
      });
   }
}
