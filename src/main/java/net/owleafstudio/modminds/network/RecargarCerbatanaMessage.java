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
import net.owleafstudio.modminds.procedures.RecargaProcedure;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class RecargarCerbatanaMessage {
   int type;
   int pressedms;

   public RecargarCerbatanaMessage(int type, int pressedms) {
      this.type = type;
      this.pressedms = pressedms;
   }

   public RecargarCerbatanaMessage(FriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      this.pressedms = buffer.readInt();
   }

   public static void buffer(RecargarCerbatanaMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.type);
      buffer.writeInt(message.pressedms);
   }

   public static void handler(RecargarCerbatanaMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> pressAction(context.getSender(), message.type, message.pressedms));
      context.setPacketHandled(true);
   }

   public static void pressAction(Player entity, int type, int pressedms) {
      Level world = entity.level();
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      if (world.hasChunkAt(entity.blockPosition()) && type == 0) {
         RecargaProcedure.execute(world, entity);
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         RecargarCerbatanaMessage.class, RecargarCerbatanaMessage::buffer, RecargarCerbatanaMessage::new, RecargarCerbatanaMessage::handler
      );
   }
}
