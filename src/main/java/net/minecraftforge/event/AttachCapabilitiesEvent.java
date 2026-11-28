package net.minecraftforge.event;

import net.minecraft.resources.Identifier;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;

public class AttachCapabilitiesEvent<T> extends Event {
   private final T object;

   public AttachCapabilitiesEvent(Class<T> type, T object) {
      this.object = object;
   }

   public T getObject() {
      return this.object;
   }

   public void addCapability(Identifier name, ICapabilityProvider provider) {
   }
}
