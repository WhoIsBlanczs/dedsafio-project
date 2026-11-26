package net.ruleta.estatua;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record PedestalDatosPayload(String json) implements CustomPacketPayload {
   public static final Type<PedestalDatosPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "pedestal_datos"));
   public static final StreamCodec<FriendlyByteBuf, PedestalDatosPayload> CODEC = StreamCodec.ofMember(
      PedestalDatosPayload::encode, PedestalDatosPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(PedestalDatosPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.json, 262144);
   }

   public static PedestalDatosPayload decode(FriendlyByteBuf b) {
      return new PedestalDatosPayload(b.readUtf(262144));
   }
}
