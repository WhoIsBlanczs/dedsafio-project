package com.watonstudio.creepermetalico.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record StartFxS2C(BlockPos pos, boolean rojo, String name) implements CustomPacketPayload {
   public static final Type<StartFxS2C> ID = new Type(Identifier.fromNamespaceAndPath("fogata", "start_fx"));
   public static final StreamCodec<RegistryFriendlyByteBuf, StartFxS2C> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, StartFxS2C::pos, ByteBufCodecs.BOOL, StartFxS2C::rojo, ByteBufCodecs.STRING_UTF8, StartFxS2C::name, StartFxS2C::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
