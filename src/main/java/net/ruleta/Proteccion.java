package net.ruleta;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public final class Proteccion {
   public static final int NIVEL_OP = 2;
   private static final Set<String> NOMBRES = Set.of(
      "tragamonedas", "puerta_bunker", "puerta_bunker_hoja", "puerta_grande", "ladrillo_bunker", "escalera_bunker", "piso_bunker", "peligro_bunker"
   );
   private static final ConcurrentHashMap<Block, Boolean> CACHE = new ConcurrentHashMap<>();

   private Proteccion() {
   }

   public static boolean protegido(Block bloque) {
      return CACHE.computeIfAbsent(bloque, b -> {
         Identifier clave = BuiltInRegistries.BLOCK.getKey(b);
         return "ruletafanproject".equals(clave.getNamespace()) && NOMBRES.contains(clave.getPath());
      });
   }

   public static boolean puede(Player jugador) {
      return jugador != null && jugador.isCreative() && net.ruleta.compat.Compat.permiso(jugador, 2);
   }
}
