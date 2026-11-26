package net.ruleta.nuclear.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record TotemActivatePacket(ItemStack stack) implements CustomPacketPayload {
   public static final Type<TotemActivatePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("nuclearcreeper", "totem_activate"));
   public static final StreamCodec<RegistryFriendlyByteBuf, TotemActivatePacket> CODEC = ItemStack.OPTIONAL_STREAM_CODEC
      .map(TotemActivatePacket::new, TotemActivatePacket::stack);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
