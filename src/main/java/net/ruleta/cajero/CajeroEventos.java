package net.ruleta.cajero;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.tragamonedas.TragamonedasBloque;

public final class CajeroEventos {
   private CajeroEventos() {
   }

   public static void registrar() {
      Bus.registrar(CajeroEventos.class);
      ServerPlayConnectionEvents.JOIN.register((Join)(manejador, emisor, servidor) -> CajeroServidor.enviarSaldo(manejador.getPlayer()));
      ServerPlayerEvents.AFTER_RESPAWN.register((AfterRespawn)(viejo, nuevo, vivo) -> CajeroServidor.enviarSaldo(nuevo));
   }

   private static boolean protegido(BlockState estado) {
      return estado.getBlock() instanceof CajeroBloque || estado.getBlock() instanceof TragamonedasBloque;
   }

   @SubscribeEvent
   public static void alRomper(BlockEvent.BreakEvent e) {
      if (protegido(e.getState())) {
         boolean permitido = e.getPlayer() instanceof ServerPlayer sp && sp.isCreative() && net.ruleta.compat.Compat.permiso(sp, 2);
         if (!permitido) {
            e.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void alExplotar(ExplosionEvent.Detonate e) {
      e.getAffectedBlocks().removeIf(p -> protegido(e.getLevel().getBlockState(p)));
   }
}
