package net.ruleta.client;

import com.supernova.SupernovaNet;
import com.supernova.client.SupernovaGhosts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.ruleta.AbrirPantallaPayload;
import net.ruleta.AjustesPayload;
import net.ruleta.CambiosPayload;
import net.ruleta.LanzarPayload;
import net.ruleta.OndaPayload;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.RuletaPayload;
import net.ruleta.ZonaAvisoPayload;
import net.ruleta.agujero.AgujeroPayload;
import net.ruleta.bunker.BunkerPayload;
import net.ruleta.cajero.client.CajeroCliente;
import net.ruleta.candado.CodigoAbrirPayload;
import net.ruleta.candado.CodigoRespuestaPayload;
import net.ruleta.client.bunker.BunkerMarcador;
import net.ruleta.client.codigo.PantallaCodigo;
import net.ruleta.client.cosmeticos.CosmeticosCliente;
import net.ruleta.cosmeticos.EstadoPayload;
import net.ruleta.estatua.client.PedestalCliente;
import net.ruleta.nuclear.client.ClientTotemHooks;
import net.ruleta.nuclear.network.TotemActivatePacket;
import net.ruleta.tornado.TornadoPayload;
import net.ruleta.tragamonedas.client.CasinoCliente;

@Environment(EnvType.CLIENT)
public final class RedCliente {
   private RedCliente() {
   }

   public static void registrar() {
      ClientPlayNetworking.registerGlobalReceiver(RuletaPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(ReviilPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(CambiosPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(AgujeroPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(OndaPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(AjustesPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(TornadoPayload.TYPE, (m, ctx) -> Red.cliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(BunkerPayload.TYPE, (m, ctx) -> BunkerMarcador.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(AbrirPantallaPayload.TYPE, (m, ctx) -> PantallasCliente.abrir(m.pantalla(), m.datos()));
      ClientPlayNetworking.registerGlobalReceiver(ZonaAvisoPayload.TYPE, (m, ctx) -> MensajeZona.mostrar(m.texto(), m.milisegundos()));
      ClientPlayNetworking.registerGlobalReceiver(CodigoAbrirPayload.TYPE, (m, ctx) -> PantallaCodigo.abrir(m.modo(), m.pos()));
      ClientPlayNetworking.registerGlobalReceiver(CodigoRespuestaPayload.TYPE, (m, ctx) -> PantallaCodigo.respuesta(m.ok()));
      ClientPlayNetworking.registerGlobalReceiver(EstadoPayload.TYPE, (m, ctx) -> CosmeticosCliente.recibir(m));
      CasinoCliente.registrar();
      PedestalCliente.registrar();
      CajeroCliente.registrar();
      ClientPlayNetworking.registerGlobalReceiver(TotemActivatePacket.TYPE, (m, ctx) -> ClientTotemHooks.showActivation(m.stack()));
      ClientPlayNetworking.registerGlobalReceiver(SupernovaNet.SyncMessage.TYPE, (m, ctx) -> SupernovaGhosts.apply(m));
   }

   public static void enviar(Object mensaje) {
      ClientPlayNetworking.send((CustomPacketPayload)mensaje);
   }

   public static boolean servidorConMod() {
      return ClientPlayNetworking.canSend(LanzarPayload.TYPE);
   }
}
