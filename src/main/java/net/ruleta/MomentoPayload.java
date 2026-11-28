package net.ruleta;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record MomentoPayload(String id, boolean activar, int segundos, int segundos2, String extra) implements CustomPacketPayload {
   public static final Type<MomentoPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "momento"));
   public static final StreamCodec<FriendlyByteBuf, MomentoPayload> CODEC = StreamCodec.ofMember(MomentoPayload::encode, MomentoPayload::decode);

   public MomentoPayload(String id, boolean activar, int segundos) {
      this(id, activar, segundos, 0, "");
   }

   public MomentoPayload(String id, boolean activar, int segundos, int segundos2) {
      this(id, activar, segundos, segundos2, "");
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(MomentoPayload m, FriendlyByteBuf b) {
      b.writeUtf(m.id, 64);
      b.writeBoolean(m.activar);
      b.writeVarInt(m.segundos);
      b.writeVarInt(m.segundos2);
      b.writeUtf(m.extra == null ? "" : m.extra, 4096);
   }

   public static MomentoPayload decode(FriendlyByteBuf b) {
      return new MomentoPayload(b.readUtf(64), b.readBoolean(), b.readVarInt(), b.readVarInt(), b.readUtf(4096));
   }
}
