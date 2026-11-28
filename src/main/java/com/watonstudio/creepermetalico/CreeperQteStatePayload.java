package com.watonstudio.creepermetalico;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CreeperQteStatePayload(int entityId, int key, int step, int total, int durationTicks) implements CustomPacketPayload {
   public static final Type<CreeperQteStatePayload> ID = new Type(Identifier.fromNamespaceAndPath("watonstudio", "qte_state"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CreeperQteStatePayload> CODEC = new StreamCodec<RegistryFriendlyByteBuf, CreeperQteStatePayload>() {
      public CreeperQteStatePayload decode(RegistryFriendlyByteBuf buf) {
         return new CreeperQteStatePayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
      }

      public void encode(RegistryFriendlyByteBuf buf, CreeperQteStatePayload payload) {
         buf.writeVarInt(payload.entityId());
         buf.writeVarInt(payload.key());
         buf.writeVarInt(payload.step());
         buf.writeVarInt(payload.total());
         buf.writeVarInt(payload.durationTicks());
      }
   };

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
