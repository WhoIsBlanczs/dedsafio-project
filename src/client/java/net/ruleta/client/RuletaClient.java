package net.ruleta.client;

import com.supernova.client.ClientSetup;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.ruleta.ReviilPayload;
import net.ruleta.agujero.AgujeroPayload;
import net.ruleta.burbuja.Burbujas;
import net.ruleta.candado.CodigoAbrirPayload;
import net.ruleta.candado.CodigoRespuestaPayload;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.burbuja.BurbujaCliente;
import net.ruleta.client.burbuja.BurbujaRenderer;
import net.ruleta.client.codigo.PantallaCodigo;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.mobs.BombaWardenRenderer;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.creeperes.client.RegistroClienteCreeperes;
import net.ruleta.creeperrojo.client.RegistroClienteCreeperRojo;
import net.ruleta.estatua.client.EstatuaNutriaCliente;
import net.ruleta.herobrine.client.RegistroClienteHerobrine;
import net.ruleta.meteoritos.MeteoritosMod;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.client.ClientEvents;
import net.ruleta.phora.PhoraMod;
import net.ruleta.puerta.client.PuertaBunkerCliente;
import net.ruleta.tragamonedas.client.TragamonedasCliente;

/**
 * Cliente del catálogo de DEDsafio 4 Ruleta Fan Project (c) 2026 DarkPlayer y colaboradores, CC BY-NC-SA 4.0.
 * Solo dibujos y efectos de mobs, entidades, ítems y bloques: sin el menú de la ruleta ni las misiones.
 */
public final class RuletaClient implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      ClientPlayNetworking.registerGlobalReceiver(ReviilPayload.TYPE, (m, ctx) -> recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(AgujeroPayload.TYPE, (m, ctx) -> AgujeroCliente.recibir(m));
      ClientPlayNetworking.registerGlobalReceiver(CodigoAbrirPayload.TYPE, (m, ctx) -> PantallaCodigo.abrir(m.modo(), m.pos()));
      ClientPlayNetworking.registerGlobalReceiver(CodigoRespuestaPayload.TYPE, (m, ctx) -> PantallaCodigo.respuesta(m.ok()));
      ClientPlayNetworking.registerGlobalReceiver(net.ruleta.nuclear.network.TotemActivatePacket.TYPE, (m, ctx) -> net.ruleta.nuclear.client.ClientTotemHooks.showActivation(m.stack()));
      net.ruleta.cajero.client.CajeroCliente.registrar();
      registrarDibujos();
      BurbujaCliente.registrar();
      ClientSetup.registrar();
      ClientEnganches.registrar();
   }

   private static void registrarDibujos() {
      EntityRendererRegistry.register(PhoraMod.MISIL.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MeteoritosMod.METEORITO.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MobsNuevos.BOMBA_WARDEN.get(), BombaWardenRenderer::new);
      EntityRendererRegistry.register(Burbujas.BURBUJA.get(), BurbujaRenderer::new);
      AgujeroCliente.Registro.registrar();
      RegistroClienteCreeperes.registrar();
      RegistroClienteHerobrine.registrar();
      RegistroClienteCreeperRojo.registrar();
      PuertaBunkerCliente.registrar();
      TragamonedasCliente.registrar();
      EstatuaNutriaCliente.registrar();
      ClientEvents.registrar();
   }

   /** Las piezas de ReviilPayload que mandan los palos de Phora y de meteoritos. */
   private static void recibir(ReviilPayload p) {
      switch (p.pieza()) {
         case "phora" -> PhoraCliente.recibir(p.valor());
         case "phora_voz" -> PhoraCliente.voz();
         case "meteoritos" -> MeteoritosCliente.recibir(p.valor());
         case "meteorito_impacto" -> MeteoritosCliente.impacto(p.valor(), p.titulo().getString());
         case "impacto" -> PhoraCliente.impacto(p.valor(), p.titulo().getString());
         default -> {
         }
      }
   }
}
