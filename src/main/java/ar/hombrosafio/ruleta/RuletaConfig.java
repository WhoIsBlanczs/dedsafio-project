package ar.hombrosafio.ruleta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

/**
 * Registro de ajustes del panel «Ajustes». Los valores viven en el servidor ({@code config/ruleta.json}),
 * se aplican al instante y se sincronizan a los clientes.
 *
 * <p>Otros mods (por ejemplo Hombrosafío) pueden leerlos con {@link #getInt(String)}, {@link #getBool(String)}
 * y {@link #getDecimal(String)}. Los ajustes de funciones que este mod no incluye (bunker, agujeros negros,
 * supernova, tornado…) se guardan y se muestran, pero solo tienen efecto si otro mod los consume.
 */
public final class RuletaConfig {
	public enum Kind { INT, BOOL }

	/**
	 * @param decimals 0 = entero; 1 = el valor guardado es décimas (35 → 3.5)
	 */
	public record Setting(String id, String category, String title, String description, Kind kind, int min, int max, int step, String unit,
		int def, int decimals) {
		public String format(int value) {
			if (kind == Kind.BOOL) {
				return value != 0 ? "Activado" : "Desactivado";
			}
			if (unit.equals("tiempo")) {
				if (value >= 60) {
					return (value / 60) + ":" + String.format(Locale.ROOT, "%02d", value % 60) + " min";
				}
				return value + " s";
			}
			String number;
			if (decimals == 1) {
				number = value % 10 == 0 ? Integer.toString(value / 10) : String.format(Locale.ROOT, "%.1f", value / 10.0);
			} else {
				number = Integer.toString(value);
			}
			return unit.isEmpty() ? number : number + (unit.equals("%") ? " %" : " " + unit);
		}
	}

	public static final List<String> CATEGORIES = List.of("Bunker", "Daño y rayos", "Momentos Revil", "Agujeros negros", "Explosiones",
		"Supernova", "Eclipse", "Phora y misiles", "Meteoritos", "Cambios de dificultad", "Tornado", "Herobrine y Linterna");

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final List<Setting> SETTINGS = new ArrayList<>();
	private static final Map<String, Setting> BY_ID = new LinkedHashMap<>();
	private static final Map<String, Integer> VALUES = new LinkedHashMap<>();
	private static Path file;

