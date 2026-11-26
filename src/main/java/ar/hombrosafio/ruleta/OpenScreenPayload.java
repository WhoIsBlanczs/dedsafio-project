package ar.hombrosafio.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** El servidor pide al cliente abrir una pantalla del mod: picker, ajustes, cambios, panel. */
public record OpenScreenPayload(String screen) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<OpenScreenPayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("open"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenPayload> CODEC =
		StreamCodec.of((buf, v) -> buf.writeUtf(v.screen, 32), buf -> new OpenScreenPayload(buf.readUtf(32)));

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
