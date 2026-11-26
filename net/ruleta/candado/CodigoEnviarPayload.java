package net.ruleta.candado;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CodigoEnviarPayload(int modo, BlockPos pos, String codigo) implements CustomPacketPayload {
   public static final Type<CodigoEnviarPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "codigo_enviar"));
   public static final StreamCodec<FriendlyByteBuf, CodigoEnviarPayload> CODEC = StreamCodec.ofMember(CodigoEnviarPayload::encode, CodigoEnviarPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CodigoEnviarPayload m, FriendlyByteBuf b) {
      b.writeVarInt(m.modo);
      b.writeBlockPos(m.pos);
      b.writeUtf(m.codigo, 16);
   }

   public static CodigoEnviarPayload decode(FriendlyByteBuf b) {
      return new CodigoEnviarPayload(b.readVarInt(), b.readBlockPos(), b.readUtf(16));
   }
}
