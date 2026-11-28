package com.watonstudio.creepermetalico;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record TotemEonPayload(int entityId) implements CustomPacketPayload {
   public static final Type<TotemEonPayload> ID = new Type(Identifier.fromNamespaceAndPath("watonstudio", "totem_eon_pop"));
   public static final StreamCodec<RegistryFriendlyByteBuf, TotemEonPayload> CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, TotemEonPayload::entityId, TotemEonPayload::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
