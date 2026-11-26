package net.ruleta.tragamonedas;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CasinoGuardarPayload(String json) implements CustomPacketPayload {
   public static final Type<CasinoGuardarPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "casino_guardar"));
   public static final StreamCodec<FriendlyByteBuf, CasinoGuardarPayload> CODEC = StreamCodec.ofMember(
      CasinoGuardarPayload::encode, CasinoGuardarPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CasinoGuardarPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.json, 30000);
   }

   public static CasinoGuardarPayload decode(FriendlyByteBuf b) {
      return new CasinoGuardarPayload(b.readUtf(30000));
   }
}
