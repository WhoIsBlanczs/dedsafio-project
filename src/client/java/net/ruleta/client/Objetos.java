package net.ruleta.client;

import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Utilidades para mostrar ítems guardados como texto ("minecraft:diamond"). */
public final class Objetos {
	private Objetos() {
	}

	public static Optional<Item> item(String id) {
		Identifier rl = Identifier.tryParse(id);

		if (rl == null) {
			return Optional.empty();
		}

		return BuiltInRegistries.ITEM.getOptional(rl);
	}

	public static ItemStack stack(String id) {
		return item(id).map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	/** Nombre del ítem; si no existe en este juego usa el nombre guardado. */
	public static String nombre(String id, String guardado) {
		ItemStack s = stack(id);

		if (!s.isEmpty()) {
			return s.getHoverName().getString();
		}

		return guardado != null && !guardado.isEmpty() ? guardado : id;
	}

	public static String id(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}
}
