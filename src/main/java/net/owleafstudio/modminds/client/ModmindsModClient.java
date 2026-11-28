package net.owleafstudio.modminds.client;

import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.ClientEventPumps;
import net.owleafstudio.modminds.compat.ClientHooks;
import net.owleafstudio.modminds.compat.ClientTickPump;
import net.owleafstudio.modminds.compat.ForgeEventBus;
import net.owleafstudio.modminds.compat.NetworkCompat;
import net.owleafstudio.modminds.compat.WorkQueue;

public class ModmindsModClient implements ClientModInitializer {
   private static final String[] CLIENT_INIT_CLASSES = new String[]{
      "net.owleafstudio.modminds.init.ModmindsModModels",
      "net.owleafstudio.modminds.init.ModmindsModEntityRenderers",
      "net.owleafstudio.modminds.init.ModmindsModParticles",
      "net.owleafstudio.modminds.init.ModmindsModKeyMappings",
      "net.owleafstudio.modminds.init.ModmindsModScreens",
      "net.owleafstudio.modminds.init.ItemAnimationFactory",
      "net.owleafstudio.modminds.init.EntityAnimationFactory"
   };

   public void onInitializeClient() {
      WorkQueue.setBooting(true);
      ClientHooks.installClientPlayer(() -> Optional.ofNullable(Minecraft.getInstance().player));

      for (String className : CLIENT_INIT_CLASSES) {
         try {
            Class.forName(className, false, ModmindsModClient.class.getClassLoader());
         } catch (Throwable var6) {
            ModmindsMod.LOGGER.warn("Client init class {} could not be loaded: {}", className, var6.toString());
         }
      }

      ForgeEventBus.MOD.post(new FMLClientSetupEvent());
      NetworkCompat.installClient();
      ClientEventPumps.installClient();
      ClientTickPump.install();
      WorkQueue.setBooting(false);
   }
}
