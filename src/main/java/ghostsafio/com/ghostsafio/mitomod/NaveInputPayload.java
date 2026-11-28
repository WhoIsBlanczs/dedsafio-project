package com.ghostsafio.mitomod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public record NaveInputPayload(int flags) implements CustomPacketPayload {
   public static final int UP = 1;
   public static final int BOOST = 2;
   public static final int BACK = 4;
   public static final int FORWARD = 8;
   public static final int LEFT = 16;
   public static final int RIGHT = 32;
   public static final Type<NaveInputPayload> TYPE = new Type(Ghostsafio.id("nave_input"));
   public static final StreamCodec<RegistryFriendlyByteBuf, NaveInputPayload> CODEC = StreamCodec.ofMember(NaveInputPayload::write, NaveInputPayload::new);

   public NaveInputPayload(RegistryFriendlyByteBuf buf) {
      this(buf.readVarInt());
   }

   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeVarInt(this.flags);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
