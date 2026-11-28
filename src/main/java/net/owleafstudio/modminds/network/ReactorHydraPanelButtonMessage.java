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
import net.owleafstudio.modminds.procedures.ReactorHydraBoneSpiderProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraCreeperCakeProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraEsqueletosProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraFantasmaAmarilloProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraFantasmaNegroProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraFantasmasProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraNereidasProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraSapoProcedure;
import net.owleafstudio.modminds.procedures.ReactorHydraZombieProcedure;
import net.owleafstudio.modminds.world.inventory.ReactorHydraPanelMenu;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ReactorHydraPanelButtonMessage {
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public ReactorHydraPanelButtonMessage(FriendlyByteBuf buffer) {
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public ReactorHydraPanelButtonMessage(int buttonID, int x, int y, int z) {
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(ReactorHydraPanelButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(ReactorHydraPanelButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
      HashMap guistate = ReactorHydraPanelMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            ReactorHydraFantasmasProcedure.execute(world, x, y, z);
         }

         if (buttonID == 1) {
            ReactorHydraBoneSpiderProcedure.execute(world, x, y, z);
         }

         if (buttonID == 2) {
            ReactorHydraCreeperCakeProcedure.execute(world, x, y, z);
         }

         if (buttonID == 3) {
            ReactorHydraNereidasProcedure.execute(world, x, y, z);
         }

         if (buttonID == 4) {
            ReactorHydraEsqueletosProcedure.execute(world, x, y, z);
         }

         if (buttonID == 5) {
            ReactorHydraZombieProcedure.execute(world, x, y, z);
         }

         if (buttonID == 6) {
            ReactorHydraSapoProcedure.execute(world, x, y, z);
         }

         if (buttonID == 7) {
            ReactorHydraFantasmaAmarilloProcedure.execute(world, x, y, z);
         }

         if (buttonID == 8) {
            ReactorHydraFantasmaNegroProcedure.execute(world, x, y, z);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         ReactorHydraPanelButtonMessage.class,
         ReactorHydraPanelButtonMessage::buffer,
         ReactorHydraPanelButtonMessage::new,
         ReactorHydraPanelButtonMessage::handler
      );
   }
}
