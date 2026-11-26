package net.ruleta.candado;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CodigoRespuestaPayload(boolean ok) implements CustomPacketPayload {
   public static final Type<CodigoRespuestaPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "codigo_respuesta"));
   public static final StreamCodec<FriendlyByteBuf, CodigoRespuestaPayload> CODEC = StreamCodec.ofMember(
      CodigoRespuestaPayload::encode, CodigoRespuestaPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CodigoRespuestaPayload m, FriendlyByteBuf b) {
      b.writeBoolean(m.ok);
   }

   public static CodigoRespuestaPayload decode(FriendlyByteBuf b) {
      return new CodigoRespuestaPayload(b.readBoolean());
   }
}
