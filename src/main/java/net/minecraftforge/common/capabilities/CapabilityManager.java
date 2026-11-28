package net.minecraftforge.common.capabilities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityManager {
   private static final Map<String, Capability<?>> CACHE = new ConcurrentHashMap<>();

   public static <T> Capability<T> get(CapabilityToken<T> type) {
      return (Capability<T>)CACHE.computeIfAbsent(type.name(), Capability::new);
   }

   public static <T> Capability<T> get(String name) {
      return (Capability<T>)CACHE.computeIfAbsent(name, Capability::new);
   }

   private CapabilityManager() {
   }
}