	static {
		String B = "Bunker";
		num(B, "bunker.min_distance", "Distancia mínima entre bunkers",
			"No se puede construir un bunker a menos de esta distancia de otro (en la misma dimensión). 0 = sin límite.", 0, 5000, 50, "bloques", 1500);
		num(B, "bunker.arrival_time", "Tiempo para llegar al bunker",
			"Valor por defecto del contador antes de que se cierre la puerta (si al activar el momento no se escribe otro).", 10, 3600, 10, "tiempo", 300);
		num(B, "bunker.closed_time", "Tiempo con la puerta cerrada", "Valor por defecto de lo que dura la puerta cerrada (luego se abre).", 10, 3600, 10,
			"tiempo", 150);
		num(B, "bunker.open_at_end", "Puerta abierta al terminar",
			"Cuánto se queda abierta la puerta del bunker cuando termina el momento, antes de cerrarse sola y sacar a quien siga dentro.", 5, 3600, 5,
			"tiempo", 60);
		num(B, "bunker.auto_close", "Cierre automático de la puerta",
			"Si abres a mano la puerta del bunker (fuera del contador), se vuelve a cerrar sola pasado este tiempo.", 1, 600, 1, "tiempo", 5);
		num(B, "bunker.no_impact_zone", "Zona sin impactos",
			"Los meteoritos y los misiles de los Phora nunca caen a menos de estos bloques de cualquier bunker (ni encima). 0 = sin zona.", 0, 500, 5,
			"bloques", 80);
		num(B, "bunker.reopen", "Reapertura de la puerta",
			"Si cierran a mano la puerta del bunker mientras el contador aún corre (el servidor la quiere abierta), se vuelve a abrir pasado este tiempo.",
			1, 600, 1, "tiempo", 5);
		bool(B, "bunker.anyone_opens", "Cualquiera puede abrir la puerta",
			"Desactivado: solo administradores y jugadores en creativo pueden abrir la puerta del bunker a mano.", false);
		num(B, "bunker.build_level", "Nivel para construir dentro",
			"Nivel de permisos mínimo para colocar bloques, cubos, cofres… dentro de un bunker (los jugadores en creativo siempre pueden).", 0, 4, 1, "", 2);
		bool(B, "bunker.kick_players", "Sacar a los jugadores fuera de un momento",
			"Si no hay un Momento Revil del bunker en marcha, quien esté dentro de un bunker (sin creativo) es sacado afuera.", true);
		num(B, "bunker.build_warning", "Duración del aviso de construcción",
			"Cuánto dura en pantalla el texto «No puedes colocar bloques en esta zona».", 1, 30, 1, "tiempo", 2);

		String D = "Daño y rayos";
		bool(D, "damage.lightning_visual", "Caen rayos en el Rayo mortal",
			"Rayos visuales (sin fuego ni daño de rayo) alrededor de quien está fuera del bunker.", true);
		num(D, "damage.lightning_chance", "Probabilidad de rayo", "Probabilidad de que cada golpe de daño venga con un rayo.", 0, 100, 5, "%", 35);
		num(D, "damage.lightning_spread", "Dispersión de los rayos", "A qué distancia máxima del jugador puede caer cada rayo.", 0, 64, 1, "bloques", 16);
		num(D, "damage.toxic_gas", "Daño del gas tóxico", "Daño por segundo del Gas tóxico de Revil a quien no esté en su capa.", 0, 20, 1, "corazones", 1);
		num(D, "damage.herobrine", "Daño de Herobrine", "Daño de cada golpe de Herobrine (ignora la armadura).", 0, 20, 1, "corazones", 2);

		String M = "Momentos Revil";
		num(M, "revil.gas_counter", "Contador del gas tóxico",
			"Valor por defecto del temporizador del gas (tiempo para llegar a la capa pedida).", 10, 3600, 10, "tiempo", 300);
		num(M, "revil.gas_up", "Capa del gas (subir)", "Quien esté por debajo de esta altura recibe daño.", -64, 320, 5, "capa", 250);
		num(M, "revil.gas_down", "Capa del gas (bajar)", "Quien esté a esta altura o por encima recibe daño.", -64, 320, 5, "capa", 0);
		num(M, "revil.electric_creeper", "Creeper Eléctrico cada…",
			"De media, cada cuántos segundos le cae un Creeper Eléctrico a cada jugador.", 5, 600, 5, "tiempo", 75);
		num(M, "revil.blind_duration", "Duración de la ceguera", "Segundos de ceguera del momento «Ceguera y Creepers Eléctricos».", 1, 120, 1, "tiempo", 15);

		String H = "Agujeros negros";
		num(H, "blackhole.min", "Mínimo de agujeros", "Cuántos agujeros negros hay a la vez como mínimo.", 0, 100, 1, "", 3);
		num(H, "blackhole.max", "Máximo de agujeros", "Cuántos agujeros negros hay a la vez como máximo.", 0, 100, 1, "", 12);
		num(H, "blackhole.per_player", "Agujeros por jugador",
			"Cuántos agujeros salen por cada jugador conectado (entre el mínimo y el máximo).", 0, 20, 1, "", 2);
		num(H, "blackhole.separation", "Separación entre agujeros", "Distancia mínima entre dos agujeros negros.", 0, 1000, 10, "bloques", 150);
		num(H, "blackhole.bunker_distance", "Distancia mínima al bunker", "Los agujeros nunca salen más cerca del bunker que esto.", 0, 1000, 10,
			"bloques", 200);
		num(H, "blackhole.replenish", "Reponer cada",
			"Cada cuántos segundos el momento revisa los agujeros: quita los que quedaron lejos de todos y pone nuevos cerca de los jugadores.", 1, 600, 1,
			"tiempo", 10);
		num(H, "blackhole.remove_distance", "Distancia para quitarlos",
			"Un agujero a más de esta distancia de todos los jugadores se quita (y sale otro cerca de alguien).", 50, 5000, 50, "bloques", 700);
		dec(H, "blackhole.medium_size", "Tamaño mediano (x radio)", "Los agujeros medianos multiplican el radio de atracción por esto.", 10, 100, 1, "", 20);
		dec(H, "blackhole.giant_size", "Tamaño gigante (x radio)", "Los agujeros gigantes multiplican el radio de atracción por esto.", 10, 200, 1, "", 35);

		String S = "Supernova";
		num(S, "supernova.fuse", "Tiempo hasta que explota",
			"Segundos de cuenta atrás desde que se activa hasta que la estrella explota (mínimo 30).", 30, 3600, 5, "tiempo", 30);
		num(S, "supernova.blast", "Duración de la explosión",
			"Segundos que dura la explosión tras estallar (la esfera crece y la onda avanza dentro de este tiempo).", 5, 600, 1, "tiempo", 24);
		num(S, "supernova.fire_inside", "Fuego dentro de la explosión", "Segundos en llamas de quien sobreviva dentro de la esfera.", 0, 120, 1, "tiempo", 15);
		num(S, "supernova.fire_wave", "Fuego de la onda expansiva", "Segundos en llamas de quien alcanza la onda fuera de la esfera.", 0, 120, 1, "tiempo", 5);
		dec(S, "supernova.armor_wear", "Desgaste de armadura por segundo",
			"Porcentaje de durabilidad que pierde cada pieza de armadura por segundo dentro de la onda.", 0, 1000, 5, "%", 25);

		String T = "Tornado";
		dec(T, "tornado.swallow_height", "Altura a la que te traga", "A qué fracción de la altura del embudo te traga (0.6 = al 60 %).", 1, 10, 1,
			"x altura", 6);
		bool(T, "tornado.swallow_top", "Te traga al llegar arriba", "Apagado: al llegar arriba te suelta (con caída lenta) en vez de tragarte.", true);
		num(T, "tornado.revil_count", "Momento Revil: cantidad de tornados",
			"Cuántos tornados salen en el Momento Revil de los tornados (repartidos por los chunks cargados, a la separación mínima).", 1, 100, 1, "", 12);
		num(T, "tornado.revil_replenish", "Momento Revil: reponer cada",
			"Cada cuántos segundos se quitan los tornados que quedaron lejos de todos y se ponen nuevos cerca de los jugadores.", 1, 600, 1, "tiempo", 10);
		num(T, "tornado.revil_separation", "Momento Revil: separación mínima", "Distancia mínima entre los tornados de ese Momento Revil.", 0, 2000, 10,
			"bloques", 400);

		// Estos sí tienen efecto en este mod.
		String C = "Cambios de dificultad";
		num(C, "diff.pearl_damage", "Daño de las Ender Pearls", "Puntos de vida (2 = un corazón) que hacen las Ender Pearls con el cambio «perlas_10».", 1, 100,
			1, "pts de vida", 10);
		num(C, "diff.fire_mult_soft", "Multiplicador de fuego (suave)", "Cuánto se multiplica el daño de fuego, lava y magma con «fuego_x3».", 1, 100, 1,
			"x", 3);
		num(C, "diff.fire_mult_hard", "Multiplicador de fuego (extremo)", "Cuánto se multiplica el daño de fuego, lava y magma con «fuego_x10».", 1, 100, 1,
			"x", 10);
		num(C, "diff.fall_mult", "Multiplicador de caída", "Cuánto se multiplica el daño por caída con «caida_x2».", 1, 100, 1, "x", 2);
		num(C, "diff.drown_mult", "Multiplicador de ahogamiento", "Cuánto se multiplica el daño por ahogamiento con «ahogamiento_x10».", 1, 100, 1, "x", 10);
		num(C, "diff.air_mult", "Velocidad de consumo de oxígeno", "Cuántas veces más rápido se gasta el aire con «oxigeno_x5».", 1, 20, 1, "x", 5);
		num(C, "diff.zombie_speed", "Velocidad de los zombis", "Nivel de Velocidad permanente de los zombis con «zombis_rapidos» (2 = Velocidad II).", 1, 10,
			1, "", 2);
		num(C, "diff.spider_speed", "Velocidad de las arañas", "Nivel de Velocidad permanente de las arañas con «aranas_velocidad_1».", 1, 10, 1, "", 1);

		for (Setting s : SETTINGS) {
			VALUES.put(s.id(), s.def());
		}
	}

