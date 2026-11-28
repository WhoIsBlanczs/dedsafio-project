package com.watonstudio.creepermetalico.net;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenPhoraS2C(int entityId, List<String> offers) implements CustomPacketPayload {
   public static final Type<OpenPhoraS2C> ID = new Type(ResourceLocation.fromNamespaceAndPath("watonstudio", "phora_open"));
   public static final StreamCodec<RegistryFriendlyByteBuf, OpenPhoraS2C> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, OpenPhoraS2C::entityId, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), OpenPhoraS2C::offers, OpenPhoraS2C::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
