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
import net.owleafstudio.modminds.procedures.NauseaHydraProcedure;
import net.owleafstudio.modminds.procedures.OrbitaReviilActivarProcedure;
import net.owleafstudio.modminds.procedures.QuitarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.RayoDeVacioReviilActivarProcedure;
import net.owleafstudio.modminds.procedures.RayoEfectosEspecialesProcedure;
import net.owleafstudio.modminds.procedures.ReviilAgarreActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilAgujeroNegroActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilAplausoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilArrastrarManoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilAullidoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilAvalanchaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilBloquesAlzandoseActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilCampoRojoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilCirculosRayosActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilCoronaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilCroupierActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilDisparoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilEmbestidaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilEncogerseActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilEscenaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilExplosionActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilFuriaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilGiganteActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilGolpeSueloActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilLluviaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilPunoMarcadoActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilRuletaGiganteActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilRuletaLetalActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilSalActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilSoldadosActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilStuntProcedure;
import net.owleafstudio.modminds.procedures.ReviilTormentaActivarProcedure;
import net.owleafstudio.modminds.procedures.ReviilZarpazoActivarProcedure;
import net.owleafstudio.modminds.world.inventory.ReviilPanelMenu;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ReviilPanelButtonMessage {
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public ReviilPanelButtonMessage(FriendlyByteBuf buffer) {
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public ReviilPanelButtonMessage(int buttonID, int x, int y, int z) {
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(ReviilPanelButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(ReviilPanelButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
      HashMap guistate = ReviilPanelMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            RayoEfectosEspecialesProcedure.execute(world);
         }

         if (buttonID == 1) {
            AgregarPantallaGrisProcedure.execute(world);
         }

         if (buttonID == 2) {
            QuitarPantallaGrisProcedure.execute(world);
         }

         if (buttonID == 3) {
            NauseaHydraProcedure.execute(world);
         }

         if (buttonID == 4) {
            ReviilStuntProcedure.execute(world, x, y, z, entity);
         }

         if (buttonID == 5) {
            ReviilExplosionActivarProcedure.execute(world);
         }

         if (buttonID == 6) {
            ReviilDisparoActivarProcedure.execute(world);
         }

         if (buttonID == 7) {
            OrbitaReviilActivarProcedure.execute(world);
         }

         if (buttonID == 8) {
            RayoDeVacioReviilActivarProcedure.execute(world);
         }

         if (buttonID == 9) {
            ReviilAullidoActivarProcedure.execute(world);
         }

         if (buttonID == 10) {
            ReviilLluviaActivarProcedure.execute(world);
         }

         if (buttonID == 11) {
            ReviilRuletaLetalActivarProcedure.execute(world);
         }

         if (buttonID == 12) {
            ReviilCroupierActivarProcedure.execute(world);
         }

         if (buttonID == 13) {
            ReviilTormentaActivarProcedure.execute(world);
         }

         if (buttonID == 14) {
            ReviilCoronaActivarProcedure.execute(world);
         }

         if (buttonID == 15) {
            ReviilAgarreActivarProcedure.execute(world);
         }

         if (buttonID == 16) {
            ReviilEscenaActivarProcedure.execute(world);
         }

         if (buttonID == 17) {
            ReviilGolpeSueloActivarProcedure.execute(world);
         }

         if (buttonID == 18) {
            ReviilFuriaActivarProcedure.execute(world);
         }

         if (buttonID == 19) {
            ReviilGiganteActivarProcedure.execute(world);
         }

         if (buttonID == 20) {
            ReviilSoldadosActivarProcedure.execute(world);
         }

         if (buttonID == 21) {
            ReviilPunoMarcadoActivarProcedure.execute(world);
         }

         if (buttonID == 22) {
            ReviilRuletaGiganteActivarProcedure.execute(world);
         }

         if (buttonID == 23) {
            ReviilArrastrarManoActivarProcedure.execute(world);
         }

         if (buttonID == 24) {
            ReviilAplausoActivarProcedure.execute(world);
         }

         if (buttonID == 25) {
            ReviilSalActivarProcedure.execute(world);
         }

         if (buttonID == 26) {
            ReviilAgujeroNegroActivarProcedure.execute(world);
         }

         if (buttonID == 27) {
            ReviilCirculosRayosActivarProcedure.execute(world);
         }

         if (buttonID == 28) {
            ReviilBloquesAlzandoseActivarProcedure.execute(world);
         }

         if (buttonID == 29) {
            ReviilAvalanchaActivarProcedure.execute(world);
         }

         if (buttonID == 30) {
            ReviilCampoRojoActivarProcedure.execute(world);
         }

         if (buttonID == 31) {
            ReviilEmbestidaActivarProcedure.execute(world);
         }

         if (buttonID == 32) {
            ReviilEncogerseActivarProcedure.execute(world);
         }

         if (buttonID == 33) {
            ReviilZarpazoActivarProcedure.execute(world);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         ReviilPanelButtonMessage.class, ReviilPanelButtonMessage::buffer, ReviilPanelButtonMessage::new, ReviilPanelButtonMessage::handler
      );
   }
}
