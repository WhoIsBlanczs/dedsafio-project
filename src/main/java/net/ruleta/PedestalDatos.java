package net.ruleta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

/**
 * Comercios del Pedestal de canje (pedestal de intercambio) de Hombrosafío.
 * Por defecto son los que trae Hombrosafío; al guardarlos desde el panel se usan en el pedestal.
 */
public final class PedestalDatos {
	private static JsonObject actual;

	private PedestalDatos() {
	}

	private static JsonObject comercio(String das, String dasNombre, int dasN, String recibes, String recibesNombre, int recibesN) {
		JsonObject o = new JsonObject();
		o.addProperty("das", das);
		o.addProperty("das_nombre", dasNombre);
		o.addProperty("das_n", dasN);
		o.addProperty("recibes", recibes);
		o.addProperty("recibes_nombre", recibesNombre);
		o.addProperty("recibes_n", recibesN);
		return o;
	}

	public static JsonObject porDefecto() {
		JsonObject o = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add(comercio("hombrosafio:casino_token_blue", "Ficha azul", 4, "hombrosafio:casino_token", "Ficha naranja", 1));
		arr.add(comercio("hombrosafio:cristal_verde", "Dilitio", 1, "hombrosafio:casino_token_blue", "Ficha azul", 1));
		o.add("comercios", arr);
		return o;
	}

	public static void aplicar(JsonObject json) {
		actual = json;
	}

	private static Item item(String id) {
		Identifier rl = Identifier.tryParse(id);
		return rl == null ? null : BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
	}

	/** Las ofertas para el pedestal; null = las de Hombrosafío. */
	public static MerchantOffers ofertas() {
		JsonObject json = actual;

		if (json == null || !json.has("comercios")) {
			return null;
		}

		MerchantOffers r = new MerchantOffers();

		for (JsonElement el : json.getAsJsonArray("comercios")) {
			JsonObject o = el.getAsJsonObject();
			Item das = item(o.has("das") ? o.get("das").getAsString() : "");
			Item recibes = item(o.has("recibes") ? o.get("recibes").getAsString() : "");

			if (das == null || recibes == null) {
				continue;
			}

			int dasN = Math.max(1, Math.min(64, o.has("das_n") ? o.get("das_n").getAsInt() : 1));
			int recibesN = Math.max(1, Math.min(64, o.has("recibes_n") ? o.get("recibes_n").getAsInt() : 1));
			r.add(new MerchantOffer(new ItemCost(das, dasN), new ItemStack(recibes, recibesN), Integer.MAX_VALUE, 0, 0.0F));
		}

		return r;
	}
}
