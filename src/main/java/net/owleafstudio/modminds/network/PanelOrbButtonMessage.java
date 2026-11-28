package net.owleafstudio.modminds.network;

import java.util.HashMap;
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
import net.owleafstudio.modminds.procedures.ParedXGuiProcedure;
import net.owleafstudio.modminds.procedures.ParedZGuiProcedure;
import net.owleafstudio.modminds.procedures.QuitarParedesGuiProcedure;
import net.owleafstudio.modminds.procedures.SummonOrbsProcedure;
import net.owleafstudio.modminds.world.inventory.PanelOrbMenu;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class PanelOrbButtonMessage {
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public PanelOrbButtonMessage(FriendlyByteBuf buffer) {
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public PanelOrbButtonMessage(int buttonID, int x, int y, int z) {
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(PanelOrbButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(PanelOrbButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         Player entity = context.getSender();
         int buttonID = message.buttonID;
         int x = message.x;
         int y = message.y;
         int z = message.z;
         handleButtonAction(entity, buttonID, x, y, z);
      });
      context.setPacketHandled(true);
   }

   public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
      Level world = entity.level();
      HashMap guistate = PanelOrbMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            ParedXGuiProcedure.execute(world, x, y, z);
         }

         if (buttonID == 1) {
            ParedZGuiProcedure.execute(world, x, y, z);
         }

         if (buttonID == 2) {
            QuitarParedesGuiProcedure.execute(world, x, y, z);
         }

         if (buttonID == 3) {
            SummonOrbsProcedure.execute(world, x, y, z, entity, guistate);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(PanelOrbButtonMessage.class, PanelOrbButtonMessage::buffer, PanelOrbButtonMessage::new, PanelOrbButtonMessage::handler);
   }
}
