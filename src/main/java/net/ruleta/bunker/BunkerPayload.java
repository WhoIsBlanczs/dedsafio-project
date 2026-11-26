package net.ruleta.bunker;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public record BunkerPayload(List<Bunkers.Bunker> lista, boolean todos) implements CustomPacketPayload {
   public static final Type<BunkerPayload> TYPE = new Type(Identifier.fromNamespaceAndPath("ruleta", "bunker"));
   public static final StreamCodec<FriendlyByteBuf, BunkerPayload> CODEC = StreamCodec.ofMember(BunkerPayload::encode, BunkerPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(BunkerPayload m, FriendlyByteBuf b) {
      b.writeBoolean(m.todos);
      b.writeVarInt(m.lista.size());

      for (Bunkers.Bunker k : m.lista) {
         b.writeUtf(k.nombre(), 64);
         b.writeUtf(k.dimension(), 256);
         b.writeInt(k.ex());
         b.writeInt(k.ey());
         b.writeInt(k.ez());
      }
   }

   public static BunkerPayload decode(FriendlyByteBuf b) {
      boolean todos = b.readBoolean();
      int n = b.readVarInt();
      List<Bunkers.Bunker> l = new ArrayList<>();

      for (int i = 0; i < n; i++) {
         l.add(new Bunkers.Bunker(b.readUtf(64), b.readUtf(256), b.readInt(), b.readInt(), b.readInt()));
      }

      return new BunkerPayload(l, todos);
   }
}
