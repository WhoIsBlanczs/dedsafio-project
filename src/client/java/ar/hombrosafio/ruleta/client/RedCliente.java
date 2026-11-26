package ar.hombrosafio.ruleta.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Envío de paquetes al servidor desde las pantallas. */
public final class RedCliente {
	private RedCliente() {
	}

	public static void enviar(Object mensaje) {
		ClientPlayNetworking.send((CustomPacketPayload) mensaje);
	}
}
