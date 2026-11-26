package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record LanzarPayload(
   int color,
   int tipo,
   boolean animacion,
   boolean reviil,
   String titulo,
   String mensaje,
   String tiempo,
   String cambios,
   boolean cielo,
   String para,
   int sancion,
   String objetivo,
   String tiempoBan,
   boolean phora,
   boolean sinRuleta
) implements CustomPacketPayload {
   public static final Type<LanzarPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "lanzar"));
   public static final StreamCodec<FriendlyByteBuf, LanzarPayload> CODEC = StreamCodec.ofMember(LanzarPayload::encode, LanzarPayload::decode);

   public LanzarPayload(int color, int tipo, boolean animacion, boolean reviil, String titulo, String mensaje, String tiempo, String cambios) {
      this(color, tipo, animacion, reviil, titulo, mensaje, tiempo, cambios, false, "", 0, "", "", false, false);
   }

   public LanzarPayload(
      int color,
      int tipo,
      boolean animacion,
      boolean reviil,
      String titulo,
      String mensaje,
      String tiempo,
      String cambios,
      boolean cielo,
      String para,
      int sancion,
      String objetivo,
      String tiempoBan,
      boolean phora
   ) {
      this(color, tipo, animacion, reviil, titulo, mensaje, tiempo, cambios, cielo, para, sancion, objetivo, tiempoBan, phora, false);
   }

   public LanzarPayload(
      int color,
      int tipo,
      boolean animacion,
      boolean reviil,
      String titulo,
      String mensaje,
      String tiempo,
      String cambios,
      boolean cielo,
      String para,
      int sancion,
      String objetivo,
      String tiempoBan
   ) {
      this(color, tipo, animacion, reviil, titulo, mensaje, tiempo, cambios, cielo, para, sancion, objetivo, tiempoBan, false, false);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(LanzarPayload m, FriendlyByteBuf b) {
      b.writeVarInt(m.color);
      b.writeVarInt(m.tipo);
      b.writeBoolean(m.animacion);
      b.writeBoolean(m.reviil);
      b.writeUtf(m.titulo == null ? "" : m.titulo, 256);
      b.writeUtf(m.mensaje == null ? "" : m.mensaje, 2048);
      b.writeUtf(m.tiempo == null ? "" : m.tiempo, 32);
      b.writeUtf(m.cambios == null ? "" : m.cambios, 1024);
      b.writeBoolean(m.cielo);
      b.writeUtf(m.para == null ? "" : m.para, 64);
      b.writeVarInt(m.sancion);
      b.writeUtf(m.objetivo == null ? "" : m.objetivo, 64);
      b.writeUtf(m.tiempoBan == null ? "" : m.tiempoBan, 32);
      b.writeBoolean(m.phora);
      b.writeBoolean(m.sinRuleta);
   }

   public static LanzarPayload decode(FriendlyByteBuf b) {
      return new LanzarPayload(
         b.readVarInt(),
         b.readVarInt(),
         b.readBoolean(),
         b.readBoolean(),
         b.readUtf(256),
         b.readUtf(2048),
         b.readUtf(32),
         b.readUtf(1024),
         b.readBoolean(),
         b.readUtf(64),
         b.readVarInt(),
         b.readUtf(64),
         b.readUtf(32),
         b.readBoolean(),
         b.readBoolean()
      );
   }
}
