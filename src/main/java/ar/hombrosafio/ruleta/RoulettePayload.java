package ar.hombrosafio.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Estado de la ruleta que se sincroniza a todos los clientes. */
public record RoulettePayload(long sequence, int elapsed, int color, String message) implements CustomPacketPayload {
	public static final RoulettePayload NONE = new RoulettePayload(0L, -1, 0, "");
	public static final CustomPacketPayload.Type<RoulettePayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("roulette"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RoulettePayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeVarLong(v.sequence);
		buf.writeVarInt(v.elapsed);
		buf.writeVarInt(v.color);
		buf.writeUtf(v.message, 256);
	}, buf -> new RoulettePayload(buf.readVarLong(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(256)));

	public RoulettePayload {
		if (elapsed < -1 || elapsed > RouletteColor.TOTAL || color < 0 || color >= RouletteColor.values().length || message.length() > 256) {
			throw new IllegalArgumentException("Invalid roulette payload");
		}
	}

	public boolean active() {
		return this.elapsed >= 0 && this.elapsed < RouletteColor.TOTAL;
	}

	public RouletteColor chosen() {
		return RouletteColor.values()[this.color];
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
