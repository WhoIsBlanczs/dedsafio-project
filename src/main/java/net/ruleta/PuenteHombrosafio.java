package net.ruleta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Conexión con Hombrosafío (mod "hombrosafio"). Se usa por reflexión para que este mod
 * funcione aunque Hombrosafío no esté instalado: en ese caso todo devuelve "no disponible".
 */
public final class PuenteHombrosafio {
	private static boolean cargado;
	private static boolean disponible;
	private static Class<?> manager;
	private static Class<?> colores;
	private static Class<?> momentos;
	private static Class<?> momento;
	private static Method start;
	private static Method active;
	private static Method activate;
	private static Method isActive;
	private static Method configureExodus;
	private static Method configureBiome;
	private static Method configureVote;
	private static Method refugeReady;
	private static Field skySuppressed;
	private static Class<?> cambio;
	private static Method cambioActivar;
	private static Method cambioDesactivar;
	private static Method cambioActivo;
	private static Field cambioId;
	private static java.lang.reflect.Constructor<?> animacion;
	private static Object animacionTipo;
	private static String error = "";

	private PuenteHombrosafio() {
	}

	private static void cargar() {
		if (cargado) {
			return;
		}

		cargado = true;

		if (!FabricLoader.getInstance().isModLoaded("hombrosafio")) {
			error = "Hombrosafío no está instalado";
			return;
		}

		try {
			manager = Class.forName("ar.hombrosafio.RouletteManager");
			colores = Class.forName("ar.hombrosafio.RouletteColor");
			momentos = Class.forName("ar.hombrosafio.RouletteRevilMoments");
			momento = Class.forName("ar.hombrosafio.RouletteRevilMoment");
			Class<?> fuente = Class.forName("net.minecraft.commands.CommandSourceStack");
			Class<?> desastres = Class.forName("ar.hombrosafio.RedDisasters");
			Class<?> votacion = Class.forName("ar.hombrosafio.RedDeathVote");
			Class<?> refugio = Class.forName("ar.hombrosafio.RefugeEvent");

			start = abrir(manager.getDeclaredMethod("start", fuente, colores, String.class));
			active = abrir(manager.getDeclaredMethod("active"));
			skySuppressed = manager.getDeclaredField("pendingSkySuppressed");
			skySuppressed.setAccessible(true);
			activate = abrir(momentos.getDeclaredMethod("activate", MinecraftServer.class, momento, int.class));
			isActive = abrir(momentos.getDeclaredMethod("isActive", momento));
			configureExodus = abrir(desastres.getDeclaredMethod("configureExodus", momento, int.class));
			configureBiome = abrir(desastres.getDeclaredMethod("configureBiome", String.class, int.class));
			configureVote = abrir(votacion.getDeclaredMethod("configure", int.class, int.class));
			refugeReady = abrir(refugio.getDeclaredMethod("ready"));
			cambio = Class.forName("ar.hombrosafio.RouletteDifficultyChange");
			Class<?> cambios = Class.forName("ar.hombrosafio.RouletteDifficultyChanges");
			cambioActivar = abrir(cambios.getDeclaredMethod("activate", MinecraftServer.class, cambio));
			cambioDesactivar = abrir(cambios.getDeclaredMethod("deactivate", MinecraftServer.class, cambio));
			cambioActivo = abrir(cambios.getDeclaredMethod("isActive", cambio));
			cambioId = cambio.getField("id");
			Class<?> revil = Class.forName("ar.hombrosafio.RevilMomentPayload");
			animacion = revil.getDeclaredConstructor(boolean.class);
			animacion.setAccessible(true);
			Field tipo = revil.getField("TYPE");
			animacionTipo = tipo.get(null);
			disponible = true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			error = "Esta versión de Hombrosafío no es compatible (" + e + ")";
			RuletaMod.LOGGER.warn("[ruleta] No se pudo conectar con Hombrosafío: {}", e.toString());
		}
	}

	private static Method abrir(Method m) {
		m.setAccessible(true);
		return m;
	}

	public static boolean disponible() {
		cargar();
		return disponible;
	}

	public static String error() {
		cargar();
		return error;
	}

	/** Gira la ruleta de Hombrosafío en ese color (0 = rojo ... 7 = naranja). Devuelve true si empezó a girar. */
	public static boolean girar(MinecraftServer server, int color, String texto, boolean sinCielo) {
		if (!disponible()) {
			return false;
		}

		try {
			Object c = colores.getEnumConstants()[Math.floorMod(color, 8)];
			int r = (int) start.invoke(null, server.createCommandSourceStack(), c, texto);

			if (r > 0 && sinCielo) {
				skySuppressed.setBoolean(null, true);
			}

			return r > 0;
		} catch (ReflectiveOperationException | RuntimeException e) {
			RuletaMod.LOGGER.warn("[ruleta] Falló la ruleta de Hombrosafío: {}", e.toString());
			return false;
		}
	}

