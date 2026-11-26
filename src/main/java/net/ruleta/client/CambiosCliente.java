package net.ruleta.client;

import java.util.Set;
import net.ruleta.Cambios;
import net.ruleta.CambiosEfectos;

public final class CambiosCliente {
   private static volatile Set<String> activos = Set.of();

   private CambiosCliente() {
   }

   public static Set<String> activos() {
      return activos;
   }

   public static boolean activo(String id) {
      return activos.contains(id);
   }

   static void poner(String csv) {
      activos = Set.copyOf(Cambios.separar(csv));
      CambiosEfectos.activosCliente = activos;
   }
}
