package net.ruleta.agujero;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record AgujeroPayload(boolean activo, List<AgujeroPayload.Agujero> agujeros) implements CustomPacketPayload {
   public static final Type<AgujeroPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "agujero"));
   public static final StreamCodec<FriendlyByteBuf, AgujeroPayload> CODEC = StreamCodec.ofMember(AgujeroPayload::encode, AgujeroPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(AgujeroPayload m, FriendlyByteBuf b) {
      b.writeBoolean(m.activo);
      b.writeVarInt(m.agujeros.size());

      for (AgujeroPayload.Agujero a : m.agujeros) {
         b.writeDouble(a.pos().x);
         b.writeDouble(a.pos().y);
         b.writeDouble(a.pos().z);
         b.writeFloat(a.radio());
         b.writeUtf(a.dimension());
      }
   }

   public static AgujeroPayload decode(FriendlyByteBuf b) {
      boolean activo = b.readBoolean();
      int n = Math.min(b.readVarInt(), 64);
      List<AgujeroPayload.Agujero> l = new ArrayList<>(n);

      for (int i = 0; i < n; i++) {
         Vec3 p = new Vec3(b.readDouble(), b.readDouble(), b.readDouble());
         float r = b.readFloat();
         String d = b.readUtf();
         l.add(new AgujeroPayload.Agujero(p, r, d));
      }

      return new AgujeroPayload(activo, l);
   }

   public record Agujero(Vec3 pos, float radio, String dimension) {
   }
}
