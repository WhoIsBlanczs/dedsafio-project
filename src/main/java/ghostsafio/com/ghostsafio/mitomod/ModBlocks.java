package com.ghostsafio.mitomod;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ModBlocks {
   private static final List<Item> ITEMS = new ArrayList<>();
   public static final Block BLOQUE_GLEBA = register("bloque_gleba");
   public static final Block CARNE_BLANDA = register("carne_blanda");
   public static final Block CARNE_ROSA = register("carne_rosa");
   public static final Block CARNE_OSCURA = register("carne_oscura");
   public static final Block TEJIDO_PALIDO = register("tejido_palido");
   public static final Block CARNE_PODRIDA = register("carne_podrida");
   public static final Block CARNE_VENOSA = register("carne_venosa");
   public static final Block BLOQUE_GRASA = register("bloque_grasa", Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.HONEY_BLOCK));
   public static final Item GRASA = (Item)Registry.register(
      BuiltInRegistries.ITEM, Ghostsafio.id("grasa"), new Item(new net.minecraft.world.item.Item.Properties())
   );

   private static Block register(String id, Properties properties) {
      Block block = (Block)Registry.register(BuiltInRegistries.BLOCK, Ghostsafio.id(id), new Block(properties));
      Item item = (Item)Registry.register(BuiltInRegistries.ITEM, Ghostsafio.id(id), new BlockItem(block, new net.minecraft.world.item.Item.Properties()));
      ITEMS.add(item);
      return block;
   }

   private static Block register(String id) {
      return register(id, Properties.ofFullCopy(Blocks.NETHERRACK).sound(SoundType.NETHER_WART));
   }

   public static void register() {
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((ModifyEntries)entries -> ITEMS.forEach(entries::accept));
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register((ModifyEntries)entries -> entries.accept(GRASA));
   }
}
