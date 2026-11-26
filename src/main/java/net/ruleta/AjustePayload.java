package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record AjustePayload(String id, double valor, boolean reiniciar) implements CustomPacketPayload {
   public static final Type<AjustePayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "ajuste"));
   public static final StreamCodec<FriendlyByteBuf, AjustePayload> CODEC = StreamCodec.ofMember(AjustePayload::encode, AjustePayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(AjustePayload m, FriendlyByteBuf b) {
      b.writeUtf(m.id, 64);
      b.writeDouble(m.valor);
      b.writeBoolean(m.reiniciar);
   }

   public static AjustePayload decode(FriendlyByteBuf b) {
      return new AjustePayload(b.readUtf(64), b.readDouble(), b.readBoolean());
   }
}
