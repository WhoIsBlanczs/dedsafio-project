package ar.hombrosafio.ruleta;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Valores de los ajustes (servidor → cliente). {@code canEdit} indica si el jugador puede cambiarlos. */
public record SettingsSyncPayload(boolean canEdit, Map<String, Integer> values) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SettingsSyncPayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("settings_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SettingsSyncPayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeBoolean(v.canEdit);
		buf.writeVarInt(v.values.size());
		for (Map.Entry<String, Integer> e : v.values.entrySet()) {
			buf.writeUtf(e.getKey(), 64);
			buf.writeInt(e.getValue());
		}
	}, buf -> {
		boolean edit = buf.readBoolean();
		int n = Math.min(buf.readVarInt(), 1024);
		Map<String, Integer> map = new LinkedHashMap<>();
		for (int i = 0; i < n; i++) {
			map.put(buf.readUtf(64), buf.readInt());
		}
		return new SettingsSyncPayload(edit, map);
	});

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
