package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record AbrirPantallaPayload(String pantalla, String datos) implements CustomPacketPayload {
   public static final Type<AbrirPantallaPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "abrir_pantalla"));
   public static final StreamCodec<FriendlyByteBuf, AbrirPantallaPayload> CODEC = StreamCodec.ofMember(
      AbrirPantallaPayload::encode, AbrirPantallaPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(AbrirPantallaPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.pantalla, 64);
      b.writeUtf(m.datos, 32000);
   }

   public static AbrirPantallaPayload decode(FriendlyByteBuf b) {
      return new AbrirPantallaPayload(b.readUtf(64), b.readUtf(32000));
   }
}
