package com.ghostsafio.mitomod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record SupernovaPayload(boolean start, double x, double y, double z, double radius, int countdownTicks, int waveTicks, int fadeTicks, int elapsedTicks)
   implements CustomPacketPayload {
   public static final Type<SupernovaPayload> TYPE = new Type(Ghostsafio.id("supernova"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SupernovaPayload> CODEC = StreamCodec.ofMember(SupernovaPayload::write, SupernovaPayload::new);

   public SupernovaPayload(RegistryFriendlyByteBuf buf) {
      this(
         buf.readBoolean(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()
      );
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeBoolean(this.start);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.radius);
      buf.writeInt(this.countdownTicks);
      buf.writeInt(this.waveTicks);
      buf.writeInt(this.fadeTicks);
      buf.writeInt(this.elapsedTicks);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
