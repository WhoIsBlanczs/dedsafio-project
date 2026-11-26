package net.ruleta.cajero.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.ruleta.cajero.CajeroRed;
import net.ruleta.client.compat.Bus;

@Environment(EnvType.CLIENT)
public final class CajeroCliente {
   public static volatile long saldo = 0L;
   public static final ResourceLocation FICHA_ROJA = tex("ficha_rojo");
   public static final ResourceLocation FICHA_VERDE = tex("ficha_verde");
   public static final ResourceLocation FICHA_AZUL = tex("ficha_azul");
   public static final int FICHA_W = 12;
   public static final int FICHA_H = 13;
   public static final long[] VALOR = new long[]{10000L, 100L, 1L};

   private CajeroCliente() {
   }

   public static void registrar() {
      ClientPlayNetworking.registerGlobalReceiver(CajeroRed.AbrirPayload.TYPE, (m, ctx) -> abrir(m.pos(), m.saldo()));
      ClientPlayNetworking.registerGlobalReceiver(CajeroRed.SaldoPayload.TYPE, (m, ctx) -> saldo(m.saldo()));
      ClientPlayNetworking.registerGlobalReceiver(CajeroRed.RespuestaPayload.TYPE, (m, ctx) -> respuesta(m.ok(), m.clave()));
      Bus.registrar(CajeroHud.class);
   }

   public static ResourceLocation tex(String nombre) {
      return ResourceLocation.fromNamespaceAndPath("ruleta", "textures/gui/cajero/" + nombre + ".png");
   }

   public static ResourceLocation icono(int i) {
      return i == 0 ? FICHA_ROJA : (i == 1 ? FICHA_VERDE : FICHA_AZUL);
   }

   public static long[] partes(long s) {
      long rojas = s / 10000L;
      long verdes = s / 100L % 100L;
      long azules = s % 100L;
      return new long[]{rojas, verdes, azules};
   }

   public static void saldo(long nuevo) {
      saldo = Math.max(0L, nuevo);
   }

   public static void abrir(BlockPos pos, long nuevoSaldo) {
      saldo = Math.max(0L, nuevoSaldo);
      Minecraft.getInstance().setScreen(new PantallaCajero(pos));
   }

   public static void respuesta(boolean ok, String clave) {
      if (Minecraft.getInstance().screen instanceof PantallaCajero p) {
         p.recibirRespuesta(ok, clave);
      }
   }
}
