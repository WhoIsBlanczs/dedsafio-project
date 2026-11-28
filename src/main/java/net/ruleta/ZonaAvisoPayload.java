package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record ZonaAvisoPayload(String texto, int milisegundos) implements CustomPacketPayload {
   public static final Type<ZonaAvisoPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "zona_aviso"));
   public static final StreamCodec<FriendlyByteBuf, ZonaAvisoPayload> CODEC = StreamCodec.ofMember(ZonaAvisoPayload::encode, ZonaAvisoPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(ZonaAvisoPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.texto, 200);
      b.writeVarInt(m.milisegundos);
   }

   public static ZonaAvisoPayload decode(FriendlyByteBuf b) {
      return new ZonaAvisoPayload(b.readUtf(200), b.readVarInt());
   }
}
