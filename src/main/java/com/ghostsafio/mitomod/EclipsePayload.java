package com.ghostsafio.mitomod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record EclipsePayload(boolean start, int durationTicks, int elapsedTicks) implements CustomPacketPayload {
   public static final Type<EclipsePayload> TYPE = new Type(Ghostsafio.id("eclipse"));
   public static final StreamCodec<RegistryFriendlyByteBuf, EclipsePayload> CODEC = StreamCodec.of((buf, p) -> {
      buf.writeBoolean(p.start());
      buf.writeVarInt(p.durationTicks());
      buf.writeVarInt(p.elapsedTicks());
   }, buf -> new EclipsePayload(buf.readBoolean(), buf.readVarInt(), buf.readVarInt()));

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
