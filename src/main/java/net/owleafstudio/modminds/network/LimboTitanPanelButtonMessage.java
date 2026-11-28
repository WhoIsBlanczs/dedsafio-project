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
import net.owleafstudio.modminds.procedures.ControlarADondeMireLaHydraProcedure;
import net.owleafstudio.modminds.procedures.FantasmaRojoSummonHydraProcedure;
import net.owleafstudio.modminds.procedures.NauseaHydraProcedure;
import net.owleafstudio.modminds.procedures.PonerRedScreenProcedure;
import net.owleafstudio.modminds.procedures.PunchActivarProcedure;
import net.owleafstudio.modminds.procedures.QuitarPantallaGrisProcedure;
import net.owleafstudio.modminds.procedures.RayoEfectosEspecialesProcedure;
import net.owleafstudio.modminds.procedures.RollActivarProcedure;
import net.owleafstudio.modminds.procedures.SummonLimboPiglinsProcedure;
import net.owleafstudio.modminds.procedures.TerraPunchActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanBolaFuegoActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanCorralActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanMuroActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanQuiebraActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanSaltoTripleActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanSierraActivarProcedure;
import net.owleafstudio.modminds.procedures.TitanSueloActivarProcedure;
import net.owleafstudio.modminds.procedures.UltimateJumpActivadoProcedure;
import net.owleafstudio.modminds.world.inventory.LimboTitanPanelMenu;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class LimboTitanPanelButtonMessage {
   private final int buttonID;
   private final int x;
   private final int y;
   private final int z;

   public LimboTitanPanelButtonMessage(FriendlyByteBuf buffer) {
      this.buttonID = buffer.readInt();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
   }

   public LimboTitanPanelButtonMessage(int buttonID, int x, int y, int z) {
      this.buttonID = buttonID;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static void buffer(LimboTitanPanelButtonMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.buttonID);
      buffer.writeInt(message.x);
      buffer.writeInt(message.y);
      buffer.writeInt(message.z);
   }

   public static void handler(LimboTitanPanelButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
      HashMap guistate = LimboTitanPanelMenu.guistate;
      if (world.hasChunkAt(new BlockPos(x, y, z))) {
         if (buttonID == 0) {
            RayoEfectosEspecialesProcedure.execute(world);
         }

         if (buttonID == 1) {
            PonerRedScreenProcedure.execute(world, x, y, z);
         }

         if (buttonID == 2) {
            NauseaHydraProcedure.execute(world);
         }

         if (buttonID == 3) {
            QuitarPantallaGrisProcedure.execute(world);
         }

         if (buttonID == 4) {
            TerraPunchActivarProcedure.execute(world);
         }

         if (buttonID == 5) {
            UltimateJumpActivadoProcedure.execute(world);
         }

         if (buttonID == 6) {
            RollActivarProcedure.execute(world);
         }

         if (buttonID == 7) {
            PunchActivarProcedure.execute(world);
         }

         if (buttonID == 8) {
            FantasmaRojoSummonHydraProcedure.execute(world, x, y, z, entity, guistate);
         }

         if (buttonID == 9) {
            SummonLimboPiglinsProcedure.execute(world, x, y, z, entity, guistate);
         }

         if (buttonID == 10) {
            ControlarADondeMireLaHydraProcedure.execute(world, x, y, z, entity);
         }

         if (buttonID == 11) {
            TitanQuiebraActivarProcedure.execute(world);
         }

         if (buttonID == 12) {
            TitanCorralActivarProcedure.execute(world);
         }

         if (buttonID == 13) {
            TitanMuroActivarProcedure.execute(world);
         }

         if (buttonID == 14) {
            TitanSueloActivarProcedure.execute(world);
         }

         if (buttonID == 15) {
            TitanBolaFuegoActivarProcedure.execute(world);
         }

         if (buttonID == 16) {
            TitanSierraActivarProcedure.execute(world);
         }

         if (buttonID == 17) {
            TitanSaltoTripleActivarProcedure.execute(world);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         LimboTitanPanelButtonMessage.class, LimboTitanPanelButtonMessage::buffer, LimboTitanPanelButtonMessage::new, LimboTitanPanelButtonMessage::handler
      );
   }
}
