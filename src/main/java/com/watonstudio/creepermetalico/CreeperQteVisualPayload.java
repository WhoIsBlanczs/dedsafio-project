package com.watonstudio.creepermetalico;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record CreeperQteVisualPayload(int playerId, int stage, boolean active) implements CustomPacketPayload {
   public static final Type<CreeperQteVisualPayload> ID = new Type(Identifier.fromNamespaceAndPath("watonstudio", "qte_visual"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CreeperQteVisualPayload> CODEC = new StreamCodec<RegistryFriendlyByteBuf, CreeperQteVisualPayload>() {
      public CreeperQteVisualPayload decode(RegistryFriendlyByteBuf buf) {
         return new CreeperQteVisualPayload(buf.readVarInt(), buf.readVarInt(), buf.readBoolean());
      }

      public void encode(RegistryFriendlyByteBuf buf, CreeperQteVisualPayload payload) {
         buf.writeVarInt(payload.playerId());
         buf.writeVarInt(payload.stage());
         buf.writeBoolean(payload.active());
      }
   };

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
