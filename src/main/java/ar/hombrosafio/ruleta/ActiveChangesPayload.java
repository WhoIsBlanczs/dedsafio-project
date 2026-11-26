package ar.hombrosafio.ruleta;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Cambios de dificultad activos (id + momento en que se activaron). */
public record ActiveChangesPayload(List<String> ids, List<Long> since) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ActiveChangesPayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("active_changes"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ActiveChangesPayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeVarInt(v.ids.size());
		for (int i = 0; i < v.ids.size(); i++) {
			buf.writeUtf(v.ids.get(i), 64);
			buf.writeLong(v.since.get(i));
		}
	}, buf -> {
		int n = Math.min(buf.readVarInt(), 256);
		List<String> ids = new ArrayList<>();
		List<Long> since = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			ids.add(buf.readUtf(64));
			since.add(buf.readLong());
		}
		return new ActiveChangesPayload(ids, since);
	});

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
