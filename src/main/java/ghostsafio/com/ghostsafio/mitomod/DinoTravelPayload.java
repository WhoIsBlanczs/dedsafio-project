package com.ghostsafio.mitomod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record DinoTravelPayload() implements CustomPacketPayload {
   public static final Type<DinoTravelPayload> TYPE = new Type(Ghostsafio.id("dino_travel"));
   public static final StreamCodec<RegistryFriendlyByteBuf, DinoTravelPayload> CODEC = StreamCodec.unit(new DinoTravelPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
