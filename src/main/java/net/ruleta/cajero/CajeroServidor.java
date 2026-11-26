package net.ruleta.cajero;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.ruleta.tragamonedas.Tragamonedas;

public final class CajeroServidor {
   public static final long[] VALOR = new long[]{10000L, 100L, 1L};
   public static final int MAX_POR_FICHA = 99;

   private CajeroServidor() {
   }

   public static Item[] fichas() {
      return new Item[]{Tragamonedas.DEDITA_ROJO.get(), Tragamonedas.DEDITA_VERDE.get(), Tragamonedas.DEDITA_AZUL.get()};
   }

   public static void enviarSaldo(ServerPlayer jugador) {
      long saldo = SaldoDatos.get(jugador.server).saldo(jugador.getUUID());
      ServerPlayNetworking.send(jugador, new CajeroRed.SaldoPayload(saldo));
   }

   private static void responder(ServerPlayer jugador, boolean ok, String clave) {
      ServerPlayNetworking.send(jugador, new CajeroRed.RespuestaPayload(ok, clave));
   }

   public static void accion(ServerPlayer jugador, CajeroRed.AccionPayload m) {
      if (jugador != null) {
         BlockPos pos = m.pos();
         if (!(jugador.distanceToSqr(Vec3.atCenterOf(pos)) > 100.0)
            && jugador.level().isLoaded(pos)
            && jugador.level().getBlockState(pos).getBlock() instanceof CajeroBloque) {
            int[] cant = new int[]{Mth.clamp(m.rojas(), 0, 99), Mth.clamp(m.verdes(), 0, 99), Mth.clamp(m.azules(), 0, 99)};
            long valor = 0L;

            for (int i = 0; i < 3; i++) {
               valor += cant[i] * VALOR[i];
            }

            if (valor <= 0L) {
               responder(jugador, false, "vacio");
            } else {
               SaldoDatos datos = SaldoDatos.get(jugador.server);
               long saldo = datos.saldo(jugador.getUUID());
               Item[] items = fichas();
               if (m.ingresar()) {
                  for (int i = 0; i < 3; i++) {
                     if (contar(jugador, items[i]) < cant[i]) {
                        responder(jugador, false, "fichas");
                        return;
                     }
                  }

                  if (saldo + valor > 9000000000000L) {
                     responder(jugador, false, "lleno");
                     return;
                  }

                  for (int i = 0; i < 3; i++) {
                     quitar(jugador, items[i], cant[i]);
                  }

                  datos.poner(jugador.getUUID(), saldo + valor);
                  jugador.containerMenu.broadcastChanges();
                  enviarSaldo(jugador);
                  responder(jugador, true, "ingresado");
               } else {
                  if (valor > saldo) {
                     responder(jugador, false, "saldo");
                     return;
                  }

                  datos.poner(jugador.getUUID(), saldo - valor);

                  for (int i = 0; i < 3; i++) {
                     dar(jugador, items[i], cant[i]);
                  }

                  jugador.containerMenu.broadcastChanges();
                  enviarSaldo(jugador);
                  responder(jugador, true, "retirado");
               }
            }
         } else {
            responder(jugador, false, "lejos");
         }
      }
   }

   private static int contar(Player jugador, Item item) {
      int n = 0;

      for (ItemStack s : jugador.getInventory().items) {
         if (s.is(item)) {
            n += s.getCount();
         }
      }

      for (ItemStack s : jugador.getInventory().offhand) {
         if (s.is(item)) {
            n += s.getCount();
         }
      }

      return n;
   }

   private static void quitar(Player jugador, Item item, int cantidad) {
      int falta = cantidad;

      for (ItemStack s : jugador.getInventory().items) {
         if (falta <= 0) {
            return;
         }

         if (s.is(item)) {
            int t = Math.min(falta, s.getCount());
            s.shrink(t);
            falta -= t;
         }
      }

      for (ItemStack s : jugador.getInventory().offhand) {
         if (falta <= 0) {
            return;
         }

         if (s.is(item)) {
            int t = Math.min(falta, s.getCount());
            s.shrink(t);
            falta -= t;
         }
      }
   }

   private static void dar(Player jugador, Item item, int cantidad) {
      int resto = cantidad;

      while (resto > 0) {
         int c = Math.min(resto, 64);
         ItemStack pila = new ItemStack(item, c);
         jugador.getInventory().add(pila);
         if (!pila.isEmpty()) {
            jugador.drop(pila, false);
         }

         resto -= c;
      }
   }
}
