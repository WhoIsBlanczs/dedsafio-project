package net.ruleta;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import net.fabricmc.loader.api.FabricLoader;

import net.ruleta.mixin.SlotPrizesAccessor;

/**
 * Premios de la tragamonedas de CasinoHS. Por defecto son los mismos que trae el mod;
 * al guardarlos desde el panel se reemplazan en el juego.
 */
public final class CasinoDatos {
	/** La tragamonedas de CasinoHS guarda en caché 21 ítems para dibujar los rodillos: no puede haber más. */
	public static final int MAXIMO = 21;
	public static final String FICHA = "hombrosafio:casino_token";

	public record Premio(String item, int cantidad, double peso, boolean jackpot, String encantamiento, int nivel) {
		public boolean esLibro() {
			return encantamiento != null && !encantamiento.isBlank();
		}
	}

	private static List<Premio> actuales;

	private CasinoDatos() {
	}

	public static List<Premio> porDefecto() {
		return List.of(
				new Premio("minecraft:diamond_boots", 1, 11.0, true, "", 0),
				new Premio("minecraft:diamond_chestplate", 1, 10.0, true, "", 0),
				new Premio("minecraft:diamond_leggings", 1, 10.0, true, "", 0),
				new Premio("minecraft:diamond_helmet", 1, 11.0, true, "", 0),
				new Premio("minecraft:cooked_porkchop", 6, 35.0, false, "", 0),
				new Premio("minecraft:golden_carrot", 6, 35.0, false, "", 0),
				new Premio("minecraft:golden_apple", 3, 20.0, false, "", 0),
				new Premio("minecraft:iron_pickaxe", 1, 40.0, false, "", 0),
				new Premio("minecraft:iron_ingot", 12, 50.0, false, "", 0),
				new Premio("minecraft:gold_ingot", 12, 48.0, false, "", 0),
				new Premio("hombrosafio:totem_nutria", 1, 1.0, true, "", 0),
				new Premio("hombrosafio:comerciante_apagado", 1, 0.2, true, "", 0),
				new Premio("minecraft:netherite_scrap", 3, 10.848, false, "", 0),
				new Premio("hombrosafio:corazon_rojo", 1, 1.0848, false, "", 0),
				new Premio("minecraft:enchanted_golden_apple", 1, 10.848, false, "", 0),
				new Premio("minecraft:enchanted_book", 1, 32.544, false, "lunge", 2),
				new Premio("minecraft:enchanted_book", 1, 21.696, false, "sharpness", 2),
				new Premio("minecraft:enchanted_book", 1, 21.696, false, "sharpness", 3),
				new Premio("minecraft:enchanted_book", 1, 32.544, false, "power", 1),
				new Premio("minecraft:enchanted_book", 1, 108.48, false, "efficiency", 1),
				new Premio("minecraft:enchanted_book", 1, 216.96, false, "vanishing_curse", 1));
	}

	public static JsonObject jsonPorDefecto() {
		return aJson(porDefecto(), FICHA);
	}

	public static JsonObject aJson(List<Premio> premios, String ficha) {
		JsonObject o = new JsonObject();
		o.addProperty("ficha", ficha);
		JsonArray arr = new JsonArray();

		for (Premio p : premios) {
			JsonObject e = new JsonObject();
			e.addProperty("item", p.item());
			e.addProperty("cantidad", p.cantidad());
			e.addProperty("peso", p.peso());
			e.addProperty("jackpot", p.jackpot());

			if (p.esLibro()) {
				e.addProperty("encantamiento", p.encantamiento());
				e.addProperty("nivel", p.nivel());
			}

			arr.add(e);
		}

		o.add("premios", arr);
		return o;
	}

	public static List<Premio> leer(JsonObject o) {
		List<Premio> r = new ArrayList<>();

		if (o == null || !o.has("premios")) {
			return new ArrayList<>(porDefecto());
		}

		for (JsonElement el : o.getAsJsonArray("premios")) {
			JsonObject e = el.getAsJsonObject();
			String item = e.has("item") ? e.get("item").getAsString() : "minecraft:stone";
			String ench = e.has("encantamiento") ? e.get("encantamiento").getAsString() : "";
			r.add(new Premio(item,
					Math.max(1, e.has("cantidad") ? e.get("cantidad").getAsInt() : 1),
					Math.max(0, e.has("peso") ? e.get("peso").getAsDouble() : 1),
					e.has("jackpot") && e.get("jackpot").getAsBoolean(),
					item.equals("minecraft:enchanted_book") ? ench : "",
					e.has("nivel") ? e.get("nivel").getAsInt() : 1));

			if (r.size() >= MAXIMO) {
				break;
			}
		}

		return r;
	}

	public static String ficha(JsonObject o) {
		return o != null && o.has("ficha") ? o.get("ficha").getAsString() : FICHA;
	}

	/** Pone estos premios en la tragamonedas de CasinoHS (si está instalada). */
	public static void aplicar(JsonObject json) {
		if (!FabricLoader.getInstance().isModLoaded("casinohs")) {
			return;
		}

		List<Premio> premios = leer(json);

		if (premios.isEmpty()) {
			premios = new ArrayList<>(porDefecto());
		}

		try {
			Class<?> clase = Class.forName("com.casinohs.SlotPrizes$Prize");
			Constructor<?> c = clase.getDeclaredConstructor(String.class, int.class, double.class, boolean.class);
			c.setAccessible(true);
			List<Object> lista = new ArrayList<>();

			for (Premio p : premios) {
				lista.add(c.newInstance(p.item(), p.cantidad(), p.peso(), p.jackpot()));
			}

			SlotPrizesAccessor.ruleta$setPremios(List.copyOf(lista));
			Identifier ficha = Identifier.tryParse(ficha(json));
			SlotPrizesAccessor.ruleta$setFicha(ficha != null ? ficha : Identifier.parse(FICHA));
			actuales = List.copyOf(premios);
		} catch (ReflectiveOperationException | RuntimeException | AssertionError e) {
			RuletaMod.LOGGER.warn("[ruleta] No se pudieron cambiar los premios de CasinoHS: {}", e.toString());
		}
	}

	/** El ítem que entrega la tragamonedas para ese premio; null = lo decide CasinoHS. */
	public static ItemStack premio(int indice, ServerLevel level) {
		List<Premio> lista = actuales;

		if (lista == null || indice < 0 || indice >= lista.size()) {
			return null;
		}

		Premio p = lista.get(indice);

		if (p.esLibro()) {
			Identifier id = Identifier.tryParse(p.encantamiento().contains(":") ? p.encantamiento() : "minecraft:" + p.encantamiento());

			if (id != null) {
				var registro = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
				var holder = registro.get(ResourceKey.create(Registries.ENCHANTMENT, id));

				if (holder.isPresent()) {
					Holder<Enchantment> h = holder.get();
					return EnchantmentHelper.createBook(new EnchantmentInstance(h, Math.max(1, p.nivel())));
				}
			}
		}

		Identifier id = Identifier.tryParse(p.item());
		Item item = id == null ? null : BuiltInRegistries.ITEM.getOptional(id).orElse(null);
		return item == null ? ItemStack.EMPTY : new ItemStack(item, p.cantidad());
	}
}
