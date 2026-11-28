package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class GlebanoidesMod implements ModInitializer {
   public void onInitialize() {
      Item nave = (Item)Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", "nave")), new Item(new Item.Properties()));
      NexoMobs.register();
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
         for (String id : new String[]{"gusano_glebanoide", "nautilus_glebanoide", "cerearana"}) {
            entries.accept((ItemLike)BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("nexo", id + "_spawn_egg")));
         }
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(nave));
   }
}
