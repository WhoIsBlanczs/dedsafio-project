package net.owleafstudio.modminds.compat;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;

public final class ClientHooks {
   private static Supplier<Optional<Player>> clientPlayer = () -> Optional.empty();

   private ClientHooks() {
   }

   public static void installClientPlayer(Supplier<Optional<Player>> provider) {
      clientPlayer = provider;
   }

   public static Optional<Player> clientPlayer() {
      return clientPlayer.get();
   }
}
