package net.ruleta.tragamonedas;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CasinoPedirPayload() implements CustomPacketPayload {
   public static final Type<CasinoPedirPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "casino_pedir"));
   public static final StreamCodec<FriendlyByteBuf, CasinoPedirPayload> CODEC = StreamCodec.unit(new CasinoPedirPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
