package com.supernova;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SupernovaNet {
   private static boolean registered;

   public static void init() {
      if (!registered) {
         registered = true;
         PayloadTypeRegistry.playS2C().register(SupernovaNet.SyncMessage.TYPE, SupernovaNet.SyncMessage.CODEC);
      }
   }

   public static void broadcast(SupernovaEntity e, boolean remove) {
      if (e.level() instanceof ServerLevel sl) {
         SupernovaNet.SyncMessage var6 = SupernovaNet.SyncMessage.of(e, remove);

         for (ServerPlayer p : sl.players()) {
            ServerPlayNetworking.send(p, var6);
         }
      }
   }

   public static void sendTo(ServerPlayer p, SupernovaEntity e) {
      ServerPlayNetworking.send(p, SupernovaNet.SyncMessage.of(e, false));
   }

   public static class SyncMessage implements CustomPacketPayload {
      public static final Type<SupernovaNet.SyncMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("supernova", "sync"));
      public static final StreamCodec<FriendlyByteBuf, SupernovaNet.SyncMessage> CODEC = StreamCodec.ofMember(
         SupernovaNet.SyncMessage::encode, SupernovaNet.SyncMessage::decode
      );
      public final int id;
      public final String dim;
      public final double x;
      public final double y;
      public final double z;
      public final float maxRadius;
      public final int born;
      public final int detonated;
      public final int hold;
      public final boolean remove;

      public SyncMessage(int id, String dim, double x, double y, double z, float maxRadius, int born, int detonated, int hold, boolean remove) {
         this.id = id;
         this.dim = dim;
         this.x = x;
         this.y = y;
         this.z = z;
         this.maxRadius = maxRadius;
         this.born = born;
         this.detonated = detonated;
         this.hold = hold;
         this.remove = remove;
      }

      public Type<? extends CustomPacketPayload> type() {
         return TYPE;
      }

      static SupernovaNet.SyncMessage of(SupernovaEntity e, boolean remove) {
         return new SupernovaNet.SyncMessage(
            e.getId(),
            e.level().dimension().location().toString(),
            e.getX(),
            e.getY(),
            e.getZ(),
            e.getMaxRadius(),
            e.bornTick(),
            e.detonatedTick(),
            e.getHoldTicks(),
            remove
         );
      }

      public SupernovaState toState() {
         return new SupernovaState(this.id, this.dim, this.x, this.y, this.z, this.maxRadius, this.born, this.detonated, this.hold);
      }

      static void encode(SupernovaNet.SyncMessage m, FriendlyByteBuf b) {
         b.writeInt(m.id);
         b.writeUtf(m.dim);
         b.writeDouble(m.x);
         b.writeDouble(m.y);
         b.writeDouble(m.z);
         b.writeFloat(m.maxRadius);
         b.writeInt(m.born);
         b.writeInt(m.detonated);
         b.writeInt(m.hold);
         b.writeBoolean(m.remove);
      }

      static SupernovaNet.SyncMessage decode(FriendlyByteBuf b) {
         return new SupernovaNet.SyncMessage(
            b.readInt(), b.readUtf(), b.readDouble(), b.readDouble(), b.readDouble(), b.readFloat(), b.readInt(), b.readInt(), b.readInt(), b.readBoolean()
         );
      }
   }
}
