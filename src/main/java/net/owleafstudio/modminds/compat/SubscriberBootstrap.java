package net.owleafstudio.modminds.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SubscriberBootstrap {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static int registered;
   private static int skipped;

   private SubscriberBootstrap() {
   }

   public static void registerAll(IEventBus modBus, IEventBus forgeBus) {
      boolean client = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
      ClassLoader loader = SubscriberBootstrap.class.getClassLoader();

      for (String name : SubscriberIndex.CLASSES) {
         try {
            Class<?> clazz = Class.forName(name, false, loader);
            Mod.EventBusSubscriber annotation = clazz.getAnnotation(Mod.EventBusSubscriber.class);
            if (annotation == null) {
               skipped++;
            } else {
               Dist[] dists = annotation.value();
               boolean clientOnly = false;
               boolean serverOnly = false;

               for (Dist dist : dists) {
                  if (dist == Dist.CLIENT) {
                     clientOnly = true;
                  } else {
                     serverOnly = true;
                  }
               }

               if ((!clientOnly || client) && (!serverOnly || !client)) {
                  (annotation.bus() == Mod.EventBusSubscriber.Bus.MOD ? modBus : forgeBus).register(clazz);
                  registered++;
               } else {
                  skipped++;
               }
            }
         } catch (Throwable var17) {
            skipped++;
            LOGGER.warn("Subscriber {} could not be registered: {}", name, var17.toString());
         }
      }

      LOGGER.info("Event subscribers: {} registered, {} skipped for this environment", registered, skipped);
   }

   public static int getRegisteredCount() {
      return registered;
   }
}
