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
import net.owleafstudio.modminds.procedures.EspacioHydraAlSerPresionadaProcedure;
import net.owleafstudio.modminds.procedures.EspacioHydraAlSerSoltadaProcedure;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD
)
public class EspacioHydraMessage {
   int type;
   int pressedms;

   public EspacioHydraMessage(int type, int pressedms) {
      this.type = type;
      this.pressedms = pressedms;
   }

   public EspacioHydraMessage(FriendlyByteBuf buffer) {
      this.type = buffer.readInt();
      this.pressedms = buffer.readInt();
   }

   public static void buffer(EspacioHydraMessage message, FriendlyByteBuf buffer) {
      buffer.writeInt(message.type);
      buffer.writeInt(message.pressedms);
   }

   public static void handler(EspacioHydraMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
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
            EspacioHydraAlSerPresionadaProcedure.execute(entity);
         }

         if (type == 1) {
            EspacioHydraAlSerSoltadaProcedure.execute(entity);
         }
      }
   }

   @SubscribeEvent
   public static void registerMessage(FMLCommonSetupEvent event) {
      ModmindsMod.addNetworkMessage(EspacioHydraMessage.class, EspacioHydraMessage::buffer, EspacioHydraMessage::new, EspacioHydraMessage::handler);
   }
}
