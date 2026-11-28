package com.watonstudio.creepermetalico;

import java.util.HashSet;
import java.util.Set;

public final class TotemEonClientState {
   private static final Set<Integer> PENDIENTES = new HashSet<>();

   private TotemEonClientState() {
   }

   public static synchronized void mark(int entityId) {
      PENDIENTES.add(entityId);
   }

   public static synchronized boolean consume(int entityId) {
      return PENDIENTES.remove(entityId);
   }
}
