package net.owleafstudio.modminds.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.owleafstudio.modminds.ModmindsMod;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class SubtitleMessage {
   private static final long FALLBACK_MILLIS = 3000L;
   private static volatile String currentText = "";
   private static volatile long currentUntilMillis;
   private final String text;
   private final int ticks;

   public SubtitleMessage(FriendlyByteBuf buffer) {
      this.text = buffer.readUtf(4096);
      this.ticks = buffer.readInt();
   }

   public SubtitleMessage(String text, int ticks) {
      this.text = text;
      this.ticks = ticks;
   }

   public static void buffer(SubtitleMessage message, FriendlyByteBuf buffer) {
      buffer.writeUtf(message.text);
      buffer.writeInt(message.ticks);
   }

   public static void handler(SubtitleMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
      NetworkEvent.Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (!context.getDirection().getReceptionSide().isServer()) {
            show(message.text, message.ticks);
         }
      });
      context.setPacketHandled(true);
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(SubtitleMessage.class, SubtitleMessage::buffer, SubtitleMessage::new, SubtitleMessage::handler);
   }

   private static void show(String text, int ticks) {
      long millis = ticks > 0 ? ticks * 50L : 3000L;
      currentText = text;
      currentUntilMillis = System.currentTimeMillis() + millis;
   }

   public static String visibleText() {
      return !currentText.isEmpty() && System.currentTimeMillis() <= currentUntilMillis ? currentText : null;
   }
}
