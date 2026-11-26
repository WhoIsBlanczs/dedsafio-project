package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CambiosPayload(String activos) implements CustomPacketPayload {
   public static final Type<CambiosPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "cambios"));
   public static final StreamCodec<FriendlyByteBuf, CambiosPayload> CODEC = StreamCodec.ofMember(CambiosPayload::encode, CambiosPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CambiosPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.activos == null ? "" : m.activos, 4096);
   }

   public static CambiosPayload decode(FriendlyByteBuf b) {
      return new CambiosPayload(b.readUtf(4096));
   }
}
