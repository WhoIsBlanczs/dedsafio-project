package com.watonstudio.creepermetalico.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record TradePhoraC2S(int entityId, int offerIndex) implements CustomPacketPayload {
   public static final Type<TradePhoraC2S> ID = new Type(Identifier.fromNamespaceAndPath("watonstudio", "phora_trade"));
   public static final StreamCodec<RegistryFriendlyByteBuf, TradePhoraC2S> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, TradePhoraC2S::entityId, ByteBufCodecs.VAR_INT, TradePhoraC2S::offerIndex, TradePhoraC2S::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
