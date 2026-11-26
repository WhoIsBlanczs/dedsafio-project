package net.ruleta.dedsafio;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class OcultarServidor {
   private static boolean todos = true;
   private static final Set<UUID> visibles = new HashSet<>();
   private static int cuenta = 0;

   private OcultarServidor() {
   }

   public static void ocultarTodos(MinecraftServer s) {
      todos = true;
      visibles.clear();
      difundir(s);
   }

   public static void mostrarTodos(MinecraftServer s) {
      todos = false;
      visibles.clear();
      difundir(s);
   }

   public static void mostrar(MinecraftServer s, ServerPlayer p) {
      visibles.add(p.getUUID());
      difundir(s);
   }

   public static void tick(MinecraftServer s) {
      if (++cuenta % 100 == 0) {
         difundir(s);
      }
   }

   private static void difundir(MinecraftServer s) {
      for (ServerPlayer p : s.getPlayerList().getPlayers()) {
         if (p.level().dimension().equals(ComandoDedsafio.DIMENSION)) {
            EscenaServidor.enviar(p, new EscenaPayload(6, 0.0F, todos ? 1.0F : 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
            EscenaServidor.enviar(p, new EscenaPayload(7, 0.0F));

            for (UUID u : visibles) {
               ServerPlayer v = s.getPlayerList().getPlayer(u);
               if (v != null) {
                  EscenaServidor.enviar(p, new EscenaPayload(8, 0.0F, v.getId(), 0.0F, 0.0F, 0.0F, 0.0F));
               }
            }
         }
      }
   }
}
