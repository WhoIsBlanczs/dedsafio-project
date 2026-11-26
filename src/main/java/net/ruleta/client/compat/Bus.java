package net.ruleta.client.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bus {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final Map<Class<?>, List<Bus.Manejador>> MANEJADORES = new HashMap<>();

   private Bus() {
   }

   public static void registrar(Class<?>... clases) {
      for (Class<?> clase : clases) {
         for (Method m : clase.getDeclaredMethods()) {
            SubscribeEvent a = m.getAnnotation(SubscribeEvent.class);
            if (a != null) {
               if (Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 1 && Evento.class.isAssignableFrom(m.getParameterTypes()[0])) {
                  m.setAccessible(true);
                  List<Bus.Manejador> lista = MANEJADORES.computeIfAbsent(m.getParameterTypes()[0], k -> new ArrayList<>());
                  lista.add(new Bus.Manejador(m, a.priority().ordinal(), a.receiveCanceled()));
                  lista.sort(Comparator.comparingInt(Bus.Manejador::prioridad));
               } else {
                  LOG.error("@SubscribeEvent mal puesto en {}#{}: debe ser estático y recibir un Evento", clase.getName(), m.getName());
               }
            }
         }
      }
   }

   public static boolean post(Evento evento) {
      List<Bus.Manejador> lista = MANEJADORES.get(evento.getClass());
      if (lista == null) {
         return false;
      }

      for (Bus.Manejador h : lista) {
         if (!evento.isCanceled() || h.recibeCancelados()) {
            try {
               h.metodo().invoke(null, evento);
            } catch (InvocationTargetException ex) {
               LOG.error("Fallo en el manejador {}#{}", new Object[]{h.metodo().getDeclaringClass().getName(), h.metodo().getName(), ex.getCause()});
            } catch (ReflectiveOperationException ex) {
               LOG.error("No se pudo llamar a {}#{}", new Object[]{h.metodo().getDeclaringClass().getName(), h.metodo().getName(), ex});
            }
         }
      }

      return evento.isCanceled();
   }

   public static boolean hayOyentes(Class<? extends Evento> tipo) {
      List<Bus.Manejador> l = MANEJADORES.get(tipo);
      return l != null && !l.isEmpty();
   }

   private record Manejador(Method metodo, int prioridad, boolean recibeCancelados) {
   }
}
