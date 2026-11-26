package net.ruleta.dedsafio;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record DulceAzarAccionPayload(int accion, String jugador, float a, float b, float c) implements CustomPacketPayload {
   public static final int IR = 0;
   public static final int VOLVER = 1;
   public static final int OLA = 2;
   public static final int OLA_FINAL = 3;
   public static final int OLA_PARAR = 4;
   public static final int ESCENA_INICIAR = 5;
   public static final int ESCENA_PARAR = 6;
   public static final int OCULTAR_TODOS = 7;
   public static final int HACER_VISIBLE = 8;
   public static final int DANO_FUERA = 9;
   public static final Type<DulceAzarAccionPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "dulceazar_accion"));
   public static final StreamCodec<RegistryFriendlyByteBuf, DulceAzarAccionPayload> CODEC = StreamCodec.ofMember(
      DulceAzarAccionPayload::encode, DulceAzarAccionPayload::decode
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(DulceAzarAccionPayload m, RegistryFriendlyByteBuf b) {
      b.writeByte(m.accion);
      b.writeUtf(m.jugador, 64);
      b.writeFloat(m.a);
      b.writeFloat(m.b);
      b.writeFloat(m.c);
   }

   public static DulceAzarAccionPayload decode(RegistryFriendlyByteBuf b) {
      return new DulceAzarAccionPayload(b.readByte(), b.readUtf(64), b.readFloat(), b.readFloat(), b.readFloat());
   }
}
