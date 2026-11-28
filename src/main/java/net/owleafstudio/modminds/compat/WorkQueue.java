package net.owleafstudio.modminds.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorkQueue {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static final Map<Integer, List<Runnable>> DELAYED = new ConcurrentHashMap<>();
   private static int tickCounter = 0;
   private static boolean booting = true;

   private WorkQueue() {
   }

   public static void setBooting(boolean value) {
      booting = value;
   }

   public static void runNowOrQueue(Runnable work) {
      if (booting) {
         runSafely(work);
      } else {
         queue(0, work);
      }
   }

   public static void queue(int ticks, Runnable work) {
      DELAYED.computeIfAbsent(tickCounter + Math.max(0, ticks), k -> new ArrayList<>()).add(work);
   }

   public static void advanceTick() {
      tickCounter++;
      List<Runnable> due = DELAYED.remove(tickCounter);
      if (due != null) {
         due.forEach(WorkQueue::runSafely);
      }

      for (int key : new ArrayList<>(DELAYED.keySet())) {
         if (key < tickCounter) {
            List<Runnable> late = DELAYED.remove(key);
            if (late != null) {
               late.forEach(WorkQueue::runSafely);
            }
         }
      }
   }

   private static void runSafely(Runnable work) {
      try {
         work.run();
      } catch (Throwable var2) {
         LOGGER.error("Queued modminds work failed", var2);
      }
   }
}
