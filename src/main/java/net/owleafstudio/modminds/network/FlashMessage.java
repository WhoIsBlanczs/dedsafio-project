package net.owleafstudio.modminds.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.client.screens.Desorientacion;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class FlashMessage {
   private final int whiteTicks;
   private final int ringDelayTicks;
   private final int ringTicks;

   public FlashMessage(FriendlyByteBuf buffer) {
      this.whiteTicks = buffer.readInt();
      this.ringDelayTicks = buffer.readInt();
      this.ringTicks = buffer.readInt();
   }

   public FlashMessage(int whiteTicks, int ringDelayTicks, int ringTicks) {
      this.whiteTicks = whiteTicks;
      this.ringDelayTicks = ringDelayTicks;
      this.ringTicks = ringTicks;
   }

   public static void buffer(FlashMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.whiteTicks);
      buffer.writeInt(message.ringDelayTicks);
      buffer.writeInt(message.ringTicks);
   }

   public static void handler(FlashMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (!context.getDirection().getReceptionSide().isServer()) {
            Desorientacion.golpeDeLuz(message.whiteTicks, message.ringDelayTicks, message.ringTicks);
         }
      });
      context.setPacketHandled(true);
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(FlashMessage.class, FlashMessage::buffer, FlashMessage::new, FlashMessage::handler);
   }
}
