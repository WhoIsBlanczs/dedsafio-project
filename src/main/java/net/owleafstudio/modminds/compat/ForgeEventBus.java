package net.owleafstudio.modminds.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ForgeEventBus implements IEventBus {
   public static final ForgeEventBus FORGE = new ForgeEventBus();
   public static final ForgeEventBus MOD = new ForgeEventBus();
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final Comparator<ForgeEventBus.Handler> BY_PRIORITY = Comparator.comparingInt(h -> h.priority.ordinal());
   private final Map<Class<?>, List<ForgeEventBus.Handler>> handlers = new ConcurrentHashMap<>();
   private final Map<Class<?>, List<ForgeEventBus.Handler>> dispatch = new ConcurrentHashMap<>();

   private ForgeEventBus() {
   }

   @Override
   public void register(Object target) {
      if (target instanceof Class<?> clazz) {
         this.register(clazz);
      } else {
         this.register(target.getClass(), target);
      }
   }

   @Override
   public void register(Class<?> clazz) {
      this.register(clazz, null);
   }

   public void register(Class<?> clazz, Object target) {
      int found = 0;

      for (Method method : clazz.getDeclaredMethods()) {
         SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
         if (annotation != null && method.getParameterCount() == 1) {
            Class<?> param = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(param)) {
               LOGGER.warn("Ignoring @SubscribeEvent {}.{}: parameter is not an Event", clazz.getSimpleName(), method.getName());
            } else {
               method.setAccessible(true);
               this.handlers.computeIfAbsent(param, k -> new CopyOnWriteArrayList<>()).add(new ForgeEventBus.Handler(method, target, annotation.priority()));
               found++;
            }
         }
      }

      if (found > 0) {
         this.dispatch.clear();
      }

      if (found == 0 && target == null) {
         LOGGER.debug("Class {} has no discoverable @SubscribeEvent handler", clazz.getName());
      }

      for (List<ForgeEventBus.Handler> list : this.handlers.values()) {
         list.sort(BY_PRIORITY);
      }
   }

   @Override
   public <T extends Event> boolean post(T event) {
      for (ForgeEventBus.Handler handler : this.dispatch.computeIfAbsent(event.getClass(), this::chainFor)) {
         try {
            handler.method.invoke(handler.target, event);
         } catch (InvocationTargetException var5) {
            LOGGER.error(
               "Handler {}.{} threw while processing {}",
               new Object[]{handler.method.getDeclaringClass().getSimpleName(), handler.method.getName(), event.getClass().getSimpleName(), var5.getCause()}
            );
         } catch (Throwable var6) {
            LOGGER.error("Handler {}.{} could not be invoked", new Object[]{handler.method.getDeclaringClass().getSimpleName(), handler.method.getName(), var6});
         }
      }

      return event.isCanceled();
   }

   private List<ForgeEventBus.Handler> chainFor(Class<?> eventType) {
      List<ForgeEventBus.Handler> chain = new ArrayList<>();

      for (Class<?> type = eventType; type != null && Event.class.isAssignableFrom(type); type = type.getSuperclass()) {
         List<ForgeEventBus.Handler> list = this.handlers.get(type);
         if (list != null) {
            chain.addAll(list);
         }
      }

      chain.sort(BY_PRIORITY);
      return List.copyOf(chain);
   }

   public static void postToBoth(Event event) {
      MOD.post(event);
      FORGE.post(event);
   }

   private static final class Handler {
      final Method method;
      final Object target;
      final EventPriority priority;

      Handler(Method method, Object target, EventPriority priority) {
         this.method = method;
         this.target = target;
         this.priority = priority;
      }
   }
}
