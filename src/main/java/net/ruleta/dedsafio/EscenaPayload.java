package net.ruleta.dedsafio;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record EscenaPayload(int tipo, float transcurrido, float a, float b, float c, float d, float e, float y, float r) implements CustomPacketPayload {
   public static final int PARAR = 0;
   public static final int INICIAR = 1;
   public static final int OJO = 2;
   public static final int OLA = 3;
   public static final int OLA_DENTRO = 4;
   public static final int OLA_FIN = 5;
   public static final int OCULTAR = 6;
   public static final int LIMPIAR_VISIBLES = 7;
   public static final int VISIBLE = 8;
   public static final Type<EscenaPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "dedsafio_escena"));
   public static final StreamCodec<RegistryFriendlyByteBuf, EscenaPayload> CODEC = StreamCodec.ofMember(EscenaPayload::encode, EscenaPayload::decode);

   public EscenaPayload(int tipo, float transcurrido) {
      this(tipo, transcurrido, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 180.0F);
   }

   public EscenaPayload(int tipo, float transcurrido, float a, float b, float c, float d, float e) {
      this(tipo, transcurrido, a, b, c, d, e, Float.NaN, 180.0F);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(EscenaPayload m, RegistryFriendlyByteBuf b) {
      b.writeByte(m.tipo);
      b.writeFloat(m.transcurrido);
      b.writeFloat(m.a);
      b.writeFloat(m.b);
      b.writeFloat(m.c);
      b.writeFloat(m.d);
      b.writeFloat(m.e);
      b.writeFloat(m.y);
      b.writeFloat(m.r);
   }

   public static EscenaPayload decode(RegistryFriendlyByteBuf b) {
      return new EscenaPayload(
         b.readByte(), b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat(), b.readFloat()
      );
   }
}
