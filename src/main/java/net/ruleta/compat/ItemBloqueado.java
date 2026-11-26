package net.ruleta.compat;

import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ItemBloqueado {
   private static UUID jugador;

   private ItemBloqueado() {
   }

   public static void marcar(UUID id) {
      jugador = id;
   }

   public static void limpiar() {
      jugador = null;
   }

   public static boolean bloqueado(@Nullable Player p) {
      return p != null && jugador != null && jugador.equals(p.getUUID());
   }
}
