package net.ruleta.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.client.AvisoPop;
import com.supernova.client.ClientSetup;
import com.supernova.client.SupernovaDebugCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.ruleta.Ajustes;
import net.ruleta.AjustesPayload;
import net.ruleta.CambiosPayload;
import net.ruleta.OndaPayload;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.RuletaPayload;
import net.ruleta.agujero.AgujeroPayload;
import net.ruleta.burbuja.Burbujas;
import net.ruleta.candado.Candados;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.agujero.CieloBake;
import net.ruleta.client.burbuja.BurbujaCliente;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.cosmeticos.CosmeticosCliente;
import net.ruleta.client.dedsafio.DedsafioCielo;
import net.ruleta.client.dedsafio.DedsafioCliente;
import net.ruleta.client.eclipse.EclipseCliente;
import net.ruleta.client.escenas.AnimacionMuerte;
import net.ruleta.client.escenas.MomentoNutria;
import net.ruleta.client.linterna.LinternaCliente;
import net.ruleta.client.mapa.MapaCliente;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.client.reviil.AnimacionReviil;
import net.ruleta.client.reviil.CieloReviil;
import net.ruleta.client.reviil.ComandoCieloReviil;
import net.ruleta.client.ruleta.RuletaHud;
import net.ruleta.client.tornado.TornadoCliente;
import net.ruleta.creeperes.client.RegistroClienteCreeperes;
import net.ruleta.creeperrojo.client.RegistroClienteCreeperRojo;
import net.ruleta.herobrine.client.RegistroClienteHerobrine;
import net.ruleta.meteoritos.MeteoritosMod;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.client.ClientEvents;
import net.ruleta.phora.PhoraMod;
import net.ruleta.tornado.TornadoPayload;

public final class RuletaClient implements ClientModInitializer {
   public void onInitializeClient() {
      OpcionesCliente.leer();
      Red.cliente = RuletaClient::recibir;
      RedCliente.registrar();
      net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(AvisoHud.OCULTAR);
      net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(LanzadorRuleta.ABRIR);
      net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(MapaCliente.TECLA);
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(LanzadorRuleta::tecla);
      registrarDibujos();
      PantallaHud.Comando.registrar();
      ComandoCieloReviil.registrar();
      DedsafioCliente.registrar();
      SupernovaDebugCommand.registrar();
      CosmeticosCliente.registrar();
      BurbujaCliente.registrar();
      ClientSetup.registrar();
      ClientEnganches.registrar();
   }

   private static void registrarShaders() { }

   private static void registrarDibujos() {
      EntityRendererRegistry.register(PhoraMod.MISIL.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MeteoritosMod.METEORITO.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MobsNuevos.BOMBA_WARDEN.get(), net.minecraft.client.renderer.entity.VexRenderer::new);
      EntityRendererRegistry.register(Burbujas.BURBUJA.get(), NoopRenderer::new);
      AgujeroCliente.Registro.registrar();
      RegistroClienteCreeperes.registrar();
      RegistroClienteHerobrine.registrar();
      RegistroClienteCreeperRojo.registrar();
      ClientEvents.registrar();
   }

   static void recibir(Object mensaje) {
      if (mensaje instanceof RuletaPayload p) {
         RuletaHud.empezar(p);
      } else if (mensaje instanceof ReviilPayload p) {
         HudCliente.recibir(p);
      } else if (mensaje instanceof CambiosPayload p) {
         CambiosCliente.poner(p.activos());
      } else if (mensaje instanceof AgujeroPayload p) {
         AgujeroCliente.recibir(p);
      } else if (mensaje instanceof TornadoPayload p) {
         TornadoCliente.recibir(p);
      } else if (mensaje instanceof OndaPayload p) {
         OndaExplosion.agregar(p);
      } else if (mensaje instanceof AjustesPayload p) {
         Ajustes.aplicarDeRed(p.valores());
      }
   }

   public static final class Juego {
      private Juego() {
      }

      @SubscribeEvent
      public static void alDibujarHud(RenderGuiEvent.Post e) {
         EfectosCliente.tinte(e.getGuiGraphics(), e.getPartialTick());
         AnimacionReviil.dibujar(e.getGuiGraphics());
         MomentoNutria.dibujar(e.getGuiGraphics());
         AnimacionMuerte.dibujar(e.getGuiGraphics());
         HudCliente.render(e.getGuiGraphics());
         RuletaHud.dibujar(e.getGuiGraphics());
         MensajeZona.dibujar(e.getGuiGraphics(), e.getGuiGraphics().guiWidth(), e.getGuiGraphics().guiHeight());
         AvisoPop.dibujar(e.getGuiGraphics(), e.getGuiGraphics().guiWidth(), e.getGuiGraphics().guiHeight());
      }

      @SubscribeEvent
      public static void alMoverCamara(ViewportEvent.ComputeCameraAngles e) {
         EfectosCliente.camara(e);
      }

      @SubscribeEvent
      public static void alTick(TickEvent.ClientTickEvent e) {
         if (e.phase == TickEvent.Phase.END) {
            while (AvisoHud.OCULTAR.consumeClick()) {
               AvisoHud.alternar();
            }

         }
      }

      @SubscribeEvent
      public static void alEntrar(ClientPlayerNetworkEvent.LoggingIn e) {
         Minecraft.getInstance().execute(RuletaHud::precargar);
      }

      @SubscribeEvent
      public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
         RuletaHud.parar();
         CambiosCliente.poner("");
         HudCliente.limpiar();
         EfectosCliente.limpiarTinte();
      }
   }
}
