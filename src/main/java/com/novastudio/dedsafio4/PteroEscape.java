package com.novastudio.dedsafio4;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class PteroEscape {
   private PteroEscape() {
   }

   static void register() {
      PayloadTypeRegistry.serverboundPlay().register(PteroEscape.Escaped.ID, PteroEscape.Escaped.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(PteroEscape.Escaped.ID, (var0, var1) -> {
         if (var1.player().getVehicle() instanceof PterodactiloEntity var2 && var2.carryTicks >= 20) {
            var2.release(var1.player(), true);
         }
      });
   }

   public record Escaped() implements CustomPacketPayload {
      public static final CustomPacketPayload.Type<PteroEscape.Escaped> ID = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath("nexo", "ptero_escape"));
      public static final StreamCodec<RegistryFriendlyByteBuf, PteroEscape.Escaped> CODEC = StreamCodec.unit(new PteroEscape.Escaped());

      public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }
}
