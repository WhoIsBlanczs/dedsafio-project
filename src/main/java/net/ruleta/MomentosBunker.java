package net.ruleta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Sin los momentos de búnker de la ruleta: ninguna puerta está controlada por un momento. */
public final class MomentosBunker {
   private MomentosBunker() {
   }

   public static boolean gestionaPuerta(Level nivel, BlockPos pos) {
      return false;
   }

   public static double[] centro() {
      return null;
   }
}
