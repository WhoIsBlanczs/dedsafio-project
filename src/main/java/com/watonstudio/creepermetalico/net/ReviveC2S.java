package com.watonstudio.creepermetalico.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record ReviveC2S(String name, BlockPos pos) implements CustomPacketPayload {
   public static final Type<ReviveC2S> ID = new Type(Identifier.fromNamespaceAndPath("fogata", "revive"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ReviveC2S> CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, ReviveC2S::name, BlockPos.STREAM_CODEC, ReviveC2S::pos, ReviveC2S::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
