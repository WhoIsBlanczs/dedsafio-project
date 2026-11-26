package ar.hombrosafio.ruleta.casino;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import ar.hombrosafio.ruleta.RuletaMod;

public record CasinoPedirPayload() implements CustomPacketPayload {
   public static final Type<CasinoPedirPayload> TYPE = new Type<>(RuletaMod.id("casino_pedir"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CasinoPedirPayload> CODEC = StreamCodec.unit(new CasinoPedirPayload());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
