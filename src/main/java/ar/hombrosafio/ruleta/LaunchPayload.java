package ar.hombrosafio.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Lanzamiento de la ruleta desde el formulario del cliente. */
public record LaunchPayload(int color, String title, String message, String target, boolean mini, String punishment, String timer, String change)
	implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LaunchPayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("launch"));
	public static final StreamCodec<RegistryFriendlyByteBuf, LaunchPayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeVarInt(v.color);
		buf.writeUtf(v.title, 80);
		buf.writeUtf(v.message, 1000);
		buf.writeUtf(v.target, 64);
		buf.writeBoolean(v.mini);
		buf.writeUtf(v.punishment, 32);
		buf.writeUtf(v.timer, 16);
		buf.writeUtf(v.change, 64);
	}, buf -> new LaunchPayload(buf.readVarInt(), buf.readUtf(80), buf.readUtf(1000), buf.readUtf(64), buf.readBoolean(), buf.readUtf(32),
		buf.readUtf(16), buf.readUtf(64)));

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
