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
import net.owleafstudio.modminds.procedures.RifleDePulsoBorgRecargarProcedure;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class RifleDePulsoBorgRecarharMessage {
   int type;
   int pressedms;

   public RifleDePulsoBorgRecarharMessage(int type, int pressedms) {
      this.type = type;
      this.pressedms = pressedms;
   }

   public RifleDePulsoBorgRecarharMessage(FriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      this.pressedms = buffer.readInt();
   }

   public static void buffer(RifleDePulsoBorgRecarharMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.type);
      buffer.writeInt(message.pressedms);
   }

   public static void handler(RifleDePulsoBorgRecarharMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
         RifleDePulsoBorgRecargarProcedure.execute(world, entity);
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(
         RifleDePulsoBorgRecarharMessage.class,
         RifleDePulsoBorgRecarharMessage::buffer,
         RifleDePulsoBorgRecarharMessage::new,
         RifleDePulsoBorgRecarharMessage::handler
      );
   }
}
