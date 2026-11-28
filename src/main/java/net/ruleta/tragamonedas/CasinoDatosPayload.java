package net.ruleta.tragamonedas;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CasinoDatosPayload(String json) implements CustomPacketPayload {
   public static final Type<CasinoDatosPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "casino_datos"));
   public static final StreamCodec<FriendlyByteBuf, CasinoDatosPayload> CODEC = StreamCodec.ofMember(CasinoDatosPayload::encode, CasinoDatosPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CasinoDatosPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.json, 262144);
   }

   public static CasinoDatosPayload decode(FriendlyByteBuf b) {
      return new CasinoDatosPayload(b.readUtf(262144));
   }
}
