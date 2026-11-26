package ar.hombrosafio.ruleta.casino;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import ar.hombrosafio.ruleta.RuletaMod;

public record CasinoGuardarPayload(String json) implements CustomPacketPayload {
   public static final Type<CasinoGuardarPayload> TYPE = new Type<>(RuletaMod.id("casino_guardar"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CasinoGuardarPayload> CODEC = StreamCodec.ofMember(
      CasinoGuardarPayload::encode, CasinoGuardarPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CasinoGuardarPayload m, RegistryFriendlyByteBuf b) {
      b.writeUtf(m.json, 30000);
   }

   public static CasinoGuardarPayload decode(RegistryFriendlyByteBuf b) {
      return new CasinoGuardarPayload(b.readUtf(30000));
   }
}
