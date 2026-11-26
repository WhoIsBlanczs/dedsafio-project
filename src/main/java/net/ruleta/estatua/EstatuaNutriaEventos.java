package net.ruleta.estatua;

import net.minecraft.server.level.ServerPlayer;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;

public final class EstatuaNutriaEventos {
   private EstatuaNutriaEventos() {
   }

   public static void registrar() {
      Bus.registrar(EstatuaNutriaEventos.class);
   }

   @SubscribeEvent
   public static void alRomper(BlockEvent.BreakEvent e) {
      if (e.getState().getBlock() instanceof EstatuaNutriaBloque) {
         boolean permitido = e.getPlayer() instanceof ServerPlayer sp && sp.isCreative() && sp.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
         if (!permitido) {
            e.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void alExplotar(ExplosionEvent.Detonate e) {
      e.getAffectedBlocks().removeIf(p -> e.getLevel().getBlockState(p).getBlock() instanceof EstatuaNutriaBloque);
   }
}
