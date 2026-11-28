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

		JsonObject c = leer("ruleta_casino.json");
		casino = c != null && c.has("premios") ? c : casinoPorDefecto();
		JsonObject p = leer("ruleta_pedestal_intercambio.json");
		pedestal = p != null && p.has("comercios") ? p : pedestalPorDefecto();
		CasinoDatos.aplicar(casino);
		PedestalDatos.aplicar(p != null && p.has("comercios") ? p : null);
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

		escribir("ruleta_casino.json", casino);
		escribir("ruleta_pedestal_intercambio.json", pedestal);
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

	public static JsonObject casinoPorDefecto() {
		return CasinoDatos.jsonPorDefecto();
	}

	public static JsonObject pedestalPorDefecto() {
		return PedestalDatos.porDefecto();
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

	/** Cambios de dificultad activos: los de Hombrosafío si está, si no los guardados aquí. */
	private static List<String> listaCambios() {
		List<String> hs = PuenteHombrosafio.cambiosActivos();
		return hs != null ? hs : CAMBIOS;
	}

	private static JsonArray jsonCambios() {
		JsonArray a = new JsonArray();
		listaCambios().forEach(a::add);
		return a;
	}

	private static String ultimosCambios = "";

	/** Si los cambios activos cambiaron (por ejemplo con /ruleta amarillo), los manda a todos. */
	private static void revisarCambios() {
		String ahora = jsonCambios().toString();

		if (!ahora.equals(ultimosCambios)) {
			ultimosCambios = ahora;
			enviarATodos("cambios", jsonCambios());
		}
	}

	public static boolean esAdmin(ServerPlayer p) {
		// Solo operadores (nivel 2 o más). En un mundo propio, el anfitrión con trucos activados.
		return p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
	}

	private static final Map<java.util.UUID, Boolean> OPERADORES = new java.util.HashMap<>();

	private static void mandarPermiso(ServerPlayer p) {
		boolean admin = esAdmin(p);
		OPERADORES.put(p.getUUID(), admin);
		JsonObject perm = new JsonObject();
		perm.addProperty("admin", admin);
		perm.addProperty("hombrosafio", PuenteHombrosafio.disponible());
		enviar(p, "permiso", perm);
	}

	/** Si a alguien le dieron o le quitaron el op, se lo avisa a su cliente al momento. */
	private static void revisarOperadores() {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			Boolean antes = OPERADORES.get(p.getUUID());

			if (antes == null || antes != esAdmin(p)) {
				mandarPermiso(p);
			}
		}

		OPERADORES.keySet().removeIf(id -> server.getPlayerList().getPlayer(id) == null);
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

		mandarPermiso(p);
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
			// Quien no es operador no puede usar la ruleta; se le recuerda al cliente que no es admin.
			mandarPermiso(p);
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
				String id = json.get("nombre").getAsString();

				if (!PuenteHombrosafio.desactivarCambio(server, id)) {
					CAMBIOS.remove(id);
				}

				guardarTodo();
				enviarATodos("cambios", jsonCambios());
			}
			case "guardar_casino" -> {
				casino = CasinoDatos.aJson(CasinoDatos.leer(json), CasinoDatos.ficha(json));
				CasinoDatos.aplicar(casino);
				guardarTodo();
				enviarATodos("casino", casino);
				p.sendSystemMessage(Component.literal(net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("casinohs") ? "Casino guardado: la tragamonedas ya usa estos premios." : "Casino guardado (CasinoHS no está instalado).").withStyle(ChatFormatting.GREEN));
			}
			case "guardar_pedestal" -> {
				pedestal = json;
				PedestalDatos.aplicar(json);
				guardarTodo();
				enviarATodos("pedestal", pedestal);
				p.sendSystemMessage(Component.literal(PuenteHombrosafio.disponible()
						? "Pedestal de intercambio guardado: el Pedestal de canje ya usa estos comercios."
						: "Pedestal de intercambio guardado (Hombrosafío no está instalado).").withStyle(ChatFormatting.GREEN));
			}
			case "detener" -> {
				PENDIENTES.clear();
				TIRADAS.clear();

				if (PuenteHombrosafio.disponible()) {
					if (PuenteHombrosafio.girando()) {
						PuenteHombrosafio.comando(server, "ruleta cancelar");
					}

					PuenteHombrosafio.comando(server, "ruleta rojo finalizar");
					PuenteHombrosafio.comando(server, "ruleta rojo finalizar cielo");
				}

				enviarATodos("detener", new JsonObject());
			}
			case "lanzar" -> lanzar(p, json);
			default -> {
			}
		}
	}

	/** Orden de los colores, igual en este mod y en Hombrosafío: rojo, rosa, morado, azul, cian, verde, amarillo, naranja. */
	private static final List<String> COLORES = List.of("reviil", "nutria", "sancion", "evento", "mision", "anuncio", "mob", "cambio");

	/** Una tirada de la ruleta de Hombrosafío que todavía está girando; al pararse sale el aviso. */
	private record Tirada(ServerPlayer quien, JsonObject json, List<ServerPlayer> destino, long desde) { }

	private static final List<Tirada> TIRADAS = new ArrayList<>();

	private static String textoRuleta(JsonObject json) {
		String t = json.has("mensaje") ? json.get("mensaje").getAsString() : "";

		if (t.isBlank()) {
			t = json.has("titulo") ? json.get("titulo").getAsString() : "";
		}

		if (t.isBlank()) {
			t = json.has("nombre") ? json.get("nombre").getAsString() : "Ruleta";
		}

		t = t.replaceAll("\\s+", " ").replaceAll("\\p{Cntrl}", "").strip();
		return t.length() > 240 ? t.substring(0, 239) + "…" : t;
	}

	/** Opciones de la ruleta roja (mensaje, animación, cielo); si no vienen, van encendidas. */
	private static boolean opcion(JsonObject json, String clave) {
		return !json.has(clave) || json.get(clave).getAsBoolean();
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

		String tipo = json.get("tipo").getAsString();
		boolean girar = !json.has("girar") || json.get("girar").getAsBoolean();

		if (girar && PuenteHombrosafio.disponible()) {
			if (PuenteHombrosafio.girando() || !TIRADAS.isEmpty()) {
				aviso(quien, "Ya hay una ruleta girando. Espera a que termine o pulsa Detener ruleta.");
				return;
			}

			boolean sinCielo = tipo.equals("reviil") && !opcion(json, "ver_cielo");

			if (!PuenteHombrosafio.girar(server, Math.max(0, COLORES.indexOf(tipo)), textoRuleta(json), sinCielo)) {
				aviso(quien, "La ruleta de Hombrosafío no pudo girar. Mira la consola del servidor.");
				return;
			}

			TIRADAS.add(new Tirada(quien, json, destino, tick));
			return;
		}

		if (girar && !PuenteHombrosafio.disponible()) {
			aviso(quien, PuenteHombrosafio.error() + ": el aviso sale sin girar la ruleta.");
		}

		terminarTirada(quien, json, destino);
	}

	/** Lo que pasa cuando la ruleta se detiene (o enseguida si no gira). */
	private static void terminarTirada(ServerPlayer quien, JsonObject json, List<ServerPlayer> destino) {
		String tipo = json.get("tipo").getAsString();

		if (tipo.equals("reviil") && !opcion(json, "ver_animacion")) {
			PuenteHombrosafio.pararAnimacion(server);
		}

		if (!tipo.equals("reviil") || opcion(json, "ver_mensaje")) {
			for (ServerPlayer p : destino) {
				if (!p.hasDisconnected()) {
					enviar(p, "anuncio", json);
				}
			}
		}

		String para = json.has("para") ? json.get("para").getAsString() : "";

		if (tipo.equals("cambio") && json.has("cambios")) {
			for (JsonElement e : json.getAsJsonArray("cambios")) {
				String n = e.getAsString();

				if (!PuenteHombrosafio.activarCambio(server, n)) {
					if (PuenteHombrosafio.disponible() && quien != null && !quien.hasDisconnected()) {
						aviso(quien, "Hombrosafío no pudo activar el cambio «" + Datos.tituloCambio(n) + "».");
					}

					if (!CAMBIOS.contains(n)) {
						CAMBIOS.add(n);
					}
				}
			}

			guardarTodo();
			enviarATodos("cambios", jsonCambios());
		}

		if (tipo.equals("reviil") && json.has("reviil") && json.get("reviil").getAsBoolean() && json.has("momentos")) {
			for (JsonElement e : json.getAsJsonArray("momentos")) {
				JsonObject o = e.getAsJsonObject();
				MomentosHS.Momento m = MomentosHS.porId(o.get("id").getAsString());

				if (m == null) {
					continue;
				}

				String fallo = PuenteHombrosafio.activarMomento(server, m,
						o.has("segundos") ? o.get("segundos").getAsInt() : 0,
						o.has("num") ? o.get("num").getAsInt() : 1,
						o.has("texto") ? o.get("texto").getAsString() : "");

				if (fallo != null && quien != null && !quien.hasDisconnected()) {
					aviso(quien, "Momento Revil «" + m.titulo() + "» no activado: " + fallo + ".");
				}
			}
		}

		String castigado = json.has("castigado") ? json.get("castigado").getAsString() : para;

		if (tipo.equals("sancion") && !castigado.isEmpty() && json.has("castigo")) {
			String castigo = json.get("castigo").getAsString();

			if (castigo.equals("kick") || castigo.equals("ban")) {
				int minutos = json.has("minutos") ? json.get("minutos").getAsInt() : 0;
				PENDIENTES.add(new Castigo(tick + 200, castigado, castigo, minutos));
			}
		}
	}

	// ---------------------------------------------------------------- tick

	public static void tick(MinecraftServer s) {
		server = s;
		tick++;

		if (tick % 40 == 0) {
			revisarCambios();
		}

		if (tick % 10 == 0) {
			revisarOperadores();
		}

		if (!TIRADAS.isEmpty()) {
			Tirada t = TIRADAS.get(0);

			// Espera a que la ruleta de Hombrosafío se detenga (como mucho 20 s).
			if (tick - t.desde() > 10 && (!PuenteHombrosafio.girando() || tick - t.desde() > 400)) {
				TIRADAS.remove(0);
				terminarTirada(t.quien(), t.json(), t.destino());
			}
		}

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
