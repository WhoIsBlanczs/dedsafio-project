package ar.hombrosafio.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Cambio de un ajuste (cliente → servidor). {@code id} puede ser un ajuste, {@code "*"} para restablecer todo
 * o {@code "cat:Nombre"} para restablecer una sección (en esos casos {@code value} se ignora).
 */
public record SettingChangePayload(String id, int value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SettingChangePayload> TYPE = new CustomPacketPayload.Type<>(RuletaMod.id("setting_change"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SettingChangePayload> CODEC = StreamCodec.of((buf, v) -> {
		buf.writeUtf(v.id, 64);
		buf.writeInt(v.value);
	}, buf -> new SettingChangePayload(buf.readUtf(64), buf.readInt()));

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
