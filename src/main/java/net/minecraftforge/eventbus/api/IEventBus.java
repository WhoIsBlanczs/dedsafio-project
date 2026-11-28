package net.minecraftforge.eventbus.api;

public interface IEventBus {
   void register(Object var1);

   void register(Class<?> var1);

   <T extends Event> boolean post(T var1);
}
