package com.watonstudio.creepermetalico.net;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record OpenMenuS2C(List<String> names, BlockPos pos) implements CustomPacketPayload {
   public static final Type<OpenMenuS2C> ID = new Type(Identifier.fromNamespaceAndPath("fogata", "open_menu"));
   public static final StreamCodec<RegistryFriendlyByteBuf, OpenMenuS2C> CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), OpenMenuS2C::names, BlockPos.STREAM_CODEC, OpenMenuS2C::pos, OpenMenuS2C::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
