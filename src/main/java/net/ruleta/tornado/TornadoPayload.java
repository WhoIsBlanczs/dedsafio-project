package net.ruleta.tornado;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record TornadoPayload(boolean cielo, List<TornadoPayload.Torn> tornados, int[] atrapados) implements CustomPacketPayload {
   public static final Type<TornadoPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "tornado"));
   public static final StreamCodec<FriendlyByteBuf, TornadoPayload> CODEC = StreamCodec.ofMember(TornadoPayload::encode, TornadoPayload::decode);

   public TornadoPayload(boolean cielo, List<TornadoPayload.Torn> tornados) {
      this(cielo, tornados, new int[0]);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(TornadoPayload m, FriendlyByteBuf b) {
      b.writeBoolean(m.cielo);
      b.writeVarInt(m.tornados.size());

      for (TornadoPayload.Torn t : m.tornados) {
         b.writeVarInt(t.id());
         b.writeDouble(t.pos().x);
         b.writeDouble(t.pos().y);
         b.writeDouble(t.pos().z);
         b.writeFloat(t.radio());
         b.writeFloat(t.alto());
         b.writeFloat(t.vx());
         b.writeFloat(t.vz());
         b.writeVarInt(t.edad());
         b.writeUtf(t.dimension());
      }

      b.writeVarIntArray(m.atrapados == null ? new int[0] : m.atrapados);
   }

   public static TornadoPayload decode(FriendlyByteBuf b) {
      boolean cielo = b.readBoolean();
      int n = Math.min(b.readVarInt(), 32);
      List<TornadoPayload.Torn> l = new ArrayList<>(n);

      for (int i = 0; i < n; i++) {
         int id = b.readVarInt();
         Vec3 p = new Vec3(b.readDouble(), b.readDouble(), b.readDouble());
         float r = b.readFloat();
         float a = b.readFloat();
         float vx = b.readFloat();
         float vz = b.readFloat();
         int edad = b.readVarInt();
         l.add(new TornadoPayload.Torn(id, p, r, a, vx, vz, edad, b.readUtf()));
      }

      return new TornadoPayload(cielo, l, b.readVarIntArray());
   }

   public record Torn(int id, Vec3 pos, float radio, float alto, float vx, float vz, int edad, String dimension) {
   }
}
