package net.ruleta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

/** Estado del lado del servidor: ajustes, cambios activos, casino, pedestal, castigos. */
public final class Servidor {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private static MinecraftServer server;
	private static final Map<String, Double> AJUSTES = new LinkedHashMap<>(Ajustes.porDefecto());
	private static final List<String> CAMBIOS = new ArrayList<>();
	private static final Map<String, Long> BANEOS = new LinkedHashMap<>();
	private static final List<Castigo> PENDIENTES = new ArrayList<>();
	private static JsonObject casino = casinoPorDefecto();
	private static JsonObject pedestal = pedestalPorDefecto();
	private static long tick;

	private record Castigo(long tick, String jugador, String tipo, int minutos) { }

	private Servidor() {
	}

	private static Path dir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	// ---------------------------------------------------------------- carga / guardado

	public static void iniciar(MinecraftServer s) {
		server = s;
		AJUSTES.clear();
		AJUSTES.putAll(Ajustes.porDefecto());
		CAMBIOS.clear();
		BANEOS.clear();
		PENDIENTES.clear();

		JsonObject aj = leer("ruleta_ajustes.json");

		if (aj != null) {
			for (Map.Entry<String, JsonElement> e : aj.entrySet()) {
				Ajustes.Def d = Ajustes.get(e.getKey());

				if (d != null && e.getValue().isJsonPrimitive()) {
					AJUSTES.put(d.id(), d.ajustar(e.getValue().getAsDouble()));
				}
			}
		}

		JsonObject est = leer("ruleta_estado.json");

		if (est != null) {
			if (est.has("cambios")) {
				for (JsonElement e : est.getAsJsonArray("cambios")) {
					CAMBIOS.add(e.getAsString());
				}
			}

			if (est.has("baneos")) {
				for (Map.Entry<String, JsonElement> e : est.getAsJsonObject("baneos").entrySet()) {
					BANEOS.put(e.getKey(), e.getValue().getAsLong());
				}
			}
		}

		JsonObject c = leer("ruleta_tragamonedas.json");
		casino = c != null ? c : casinoPorDefecto();
		JsonObject p = leer("ruleta_pedestal.json");
		pedestal = p != null ? p : pedestalPorDefecto();
	}

	public static void guardarTodo() {
		JsonObject aj = new JsonObject();
		AJUSTES.forEach(aj::addProperty);
		escribir("ruleta_ajustes.json", aj);

		JsonObject est = new JsonObject();
		JsonArray arr = new JsonArray();
		CAMBIOS.forEach(arr::add);
		est.add("cambios", arr);
		JsonObject ban = new JsonObject();
		BANEOS.forEach(ban::addProperty);
		est.add("baneos", ban);
		escribir("ruleta_estado.json", est);

		escribir("ruleta_tragamonedas.json", casino);
		escribir("ruleta_pedestal.json", pedestal);
	}

