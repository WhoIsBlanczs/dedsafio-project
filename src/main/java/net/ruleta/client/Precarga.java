package net.ruleta.client;

import net.ruleta.client.escenas.AnimacionMuerte;
import net.ruleta.client.escenas.MomentoNutria;
import net.ruleta.client.reviil.AnimacionReviil;
import net.ruleta.client.reviil.CabezaReviil;
import net.ruleta.client.ruleta.Pintor;

public final class Precarga {
   private static boolean hecho;

   private Precarga() {
   }

   public static void alEntrar() {
      Pintor.precargar();
      if (!hecho) {
         hecho = true;

         try {
            AnimacionReviil.precargar();
            CabezaReviil.precargar();
            MomentoNutria.precargar();
            AnimacionMuerte.precargar();
         } catch (RuntimeException var1) {
         }
      }
   }

   public static void tick() {
      Pintor.subirPendientes();
   }
}
