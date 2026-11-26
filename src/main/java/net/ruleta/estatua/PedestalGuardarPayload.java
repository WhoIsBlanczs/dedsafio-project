package net.ruleta.estatua;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record PedestalGuardarPayload(String json) implements CustomPacketPayload {
   public static final Type<PedestalGuardarPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "pedestal_guardar"));
   public static final StreamCodec<FriendlyByteBuf, PedestalGuardarPayload> CODEC = StreamCodec.ofMember(
      PedestalGuardarPayload::encode, PedestalGuardarPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(PedestalGuardarPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.json, 30000);
   }

   public static PedestalGuardarPayload decode(FriendlyByteBuf b) {
      return new PedestalGuardarPayload(b.readUtf(30000));
   }
}
