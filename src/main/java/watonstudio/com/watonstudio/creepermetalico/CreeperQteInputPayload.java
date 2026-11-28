package com.watonstudio.creepermetalico;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CreeperQteInputPayload(int key, int step) implements CustomPacketPayload {
   public static final Type<CreeperQteInputPayload> ID = new Type(ResourceLocation.fromNamespaceAndPath("watonstudio", "qte_input"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CreeperQteInputPayload> CODEC = new StreamCodec<RegistryFriendlyByteBuf, CreeperQteInputPayload>() {
      public CreeperQteInputPayload decode(RegistryFriendlyByteBuf buf) {
         return new CreeperQteInputPayload(buf.readVarInt(), buf.readVarInt());
      }

      public void encode(RegistryFriendlyByteBuf buf, CreeperQteInputPayload payload) {
         buf.writeVarInt(payload.key());
         buf.writeVarInt(payload.step());
      }
   };

   public Type<? extends CustomPacketPayload> type() {
      return ID;
   }
}
