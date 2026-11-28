package net.ruleta.cosmeticos;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record EstadoPayload(UUID id, String aura) implements CustomPacketPayload {
   public static final Type<EstadoPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "estado"));
   public static final StreamCodec<FriendlyByteBuf, EstadoPayload> CODEC = StreamCodec.ofMember(EstadoPayload::encode, EstadoPayload::decode);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void encode(EstadoPayload m, FriendlyByteBuf b) {
      b.writeUUID(m.id);
      b.writeUtf(m.aura, 32);
   }

   public static EstadoPayload decode(FriendlyByteBuf b) {
      return new EstadoPayload(b.readUUID(), b.readUtf(32));
   }

   public boolean vacio() {
      return this.aura.isEmpty();
   }
}
