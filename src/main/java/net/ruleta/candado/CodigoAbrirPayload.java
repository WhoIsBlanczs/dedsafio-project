package net.ruleta.candado;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record CodigoAbrirPayload(int modo, BlockPos pos) implements CustomPacketPayload {
   public static final Type<CodigoAbrirPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "codigo_abrir"));
   public static final StreamCodec<FriendlyByteBuf, CodigoAbrirPayload> CODEC = StreamCodec.ofMember(CodigoAbrirPayload::encode, CodigoAbrirPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(CodigoAbrirPayload m, FriendlyByteBuf b) {
      b.writeVarInt(m.modo);
      b.writeBlockPos(m.pos);
   }

   public static CodigoAbrirPayload decode(FriendlyByteBuf b) {
      return new CodigoAbrirPayload(b.readVarInt(), b.readBlockPos());
   }
}
