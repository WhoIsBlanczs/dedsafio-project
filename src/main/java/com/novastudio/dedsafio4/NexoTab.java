package com.novastudio.dedsafio4;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public final class NexoTab {
   public static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("nexo", "main"));

   private NexoTab() {
   }

   public static void register() {
      CreativeModeTab tab = FabricCreativeModeTab.builder()
         .title(Component.translatable("itemGroup.nexo.main"))
         .icon(() -> new ItemStack((ItemLike)BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("nexo", "t_rex_spawn_egg"))))
         .displayItems((params, output) -> {
            List<Identifier> eggs = new ArrayList<>();
            List<Identifier> others = new ArrayList<>();

            for (Identifier id : BuiltInRegistries.ITEM.keySet()) {
               if (id.getNamespace().equals("nexo") && !id.getPath().equals("caca_dinosaurio")) {
                  (id.getPath().endsWith("_spawn_egg") ? eggs : others).add(id);
               }
            }

            eggs.sort(null);
            others.sort(null);

            for (Identifier idx : eggs) {
               output.accept((ItemLike)BuiltInRegistries.ITEM.getValue(idx));
            }

            for (Identifier idx : others) {
               output.accept((ItemLike)BuiltInRegistries.ITEM.getValue(idx));
            }
         })
         .build();
      Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KEY, tab);
   }
}
