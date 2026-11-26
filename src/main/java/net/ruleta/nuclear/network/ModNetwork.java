package net.ruleta.nuclear.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetwork {
   private ModNetwork() {
   }

   public static void register() {
      PayloadTypeRegistry.clientboundPlay().register(TotemActivatePacket.TYPE, TotemActivatePacket.CODEC);
   }
}
