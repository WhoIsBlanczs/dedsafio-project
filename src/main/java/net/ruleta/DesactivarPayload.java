package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record DesactivarPayload(String id) implements CustomPacketPayload {
   public static final Type<DesactivarPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "desactivar"));
   public static final StreamCodec<FriendlyByteBuf, DesactivarPayload> CODEC = StreamCodec.ofMember(DesactivarPayload::encode, DesactivarPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(DesactivarPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.id, 128);
   }

   public static DesactivarPayload decode(FriendlyByteBuf b) {
      return new DesactivarPayload(b.readUtf(128));
   }
}
