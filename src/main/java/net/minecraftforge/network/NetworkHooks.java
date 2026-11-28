package net.minecraftforge.network;

import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public final class NetworkHooks {
   private NetworkHooks() {
   }

   public static void openScreen(ServerPlayer player, MenuProvider provider) {
      player.openMenu(provider);
   }

   public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraDataWriter) {
      FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
      extraDataWriter.accept(buffer);
      byte[] data = new byte[buffer.readableBytes()];
      buffer.readBytes(data);
      player.openMenu(new NetworkHooks.ExtraDataMenuProvider(provider, data));
   }

   public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos) {
      openScreen(player, provider, (Consumer<FriendlyByteBuf>)(buffer -> buffer.writeBlockPos(pos)));
   }

   public static FriendlyByteBuf asBuffer(byte[] data) {
      return new FriendlyByteBuf(Unpooled.wrappedBuffer(data == null ? new byte[0] : data));
   }

   public record ExtraDataMenuProvider(MenuProvider delegate, byte[] data) implements ExtendedMenuProvider<byte[]> {
      public byte[] getScreenOpeningData(ServerPlayer player) {
         return this.data;
      }

      public Component getDisplayName() {
         return this.delegate.getDisplayName();
      }

      public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
         return this.delegate.createMenu(id, inventory, player);
      }
   }
}
