package net.owleafstudio.modminds;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.owleafstudio.modminds.compat.ForgeEventBus;
import net.owleafstudio.modminds.compat.GameEventPumps;
import net.owleafstudio.modminds.compat.NetworkCompat;
import net.owleafstudio.modminds.compat.SubscriberBootstrap;
import net.owleafstudio.modminds.compat.WorkQueue;
import net.owleafstudio.modminds.init.ModmindsModSpawns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("modminds")
public class ModmindsMod implements ModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("modminds");
   public static final String MODID = "modminds";
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
      Identifier.fromNamespaceAndPath("modminds", "modminds"), () -> "1", "1"::equals, "1"::equals
   );
   private static final String[] INIT_CLASSES = new String[]{
      "net.owleafstudio.modminds.init.ModmindsModSounds",
      "net.owleafstudio.modminds.init.ModmindsModFluidTypes",
      "net.owleafstudio.modminds.init.ModmindsModFluids",
      "net.owleafstudio.modminds.init.ModmindsModBlocks",
      "net.owleafstudio.modminds.init.ModmindsModItems",
      "net.owleafstudio.modminds.init.ModmindsModEntities",
      "net.owleafstudio.modminds.init.ModmindsModMobEffects",
      "net.owleafstudio.modminds.init.ModmindsModPotions",
      "net.owleafstudio.modminds.init.ModmindsModParticleTypes",
      "net.owleafstudio.modminds.init.ModmindsModMenus",
      "net.owleafstudio.modminds.init.ModmindsModTabs",
      "net.owleafstudio.modminds.init.ModmindsModGameRules"
   };
   private static int messageID = 0;

   public void onInitialize() {
      for (String className : INIT_CLASSES) {
         try {
            Class.forName(className);
         } catch (Throwable var6) {
            LOGGER.error("Init class {} could not be loaded", className, var6);
         }
      }

      DeferredRegister.flushAll();
      LOGGER.info("ModMinds registered {} deferred entries", DeferredRegister.registeredCount());
      ModmindsModSpawns.register();
      SubscriberBootstrap.registerAll(ForgeEventBus.MOD, ForgeEventBus.FORGE);
      ForgeEventBus.MOD.post(new FMLCommonSetupEvent());
      ForgeEventBus.MOD.post(new EntityAttributeCreationEvent());
      ForgeEventBus.MOD.post(new RegisterEvent(ForgeRegistries.POI_TYPES.vanilla().key().location()));
      NetworkCompat.installCommon();
      GameEventPumps.installServer();
      WorkQueue.setBooting(false);
   }

   public static <T> void addNetworkMessage(
      Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer
   ) {
      PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
      messageID++;
   }

   public static void queueServerWork(int tick, Runnable action) {
      WorkQueue.queue(tick, action);
   }
}
