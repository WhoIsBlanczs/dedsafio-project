package net.owleafstudio.modminds.network;

import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.procedures.BombaNegraEspacioAlSerPresionadaProcedure;
import net.owleafstudio.modminds.procedures.BombaNegraEspacioAlSerSoltadaProcedure;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BombaNegraEspacioMessage {
   int type;
   int pressedms;

   public BombaNegraEspacioMessage(int type, int pressedms) {
      this.type = type;
      this.pressedms = pressedms;
   }

   public BombaNegraEspacioMessage(FriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      this.pressedms = buffer.readInt();
   }

   public static void buffer(BombaNegraEspacioMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.type);
      buffer.writeInt(message.pressedms);
   }

   public static void handler(BombaNegraEspacioMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> pressAction(context.getSender(), message.type, message.pressedms));
      context.setPacketHandled(true);
   }

   public static void pressAction(Player entity, int type, int pressedms) {
      Level world = entity.level();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      if (world.hasChunkAt(entity.blockPosition())) {
         if (type == 0) {
            BombaNegraEspacioAlSerPresionadaProcedure.execute(entity);
         }

         if (type == 1) {
            BombaNegraEspacioAlSerSoltadaProcedure.execute(entity);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         BombaNegraEspacioMessage.class, BombaNegraEspacioMessage::buffer, BombaNegraEspacioMessage::new, BombaNegraEspacioMessage::handler
      );
   }
}
