package net.ruleta.estatua;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record PedestalPedirPayload() implements CustomPacketPayload {
   public static final Type<PedestalPedirPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruletafanproject", "pedestal_pedir"));
   public static final StreamCodec<FriendlyByteBuf, PedestalPedirPayload> CODEC = StreamCodec.unit(new PedestalPedirPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
