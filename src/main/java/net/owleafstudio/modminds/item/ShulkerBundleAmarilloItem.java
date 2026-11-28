package net.owleafstudio.modminds.item;

import net.minecraft.world.InteractionResult;

import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import net.owleafstudio.modminds.item.inventory.ShulkerBundleAmarilloInventoryCapability;
import net.owleafstudio.modminds.world.inventory.InventarioMenu;

public class ShulkerBundleAmarilloItem extends Item {
   public ShulkerBundleAmarilloItem() {
      super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
   }

   public InteractionResult use(Level world, Player entity, InteractionHand hand) {
      InteractionResult ar = super.use(world, entity, hand);
      if (entity instanceof ServerPlayer serverPlayer) {
         NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
            public Component getDisplayName() {
               return Component.literal("Shulker Bundle Amarilla");
            }

            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
               FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
               packetBuffer.writeBlockPos(entity.blockPosition());
               packetBuffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
               return new InventarioMenu(id, inventory, packetBuffer);
            }
         }, (Consumer<FriendlyByteBuf>)(buf -> {
            buf.writeBlockPos(entity.blockPosition());
            buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
         }));
      }

      return ar;
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag compound) {
      return new ShulkerBundleAmarilloInventoryCapability();
   }
}
