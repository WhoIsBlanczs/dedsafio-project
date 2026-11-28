package net.ruleta.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.ruleta.Ajustes;
import net.ruleta.Red;
import net.ruleta.Servidor;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Copia en el cliente de lo que manda el servidor. */
public final class ClienteEstado {
	public static final Map<String, Double> AJUSTES = new LinkedHashMap<>(Ajustes.porDefecto());
	public static final List<String> CAMBIOS = new ArrayList<>();
	public static final List<JsonObject> BUNKERS = new ArrayList<>();
	public static JsonObject casino = Servidor.casinoPorDefecto();
	public static JsonObject pedestal = Servidor.pedestalPorDefecto();
	public static boolean admin;
	/** Si el servidor tiene el mod instalado (si no, el menú funciona solo de forma local). */
	public static boolean conectado;
	/** Si el servidor tiene Hombrosafío (su ruleta y sus Momentos Revil). */
	public static boolean hombrosafio;

	private ClienteEstado() {
	}

	public static double valor(String id) {
		Double v = AJUSTES.get(id);
		return v != null ? v : Ajustes.get(id).porDefecto();
	}

	public static void recibir(String tipo, String texto) {
		conectado = true;
		JsonElement json = JsonParser.parseString(texto);

		switch (tipo) {
			case "permiso" -> {
				admin = json.getAsJsonObject().get("admin").getAsBoolean();
				hombrosafio = json.getAsJsonObject().has("hombrosafio") && json.getAsJsonObject().get("hombrosafio").getAsBoolean();
			}
			case "ajustes" -> {
				for (Map.Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
					AJUSTES.put(e.getKey(), e.getValue().getAsDouble());
				}
			}
			case "cambios" -> {
				CAMBIOS.clear();

				for (JsonElement e : json.getAsJsonArray()) {
					CAMBIOS.add(e.getAsString());
				}
			}
			case "bunkers" -> {
				BUNKERS.clear();

				for (JsonElement e : json.getAsJsonArray()) {
					BUNKERS.add(e.getAsJsonObject());
				}
			}
			case "casino" -> {
				casino = json.getAsJsonObject();
				ClienteCasino.aplicar(casino);
			}
			case "pedestal" -> pedestal = json.getAsJsonObject();
			case "anuncio" -> HudAnuncio.mostrar(json.getAsJsonObject());
			case "detener" -> HudAnuncio.detener();
			default -> {
			}
		}
	}

	public static void reiniciar() {
		AJUSTES.clear();
		AJUSTES.putAll(Ajustes.porDefecto());
		CAMBIOS.clear();
		BUNKERS.clear();
		casino = Servidor.casinoPorDefecto();
		pedestal = Servidor.pedestalPorDefecto();
		ClienteCasino.aplicar(casino);
		admin = false;
		conectado = false;
		hombrosafio = false;
		HudAnuncio.detener();
	}

	/** Manda una acción al servidor. Si el servidor no tiene el mod, la aplica en local para que el menú siga funcionando. */
	public static void accion(String tipo, JsonObject json) {
		if (ClientPlayNetworking.canSend(Red.Accion.TYPE)) {
			ClientPlayNetworking.send(new Red.Accion(tipo, json.toString()));
			return;
		}

		switch (tipo) {
			case "ajuste" -> {
				Ajustes.Def d = Ajustes.get(json.get("id").getAsString());
				AJUSTES.put(d.id(), d.ajustar(json.get("valor").getAsDouble()));
			}
			case "restablecer" -> {
				String cat = json.has("categoria") ? json.get("categoria").getAsString() : "";

				for (Ajustes.Def d : Ajustes.todos().values()) {
					if (cat.isEmpty() || d.categoria().equals(cat)) {
						AJUSTES.put(d.id(), d.porDefecto());
					}
				}
			}
			case "desactivar_cambio" -> CAMBIOS.remove(json.get("nombre").getAsString());
			case "guardar_casino" -> {
				casino = json;
				ClienteCasino.aplicar(json);
			}
			case "guardar_pedestal" -> pedestal = json;
			case "detener" -> HudAnuncio.detener();
			case "lanzar" -> {
				HudAnuncio.mostrar(json);

				if (json.has("cambios")) {
					for (JsonElement e : json.getAsJsonArray("cambios")) {
						if (!CAMBIOS.contains(e.getAsString())) {
							CAMBIOS.add(e.getAsString());
						}
					}
				}
			}
			default -> {
			}
		}
	}

	public static JsonArray copiar(JsonArray a) {
		return a.deepCopy();
	}
}
