package net.owleafstudio.modminds.network;

import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossPanels;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BossPanelButtonMessage {
   private final int boss;
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public BossPanelButtonMessage(FriendlyByteBuf buffer) {
      this.boss = buffer.readInt();
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public BossPanelButtonMessage(int boss, int buttonID, int x, int y, int z) {
      this.boss = boss;
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(BossPanelButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.boss);
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(BossPanelButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> handleButtonAction(context.getSender(), message.boss, message.buttonID, message.x, message.y, message.z));
      context.setPacketHandled(true);
   }

   public static void handleButtonAction(Player entity, int boss, int buttonID, int x, int y, int z) {
      if (entity != null) {
         Level world = entity.level();
         if (world.hasChunkAt(new BlockPos(x, y, z))) {
            BossPanels.activate(world, boss, buttonID, x, y, z, entity);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(BossPanelButtonMessage.class, BossPanelButtonMessage::buffer, BossPanelButtonMessage::new, BossPanelButtonMessage::handler);
   }
}
