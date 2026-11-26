package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record BunkerAccionPayload(String accion, String clave) implements CustomPacketPayload {
   public static final Type<BunkerAccionPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "bunker_accion"));
   public static final StreamCodec<FriendlyByteBuf, BunkerAccionPayload> CODEC = StreamCodec.ofMember(BunkerAccionPayload::encode, BunkerAccionPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(BunkerAccionPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.accion, 32);
      b.writeUtf(m.clave, 128);
   }

   public static BunkerAccionPayload decode(FriendlyByteBuf b) {
      return new BunkerAccionPayload(b.readUtf(32), b.readUtf(128));
   }
}
