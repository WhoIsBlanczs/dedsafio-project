package net.ruleta;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record ReviilPayload(String pieza, int valor, Component titulo, Component aviso) implements CustomPacketPayload {
   public static final Type<ReviilPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruleta", "reviil"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ReviilPayload> CODEC = StreamCodec.ofMember(ReviilPayload::encode, ReviilPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(ReviilPayload m, RegistryFriendlyByteBuf b) {
      b.writeUtf(m.pieza);
      b.writeInt(m.valor);
      ComponentSerialization.STREAM_CODEC.encode(b, m.titulo);
      ComponentSerialization.STREAM_CODEC.encode(b, m.aviso);
   }

   public static ReviilPayload decode(RegistryFriendlyByteBuf b) {
      return new ReviilPayload(
         b.readUtf(), b.readInt(), (Component)ComponentSerialization.STREAM_CODEC.decode(b), (Component)ComponentSerialization.STREAM_CODEC.decode(b)
      );
   }
}
