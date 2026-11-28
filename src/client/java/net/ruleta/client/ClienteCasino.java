package net.ruleta.client;

import java.util.Arrays;

import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;

import net.ruleta.CasinoDatos;
import net.ruleta.RuletaMod;
import net.ruleta.client.mixin.SlotMachineRendererAccessor;

/** Pone en el cliente los premios del casino (para que los rodillos muestren los ítems nuevos). */
public final class ClienteCasino {
	private ClienteCasino() {
	}

	public static void aplicar(JsonObject json) {
		if (!FabricLoader.getInstance().isModLoaded("casinohs")) {
			return;
		}

		CasinoDatos.aplicar(json);

		try {
			ItemStackCache.limpiar();
		} catch (RuntimeException | LinkageError | AssertionError e) {
			RuletaMod.LOGGER.warn("[ruleta] No se pudo refrescar la tragamonedas: {}", e.toString());
		}
	}

	private static final class ItemStackCache {
		static void limpiar() {
			Arrays.fill(SlotMachineRendererAccessor.ruleta$cache(), null);
		}
	}
}
