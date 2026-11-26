package ar.hombrosafio.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Acciones simples del cliente: {@code stop} (detener ruleta), {@code change_off} (quitar cambio, arg = id). */
public record ActionPayload(String action, String arg) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ActionPayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("action"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ActionPayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeUtf(v.action, 32);
		buf.writeUtf(v.arg, 64);
	}, buf -> new ActionPayload(buf.readUtf(32), buf.readUtf(64)));

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
