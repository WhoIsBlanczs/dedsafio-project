package ar.hombrosafio.ruleta.casino;

import ar.hombrosafio.ruleta.RuletaMod;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Registro del casino: tragamonedas, deditas y sonidos. */
public final class Casino {
	public static Block BLOQUE;
	public static Item ITEM;
	public static Item DEDITA;
	public static Item DEDITA_AZUL;
	public static Item DEDITA_ROJO;
	public static Item DEDITA_CIAN;
	public static Item DEDITA_VERDE;
	public static BlockEntityType<TragamonedasBE> BE;
	public static SoundEvent SONIDO_PALANCA;
	public static SoundEvent SONIDO_GIRO;

	private static Item item(String name, ChatFormatting color) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, RuletaMod.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, new DeditaItem(new Item.Properties().setId(key), color));
	}

	private static SoundEvent sound(String name) {
		Identifier id = RuletaMod.id(name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	public static List<Item> todasLasDeditas() {
		return List.of(DEDITA, DEDITA_AZUL, DEDITA_ROJO, DEDITA_CIAN, DEDITA_VERDE);
	}

	public static void register() {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, RuletaMod.id("tragamonedas"));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, RuletaMod.id("tragamonedas"));
		BLOQUE = Registry.register(BuiltInRegistries.BLOCK, blockKey, new TragamonedasBloque(BlockBehaviour.Properties.of().setId(blockKey)
			.mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0F, 6.0F).sound(SoundType.METAL).noOcclusion().noLootTable()));
		ITEM = Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(BLOQUE, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		DEDITA = item("dedita_casino", ChatFormatting.GOLD);
		DEDITA_AZUL = item("dedita_azul", ChatFormatting.BLUE);
		DEDITA_ROJO = item("dedita_rojo", ChatFormatting.RED);
		DEDITA_CIAN = item("dedita_cian", ChatFormatting.AQUA);
		DEDITA_VERDE = item("dedita_verde", ChatFormatting.GREEN);
		BE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, RuletaMod.id("tragamonedas"), new BlockEntityType<>(TragamonedasBE::new, Set.of(BLOQUE)));
		SONIDO_PALANCA = sound("tragamonedas.palanca");
		SONIDO_GIRO = sound("tragamonedas.giro");
		CasinoRed.registrar();
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> output.accept(ITEM));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> todasLasDeditas().forEach(output::accept));
	}

	private Casino() {
	}
}
