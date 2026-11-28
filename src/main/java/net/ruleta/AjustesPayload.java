package net.ruleta;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record AjustesPayload(Map<String, Double> valores) implements CustomPacketPayload {
   public static final Type<AjustesPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "ajustes"));
   public static final StreamCodec<FriendlyByteBuf, AjustesPayload> CODEC = StreamCodec.ofMember(AjustesPayload::encode, AjustesPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(AjustesPayload m, FriendlyByteBuf b) {
      b.writeVarInt(m.valores.size());

      for (Entry<String, Double> e : m.valores.entrySet()) {
         b.writeUtf(e.getKey(), 64);
         b.writeDouble(e.getValue());
      }
   }

   public static AjustesPayload decode(FriendlyByteBuf b) {
      int n = Math.min(b.readVarInt(), 512);
      Map<String, Double> r = new LinkedHashMap<>();

      for (int i = 0; i < n; i++) {
         r.put(b.readUtf(64), b.readDouble());
      }

      return new AjustesPayload(r);
   }
}
