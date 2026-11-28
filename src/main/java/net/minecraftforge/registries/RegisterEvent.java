package net.minecraftforge.registries;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraftforge.eventbus.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterEvent extends Event {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private final Identifier registryName;

   public RegisterEvent(Identifier registryName) {
      this.registryName = registryName;
   }

   public Identifier getRegistryName() {
      return this.registryName;
   }

   public <T> void register(ResourceKey<? extends Registry<T>> key, Consumer<RegisterEvent.RegisterHelper<T>> consumer) {
      Registry<T> registry = ForgeRegistries.resolve(key);
      consumer.accept((name, value) -> {
         if (registry != null && value != null) {
            Identifier id = Identifier.fromNamespaceAndPath("modminds", name);

            try {
               Object t = Registry.register(registry, id, value);
            } catch (Throwable var6) {
               LOGGER.warn("Could not register {} into {}: {}", new Object[]{id, key.identifier(), var6.toString()});
            }
         }
      });
   }

   public static <T> Optional<Holder.Reference<T>> holderOf(Registry<T> registry, T value) {
      return registry.getResourceKey(value).flatMap(registry::get);
   }

   @FunctionalInterface
   public interface RegisterHelper<T> {
      void register(String var1, T var2);
   }
}
