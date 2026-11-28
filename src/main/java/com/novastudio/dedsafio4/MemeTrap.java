package com.novastudio.dedsafio4;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class MemeTrap {
   static final int MEME = 1;
   static final int TREX = 2;
   static final int TREX_TICKS = 200;
   static final int TREX_COOLDOWN = 200;
   private static final Map<UUID, Long> TREX_UNTIL = new HashMap<>();
   private static final Map<UUID, Long> TREX_READY = new HashMap<>();
   private static boolean registered;

   private MemeTrap() {
   }

   static void register() {
      if (!registered) {
         registered = true;
         PayloadTypeRegistry.clientboundPlay().register(MemeTrap.Trapped.ID, MemeTrap.Trapped.CODEC);
         PayloadTypeRegistry.serverboundPlay().register(MemeTrap.Freed.ID, MemeTrap.Freed.CODEC);
         ServerPlayNetworking.registerGlobalReceiver(MemeTrap.Freed.ID, (var0, var1) -> {
            MemeCreeperEntity var2 = MemeCreeperEntity.HOLDING.get(var1.player().getUUID());
            if (var2 != null && !var2.exploding()) {
               var2.release(var1.player(), true);
            } else if (TREX_UNTIL.containsKey(var1.player().getUUID())) {
               freeTrex(var1.player());
            }
         });
         ServerTickEvents.END_SERVER_TICK.register((EndTick)var0 -> {
            if (!TREX_UNTIL.isEmpty()) {
               for (UUID var2 : List.copyOf(TREX_UNTIL.keySet())) {
                  ServerPlayer var3 = var0.getPlayerList().getPlayer(var2);
                  if (var3 == null) {
                     TREX_UNTIL.remove(var2);
                  } else if (var3.level().getGameTime() >= TREX_UNTIL.get(var2)) {
                     freeTrex(var3);
                  }
               }
            }
         });
      }
   }

   static void send(ServerPlayer var0, boolean var1) {
      send(var0, var1 ? 1 : 0);
   }

   static void send(ServerPlayer var0, int var1) {
      if (ServerPlayNetworking.canSend(var0, MemeTrap.Trapped.ID)) {
         ServerPlayNetworking.send(var0, new MemeTrap.Trapped(var1));
      }
   }

   static void trapTrex(ServerPlayer var0) {
      UUID var1 = var0.getUUID();
      long var2 = var0.level().getGameTime();
      if (!var0.isCreative()
         && !var0.isSpectator()
         && !TREX_UNTIL.containsKey(var1)
         && !MemeCreeperEntity.HOLDING.containsKey(var1)
         && var2 >= TREX_READY.getOrDefault(var1, 0L)) {
         TREX_UNTIL.put(var1, var2 + 200L);
         var0.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 6, false, false, true));
         send(var0, 2);
      }
   }

   static void freeTrex(ServerPlayer var0) {
      if (TREX_UNTIL.remove(var0.getUUID()) != null) {
         var0.removeEffect(MobEffects.SLOWNESS);
         TREX_READY.put(var0.getUUID(), var0.level().getGameTime() + 200L);
         send(var0, 0);
      }
   }

   public record Freed() implements CustomPacketPayload {
      public static final CustomPacketPayload.Type<MemeTrap.Freed> ID = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath("nexo", "meme_freed"));
      public static final StreamCodec<RegistryFriendlyByteBuf, MemeTrap.Freed> CODEC = StreamCodec.unit(new MemeTrap.Freed());

      public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }

   public record Trapped(int mode) implements CustomPacketPayload {
      public static final CustomPacketPayload.Type<MemeTrap.Trapped> ID = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath("nexo", "meme_trap"));
      public static final StreamCodec<ByteBuf, MemeTrap.Trapped> CODEC = StreamCodec.composite(
         ByteBufCodecs.INT, MemeTrap.Trapped::mode, MemeTrap.Trapped::new
      );

      public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }
}
