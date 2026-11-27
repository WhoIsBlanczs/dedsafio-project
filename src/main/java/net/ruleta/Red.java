package net.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Dos canales sencillos: el servidor manda datos (JSON) a los clientes y los
 * clientes mandan acciones (JSON) al servidor. Así el resto del mod no depende
 * de muchos tipos de paquete distintos.
 */
public final class Red {
	private static final int MAX = 1 << 20;

	private Red() {
	}

	/** Servidor → cliente. tipo: ajustes, cambios, bunkers, casino, pedestal, anuncio, detener, permiso, aviso. */
	public record Datos(String tipo, String json) implements CustomPacketPayload {
		public static final CustomPacketPayload.Type<Datos> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("datos"));
		public static final StreamCodec<RegistryFriendlyByteBuf, Datos> CODEC = CustomPacketPayload.codec(Datos::write, Datos::new);

		public Datos(RegistryFriendlyByteBuf buf) {
			this(buf.readUtf(64), buf.readUtf(MAX));
		}

		public void write(RegistryFriendlyByteBuf buf) {
			buf.writeUtf(this.tipo, 64);
			buf.writeUtf(this.json, MAX);
		}

		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	/** Cliente → servidor. tipo: ajuste, restablecer, lanzar, detener, desactivar_cambio, guardar_casino, guardar_pedestal, pedir. */
	public record Accion(String tipo, String json) implements CustomPacketPayload {
		public static final CustomPacketPayload.Type<Accion> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("accion"));
		public static final StreamCodec<RegistryFriendlyByteBuf, Accion> CODEC = CustomPacketPayload.codec(Accion::write, Accion::new);

		public Accion(RegistryFriendlyByteBuf buf) {
			this(buf.readUtf(64), buf.readUtf(MAX));
		}

		public void write(RegistryFriendlyByteBuf buf) {
			buf.writeUtf(this.tipo, 64);
			buf.writeUtf(this.json, MAX);
		}

		@Override
		public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}

	public static void registrar() {
		PayloadTypeRegistry.clientboundPlay().register(Datos.TYPE, Datos.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Accion.TYPE, Accion.CODEC);
	}
}
