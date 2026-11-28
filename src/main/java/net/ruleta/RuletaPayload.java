package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record RuletaPayload(int result, float startAngle, int spins, float offset, String who, boolean mini) implements CustomPacketPayload {
   public static final Type<RuletaPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "ruletafanproject"));
   public static final StreamCodec<FriendlyByteBuf, RuletaPayload> CODEC = StreamCodec.ofMember(RuletaPayload::encode, RuletaPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(RuletaPayload m, FriendlyByteBuf b) {
      b.writeVarInt(m.result);
      b.writeFloat(m.startAngle);
      b.writeVarInt(m.spins);
      b.writeFloat(m.offset);
      b.writeUtf(m.who);
      b.writeBoolean(m.mini);
   }

   public static RuletaPayload decode(FriendlyByteBuf b) {
      return new RuletaPayload(b.readVarInt(), b.readFloat(), b.readVarInt(), b.readFloat(), b.readUtf(), b.readBoolean());
   }
}