	private static JsonObject leer(String nombre) {
		Path f = dir().resolve(nombre);

		if (!Files.isRegularFile(f)) {
			return null;
		}

		try {
			return JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8)).getAsJsonObject();
		} catch (Exception e) {
			RuletaMod.LOGGER.warn("No se pudo leer {}: {}", nombre, e.toString());
			return null;
		}
	}

	private static void escribir(String nombre, JsonObject json) {
		try {
			Files.createDirectories(dir());
			Files.writeString(dir().resolve(nombre), GSON.toJson(json), StandardCharsets.UTF_8);
		} catch (IOException e) {
			RuletaMod.LOGGER.warn("No se pudo guardar {}: {}", nombre, e.toString());
		}
	}

	// ---------------------------------------------------------------- valores por defecto

	private static JsonObject entradaRodillo(String item, String nombre, int frecuencia) {
		JsonObject o = new JsonObject();
		o.addProperty("item", item);
		o.addProperty("nombre", nombre);
		o.addProperty("frecuencia", frecuencia);
		return o;
	}

	public static JsonObject casinoPorDefecto() {
		JsonObject o = new JsonObject();
		JsonArray rod = new JsonArray();
		rod.add(entradaRodillo("minecraft:iron_ingot", "Iron Ingot", 30));
		rod.add(entradaRodillo("minecraft:gold_ingot", "Gold Ingot", 24));
		rod.add(entradaRodillo("minecraft:emerald", "Emerald", 16));
		rod.add(entradaRodillo("minecraft:diamond", "Diamond", 10));
		rod.add(entradaRodillo("ruletafanproject:dedita_casino", "Dedita Naranja", 8));
		o.add("rodillos", rod);

		JsonArray pre = new JsonArray();
		String[][] premios = {
				{"minecraft:iron_ingot", "Iron Ingot", "minecraft:iron_ingot", "Iron Ingot", "8"},
				{"minecraft:gold_ingot", "Gold Ingot", "minecraft:gold_ingot", "Gold Ingot", "8"},
				{"minecraft:emerald", "Emerald", "minecraft:emerald", "Emerald", "8"},
				{"minecraft:diamond", "Diamond", "minecraft:diamond", "Diamond", "4"},
				{"ruletafanproject:dedita_casino", "Dedita Naranja", "ruletafanproject:dedita_casino", "Dedita Naranja", "3"}
		};

		for (String[] p : premios) {
			JsonObject e = new JsonObject();
			e.addProperty("simbolo", p[0]);
			e.addProperty("simbolo_nombre", p[1]);
			e.addProperty("item", p[2]);
			e.addProperty("nombre", p[3]);
			e.addProperty("cantidad", Integer.parseInt(p[4]));
			pre.add(e);
		}

		o.add("premios", pre);

		JsonObject cobro = new JsonObject();
		cobro.addProperty("item", "ruletafanproject:dedita_casino");
		cobro.addProperty("nombre", "Dedita Naranja");
		cobro.addProperty("cantidad", 1);
		o.add("cobro", cobro);
		return o;
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

	public static JsonObject pedestalPorDefecto() {
		JsonObject o = new JsonObject();
		JsonArray arr = new JsonArray();
		arr.add(comercio("ruletafanproject:dedita_azul", "Dedita Azul", 2, "ruletafanproject:dedita_casino", "Dedita Naranja", 1));
		arr.add(comercio("ruletafanproject:dedita_azul", "Dedita Azul", 2, "ruletafanproject:dilitio", "Dilitio", 1));
		o.add("comercios", arr);
		return o;
	}

	// ---------------------------------------------------------------- envío

	private static void enviar(ServerPlayer p, String tipo, JsonElement json) {
		if (ServerPlayNetworking.canSend(p, Red.Datos.TYPE)) {
			ServerPlayNetworking.send(p, new Red.Datos(tipo, json.toString()));
		}
	}

	private static void enviarATodos(String tipo, JsonElement json) {
		if (server == null) {
			return;
		}

		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			enviar(p, tipo, json);
		}
	}

	private static JsonObject jsonAjustes() {
		JsonObject o = new JsonObject();
		AJUSTES.forEach(o::addProperty);
		return o;
	}

	private static JsonArray jsonCambios() {
		JsonArray a = new JsonArray();
		CAMBIOS.forEach(a::add);
		return a;
	}

	public static boolean esAdmin(ServerPlayer p) {
		// En un mundo propio (no servidor dedicado) el anfitrión siempre puede usar el menú.
		return p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) || (server != null && !server.isDedicatedServer());
	}

	public static void alEntrar(ServerPlayer p) {
		Long hasta = BANEOS.get(p.getPlainTextName().toLowerCase());

		if (hasta != null) {
			if (hasta > System.currentTimeMillis()) {
				long min = Math.max(1, (hasta - System.currentTimeMillis()) / 60000);
				p.connection.disconnect(Component.literal("GG\nSancionado durante " + min + " min más."));
				return;
			}

			BANEOS.remove(p.getPlainTextName().toLowerCase());
		}

		JsonObject perm = new JsonObject();
		perm.addProperty("admin", esAdmin(p));
		enviar(p, "permiso", perm);
		enviar(p, "ajustes", jsonAjustes());
		enviar(p, "cambios", jsonCambios());
		enviar(p, "bunkers", new JsonArray());
		enviar(p, "casino", casino);
		enviar(p, "pedestal", pedestal);
	}

	private static void aviso(ServerPlayer p, String texto) {
		p.sendSystemMessage(Component.literal(texto).withStyle(ChatFormatting.RED));
	}

	// ---------------------------------------------------------------- acciones del cliente

	public static void recibir(ServerPlayer p, String tipo, String texto) {
		if (server == null) {
			server = p.level().getServer();
		}

		if (tipo.equals("pedir")) {
			alEntrar(p);
			return;
		}

		if (!esAdmin(p)) {
			aviso(p, "Necesitas permisos de administrador para usar la ruleta.");
			return;
		}

		JsonObject json;

		try {
			json = texto.isEmpty() ? new JsonObject() : JsonParser.parseString(texto).getAsJsonObject();
		} catch (Exception e) {
			return;
		}

		switch (tipo) {
			case "ajuste" -> {
				Ajustes.Def d = Ajustes.get(json.get("id").getAsString());

				if (d != null) {
					AJUSTES.put(d.id(), d.ajustar(json.get("valor").getAsDouble()));
					guardarTodo();
					enviarATodos("ajustes", jsonAjustes());
				}
			}
			case "restablecer" -> {
				String cat = json.has("categoria") ? json.get("categoria").getAsString() : "";

				for (Ajustes.Def d : Ajustes.todos().values()) {
					if (cat.isEmpty() || d.categoria().equals(cat)) {
						AJUSTES.put(d.id(), d.porDefecto());
					}
				}

				guardarTodo();
				enviarATodos("ajustes", jsonAjustes());
			}
			case "desactivar_cambio" -> {
				CAMBIOS.remove(json.get("nombre").getAsString());
				guardarTodo();
				enviarATodos("cambios", jsonCambios());
			}
			case "guardar_casino" -> {
				casino = json;
				guardarTodo();
				enviarATodos("casino", casino);
				p.sendSystemMessage(Component.literal("Casino guardado en config/ruleta_tragamonedas.json").withStyle(ChatFormatting.GREEN));
			}
			case "guardar_pedestal" -> {
				pedestal = json;
				guardarTodo();
				enviarATodos("pedestal", pedestal);
				p.sendSystemMessage(Component.literal("Nutria Pedestal guardado en config/ruleta_pedestal.json").withStyle(ChatFormatting.GREEN));
			}
			case "detener" -> {
				PENDIENTES.clear();
				enviarATodos("detener", new JsonObject());
			}
			case "lanzar" -> lanzar(p, json);
			default -> {
			}
		}
	}

	private static void lanzar(ServerPlayer quien, JsonObject json) {
		String para = json.has("para") ? json.get("para").getAsString() : "";
		List<ServerPlayer> destino = new ArrayList<>();

		if (para.isEmpty()) {
			destino.addAll(server.getPlayerList().getPlayers());
		} else {
			ServerPlayer objetivo = server.getPlayerList().getPlayerByName(para);

			if (objetivo == null) {
				aviso(quien, "El jugador " + para + " no está conectado.");
				return;
			}

			destino.add(objetivo);
		}

		for (ServerPlayer p : destino) {
			enviar(p, "anuncio", json);
		}

		String tipo = json.get("tipo").getAsString();

		if (tipo.equals("cambio") && json.has("cambios")) {
			for (JsonElement e : json.getAsJsonArray("cambios")) {
				String n = e.getAsString();

				if (!CAMBIOS.contains(n)) {
					CAMBIOS.add(n);
				}
			}

			guardarTodo();
			enviarATodos("cambios", jsonCambios());
		}

		if (tipo.equals("sancion") && !para.isEmpty() && json.has("castigo")) {
			String castigo = json.get("castigo").getAsString();

			if (castigo.equals("kick") || castigo.equals("ban")) {
				int minutos = json.has("minutos") ? json.get("minutos").getAsInt() : 0;
				PENDIENTES.add(new Castigo(tick + 200, para, castigo, minutos));
			}
		}
	}

	// ---------------------------------------------------------------- tick

	public static void tick(MinecraftServer s) {
		server = s;
		tick++;

		Iterator<Castigo> it = PENDIENTES.iterator();

		while (it.hasNext()) {
			Castigo c = it.next();

			if (c.tick() > tick) {
				continue;
			}

			it.remove();
			ServerPlayer p = s.getPlayerList().getPlayerByName(c.jugador());

			if (c.tipo().equals("ban")) {
				long dur = Math.max(1, c.minutos()) * 60000L;
				BANEOS.put(c.jugador().toLowerCase(), System.currentTimeMillis() + dur);
				guardarTodo();
			}

			if (p != null) {
				p.connection.disconnect(Component.literal("GG"));
			}
		}
	}
}
