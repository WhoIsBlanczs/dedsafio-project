package net.minecraftforge.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredRegister<T> {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final List<DeferredRegister<?>> INSTANCES = new ArrayList<>();
   private static int REGISTERED;
   private final Registry<T> registry;
   private final String namespace;
   private final Identifier registryName;
   private final List<DeferredRegister.Pending<T>> pending = new ArrayList<>();
   private boolean flushed;

   private DeferredRegister(Registry<T> registry, String namespace, Identifier registryName) {
      this.registry = registry;
      this.namespace = namespace;
      this.registryName = registryName;
   }

   public static <T> DeferredRegister<T> create(ForgeRegistryHandle<T> handle, String namespace) {
      return registerInstance(new DeferredRegister<>(handle.vanilla(), namespace, handle.vanilla().key().identifier()));
   }

   public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key, String namespace) {
      return registerInstance(new DeferredRegister<>(ForgeRegistries.resolve(key), namespace, key.identifier()));
   }

   public static <T> DeferredRegister<T> create(Registry<T> registry, String namespace) {
      return registerInstance(new DeferredRegister<>(registry, namespace, registry.key().identifier()));
   }

   private static <T> DeferredRegister<T> registerInstance(DeferredRegister<T> instance) {
      INSTANCES.add(instance);
      return instance;
   }

   public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      Identifier id = Identifier.fromNamespaceAndPath(this.namespace, name);
      RegistryObject<I> object = new RegistryObject<>(id);
      object.bind(this, supplier);
      if (this.flushed) {
         this.write(object, supplier);
      } else {
         this.pending.add(new DeferredRegister.Pending<>(object, supplier));
      }

      return object;
   }

   public <I extends T> RegistryObject<I> register(Identifier name, Supplier<? extends I> supplier) {
      return this.register(name.getPath(), supplier);
   }

   void materialise(RegistryObject<T> object) {
      Supplier<? extends T> supplier = object.pendingSupplier();
      if (supplier != null) {
         this.pending.removeIf(entry -> entry.object() == object);
         this.write(object, supplier);
      }
   }

   private <I extends T> void write(RegistryObject<I> object, Supplier<? extends I> supplier) {
      object.release();

      I value;
      try {
         // 26.3: Item/Block.Properties necesitan saber su id antes de crearse
         if (this.registry == net.minecraft.core.registries.BuiltInRegistries.ITEM) {
            value = (I)net.owleafstudio.modminds.compat.Ids.conItem(object.getId(), supplier);
         } else if (this.registry == net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
            value = (I)net.owleafstudio.modminds.compat.Ids.conBloque(object.getId(), supplier);
         } else {
            value = (I)supplier.get();
         }
      } catch (Throwable var7) {
         LOGGER.error("Supplier for {} failed", object.getId(), var7);
         object.setValue(null, true);
         return;
      }

      boolean virtual = true;
      if (this.registry != null && value != null) {
         try {
            Object ignored = Registry.register(this.registry, object.getId(), value);
            virtual = false;
            REGISTERED++;
         } catch (Throwable var6) {
            LOGGER.warn("Could not register {} into {}: {} - value stays mod-local", new Object[]{object.getId(), this.registryName, var6.toString()});
         }
      } else if (this.registry == null) {
         LOGGER.debug("No vanilla registry for {}: {} is mod-local", this.registryName, object.getId());
      }

      object.setValue(value, virtual);
   }

   public static void flushAll() {
      for (int i = 0; i < INSTANCES.size(); i++) {
         INSTANCES.get(i).flush();
      }
   }

   public void flush() {
      if (!this.flushed) {
         this.flushed = true;

         for (DeferredRegister.Pending<T> entry : this.pending) {
            this.write(entry.object(), entry.supplier());
         }

         this.pending.clear();
      }
   }

   public void register(IEventBus bus) {
   }

   public Identifier getRegistryName() {
      return this.registryName;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public static int pendingCount() {
      int total = 0;

      for (DeferredRegister<?> instance : INSTANCES) {
         total += instance.pending.size();
      }

      return total;
   }

   public static int registeredCount() {
      return REGISTERED;
   }

   private record Pending<T>(RegistryObject<T> object, Supplier<? extends T> supplier) {
   }
}
