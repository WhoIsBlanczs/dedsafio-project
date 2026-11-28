package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OndaPayload(double x, double y, double z, float radio) implements CustomPacketPayload {
   public static final Type<OndaPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "onda"));
   public static final StreamCodec<FriendlyByteBuf, OndaPayload> CODEC = StreamCodec.ofMember(OndaPayload::encode, OndaPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(OndaPayload m, FriendlyByteBuf b) {
      b.writeDouble(m.x);
      b.writeDouble(m.y);
      b.writeDouble(m.z);
      b.writeFloat(m.radio);
   }

   public static OndaPayload decode(FriendlyByteBuf b) {
      return new OndaPayload(b.readDouble(), b.readDouble(), b.readDouble(), b.readFloat());
   }
}
