package ar.hombrosafio.ruleta.casino;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import ar.hombrosafio.ruleta.RuletaMod;

public record CasinoDatosPayload(String json) implements CustomPacketPayload {
   public static final Type<CasinoDatosPayload> TYPE = new Type<>(RuletaMod.id("casino_datos"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CasinoDatosPayload> CODEC = StreamCodec.ofMember(CasinoDatosPayload::encode, CasinoDatosPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CasinoDatosPayload m, RegistryFriendlyByteBuf b) {
      b.writeUtf(m.json, 262144);
   }

   public static CasinoDatosPayload decode(RegistryFriendlyByteBuf b) {
      return new CasinoDatosPayload(b.readUtf(262144));
   }
}