	private static void add(Setting s) {
		SETTINGS.add(s);
		BY_ID.put(s.id(), s);
	}

	private static void num(String cat, String id, String title, String desc, int min, int max, int step, String unit, int def) {
		add(new Setting(id, cat, title, desc, Kind.INT, min, max, step, unit, def, 0));
	}

	private static void dec(String cat, String id, String title, String desc, int min, int max, int step, String unit, int def) {
		add(new Setting(id, cat, title, desc, Kind.INT, min, max, step, unit, def, 1));
	}

	private static void bool(String cat, String id, String title, String desc, boolean def) {
		add(new Setting(id, cat, title, desc, Kind.BOOL, 0, 1, 1, "", def ? 1 : 0, 0));
	}

	public static List<Setting> all() {
		return SETTINGS;
	}

	public static Setting setting(String id) {
		return BY_ID.get(id);
	}

	public static Map<String, Integer> snapshot() {
		return new LinkedHashMap<>(VALUES);
	}

	/** Reemplaza los valores locales (lo usa el cliente al recibir la sincronización). */
	public static void replace(Map<String, Integer> values) {
		for (Map.Entry<String, Integer> e : values.entrySet()) {
			Setting s = BY_ID.get(e.getKey());
			if (s != null) {
				VALUES.put(s.id(), Math.clamp(e.getValue(), s.min(), s.max()));
			}
		}
	}