	public static boolean girando() {
		if (!disponible()) {
			return false;
		}

		try {
			return (boolean) active.invoke(null);
		} catch (ReflectiveOperationException | RuntimeException e) {
			return false;
		}
	}

	public static boolean refugioListo() {
		if (!disponible()) {
			return false;
		}

		try {
			return (boolean) refugeReady.invoke(null);
		} catch (ReflectiveOperationException | RuntimeException e) {
			return false;
		}
	}

	/**
	 * Activa un Momento Revil de Hombrosafío. segundos: plazo elegido; num: muertes (votación);
	 * texto: bioma. Devuelve null si salió bien, o el motivo del fallo.
	 */
	public static String activarMomento(MinecraftServer server, MomentosHS.Momento m, int segundos, int num, String texto) {
		if (!disponible()) {
			return error;
		}

		try {
			@SuppressWarnings({"unchecked", "rawtypes"})
			Object e = Enum.valueOf((Class) momento, m.enumHS());

			if ((boolean) isActive.invoke(null, e)) {
				return "ya estaba activo";
			}

			int refugio = 6000;

			switch (m.parametro()) {
				case MINUTOS -> configureExodus.invoke(null, e, Math.max(1, segundos / 60) * 1200);
				case BIOMA -> configureBiome.invoke(null, texto == null || texto.isBlank() ? "minecraft:plains" : texto, Math.max(1, segundos) * 20);
				case VOTACION -> {
					int vivos = (int) server.getPlayerList().getPlayers().stream().filter(p -> p.isAlive() && !p.isSpectator()).count();

					if (vivos < 2 || num >= vivos) {
						return "la cantidad de muertes debe ser menor que los jugadores vivos (" + vivos + ")";
					}

					configureVote.invoke(null, Math.max(1, num), Math.max(10, segundos));
				}
				case REFUGIO -> {
					if (!refugioListo()) {
						return "primero creá el refugio con /hombrosafio admin refugio crear";
					}

					refugio = Math.max(1, segundos) * 20;
				}
				default -> {
				}
			}

			activate.invoke(null, server, e, refugio);
			return (boolean) isActive.invoke(null, e) ? null : "Hombrosafío no lo pudo activar";
		} catch (ReflectiveOperationException | RuntimeException ex) {
			RuletaMod.LOGGER.warn("[ruleta] Falló el Momento Revil {}: {}", m.id(), ex.toString());
			return "error: " + ex.getClass().getSimpleName();
		}
	}

	private static Object cambioPorId(String id) throws ReflectiveOperationException {
		for (Object c : cambio.getEnumConstants()) {
			if (cambioId.get(c).equals(id)) {
				return c;
			}
		}

		return null;
	}

	/** Activa un cambio de dificultad de Hombrosafío (sale en los cambios activos de la M). */
	public static boolean activarCambio(MinecraftServer server, String id) {
		if (!disponible()) {
			return false;
		}

		try {
			Object c = cambioPorId(id);

			if (c == null) {
				return false;
			}

			cambioActivar.invoke(null, server, c);
			return (boolean) cambioActivo.invoke(null, c);
		} catch (ReflectiveOperationException | RuntimeException e) {
			RuletaMod.LOGGER.warn("[ruleta] Falló el cambio {}: {}", id, e.toString());
			return false;
		}
	}

	public static boolean desactivarCambio(MinecraftServer server, String id) {
		if (!disponible()) {
			return false;
		}

		try {
			Object c = cambioPorId(id);
			return c != null && (boolean) cambioDesactivar.invoke(null, server, c);
		} catch (ReflectiveOperationException | RuntimeException e) {
			RuletaMod.LOGGER.warn("[ruleta] No se pudo quitar el cambio {}: {}", id, e.toString());
			return false;
		}
	}

	/** Ids de los cambios de dificultad activos en Hombrosafío, o null si no está. */
	public static java.util.List<String> cambiosActivos() {
		if (!disponible()) {
			return null;
		}

		java.util.List<String> r = new java.util.ArrayList<>();

		try {
			for (Object c : cambio.getEnumConstants()) {
				String id = (String) cambioId.get(c);

				if (!id.equals("texto") && (boolean) cambioActivo.invoke(null, c)) {
					r.add(id);
				}
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			return null;
		}

		return r;
	}

	/** Corta la animación de Revil (el GIF y la pantalla roja) en todos los jugadores. */
	@SuppressWarnings("unchecked")
	public static void pararAnimacion(MinecraftServer server) {
		if (!disponible()) {
			return;
		}

		try {
			Object payload = animacion.newInstance(false);

			for (var p : server.getPlayerList().getPlayers()) {
				if (net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.canSend(p,
						(net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<?>) animacionTipo)) {
					net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(p,
							(net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload);
				}
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			RuletaMod.LOGGER.warn("[ruleta] No se pudo parar la animación de Revil: {}", e.toString());
		}
	}

	/** Ejecuta un comando de Hombrosafío como la consola (para cancelar y finalizar). */
	public static void comando(MinecraftServer server, String comando) {
		if (disponible()) {
			server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), comando);
		}
	}
}
