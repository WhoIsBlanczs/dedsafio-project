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
import net.owleafstudio.modminds.procedures.AgregarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.AreaAttackHydraActivarProcedure;
import net.owleafstudio.modminds.procedures.ControlarADondeMireLaHydraProcedure;
import net.owleafstudio.modminds.procedures.FantasmaRojoSummonHydraProcedure;
import net.owleafstudio.modminds.procedures.HydraAbismoActivarProcedure;
import net.owleafstudio.modminds.procedures.HydraChorroActivarProcedure;
import net.owleafstudio.modminds.procedures.HydraMareaActivarProcedure;
import net.owleafstudio.modminds.procedures.HydraMordidaActivarProcedure;
import net.owleafstudio.modminds.procedures.NauseaHydraProcedure;
import net.owleafstudio.modminds.procedures.QuitarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.RayoActivarProcedure;
import net.owleafstudio.modminds.procedures.RayoEfectosEspecialesProcedure;
import net.owleafstudio.modminds.procedures.ScreamActivarProcedure;
import net.owleafstudio.modminds.procedures.SpawnearNereidasHydraProcedure;
import net.owleafstudio.modminds.procedures.WaterChargeActivarProcedure;
import net.owleafstudio.modminds.world.inventory.HydraPanelMenu;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class HydraPanelButtonMessage {
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public HydraPanelButtonMessage(FriendlyByteBuf buffer) {
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public HydraPanelButtonMessage(int buttonID, int x, int y, int z) {
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(HydraPanelButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(HydraPanelButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
      HashMap guistate = HydraPanelMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            ScreamActivarProcedure.execute(world);
         }

         if (buttonID == 1) {
            RayoEfectosEspecialesProcedure.execute(world);
         }

         if (buttonID == 2) {
            AgregarPantallaGrisProcedure.execute(world);
         }

         if (buttonID == 3) {
            RayoActivarProcedure.execute(world);
         }

         if (buttonID == 4) {
            QuitarPantallaGrisProcedure.execute(world);
         }

         if (buttonID == 5) {
            NauseaHydraProcedure.execute(world);
         }

         if (buttonID == 6) {
            FantasmaRojoSummonHydraProcedure.execute(world, x, y, z, entity, guistate);
         }

         if (buttonID == 7) {
            AreaAttackHydraActivarProcedure.execute(world, x, y, z);
         }

         if (buttonID == 8) {
            SpawnearNereidasHydraProcedure.execute(world, x, y, z, entity, guistate);
         }

         if (buttonID == 9) {
            WaterChargeActivarProcedure.execute(world);
         }

         if (buttonID == 10) {
            ControlarADondeMireLaHydraProcedure.execute(world, x, y, z, entity);
         }

         if (buttonID == 11) {
            HydraMareaActivarProcedure.execute(world);
         }

         if (buttonID == 12) {
            HydraMordidaActivarProcedure.execute(world);
         }

         if (buttonID == 13) {
            HydraAbismoActivarProcedure.execute(world);
         }

         if (buttonID == 14) {
            HydraChorroActivarProcedure.execute(world);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         HydraPanelButtonMessage.class, HydraPanelButtonMessage::buffer, HydraPanelButtonMessage::new, HydraPanelButtonMessage::handler
      );
   }
}