	public static int getInt(String id) {
		Integer v = VALUES.get(id);
		return v != null ? v : BY_ID.containsKey(id) ? BY_ID.get(id).def() : 0;
	}

	public static boolean getBool(String id) {
		return getInt(id) != 0;
	}

	/** Valor con decimales (los ajustes con {@code decimals = 1} se guardan en décimas). */
	public static double getDecimal(String id) {
		Setting s = BY_ID.get(id);
		return s != null && s.decimals() == 1 ? getInt(id) / 10.0 : getInt(id);
	}

	/** Cambia un ajuste en el servidor, lo guarda y lo envía a todos. */
	public static boolean set(net.minecraft.server.MinecraftServer server, String id, int value) {
		Setting s = BY_ID.get(id);
		if (s == null) {
			return false;
		}
		int v = Math.clamp(value, s.min(), s.max());
		if (s.kind() == Kind.BOOL) {
			v = v != 0 ? 1 : 0;
		}
		VALUES.put(id, v);
		save();
		syncAll(server);
		return true;
	}

	public static void reset(net.minecraft.server.MinecraftServer server, String target) {
		for (Setting s : SETTINGS) {
			if (target.equals("*") || (target.startsWith("cat:") && s.category().equals(target.substring(4)))) {
				VALUES.put(s.id(), s.def());
			}
		}
		save();
		syncAll(server);
	}

	public static boolean canEdit(ServerPlayer player) {
		return player.createCommandSourceStack().permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
			|| player.level().getServer().isSingleplayer();
	}

	public static void sync(ServerPlayer player) {
		if (net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(player, SettingsSyncPayload.TYPE)) {
			net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new SettingsSyncPayload(canEdit(player), snapshot()));
		}
	}

	public static void syncAll(net.minecraft.server.MinecraftServer server) {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			sync(p);
		}
	}

	public static void register() {
		file = FabricLoader.getInstance().getConfigDir().resolve("ruleta.json");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> load());
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.player));
	}

	private static void load() {
		for (Setting s : SETTINGS) {
			VALUES.put(s.id(), s.def());
		}
		if (file != null && Files.exists(file)) {
			try {
				JsonObject json = GSON.fromJson(Files.readString(file), JsonObject.class);
				if (json != null) {
					for (Setting s : SETTINGS) {
						if (json.has(s.id())) {
							VALUES.put(s.id(), Math.clamp(json.get(s.id()).getAsInt(), s.min(), s.max()));
						}
					}
				}
			} catch (IOException | RuntimeException e) {
				System.err.println("[ruleta] No se pudo leer " + file + ": " + e);
			}
		}
	}

	private static void save() {
		if (file == null) {
			return;
		}
		JsonObject json = new JsonObject();
		for (Setting s : SETTINGS) {
			json.addProperty(s.id(), VALUES.get(s.id()));
		}
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, GSON.toJson(json));
		} catch (IOException e) {
			System.err.println("[ruleta] No se pudo guardar " + file + ": " + e);
		}
	}

	private RuletaConfig() {
	}
}
