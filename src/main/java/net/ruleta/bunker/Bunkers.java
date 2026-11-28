package net.ruleta.bunker;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Sin el sistema de búnkeres de la ruleta: nada queda protegido y no hay búnkeres registrados. */
public final class Bunkers {
   private Bunkers() {
   }

   public static boolean protegido(Level nivel, BlockPos pos, BlockState estado) {
      return false;
   }

   public static boolean cercaDeBunker(ServerLevel nivel, double x, double z, int radio) {
      return false;
   }

   public static Object interiorDe(ServerLevel nivel, BlockPos pos) {
      return null;
   }

   public static boolean enCaja(Level nivel, BlockPos pos) {
      return false;
   }
}
