package com.watonstudio.creepermetalico;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record EclipsePayload(boolean on, int age) implements CustomPacketPayload {
   public static final Type<EclipsePayload> ID = new Type(Identifier.fromNamespaceAndPath("eclipse", "state"));
   public static final StreamCodec<RegistryFriendlyByteBuf, EclipsePayload> CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, EclipsePayload::on, ByteBufCodecs.VAR_INT, EclipsePayload::age, EclipsePayload::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
