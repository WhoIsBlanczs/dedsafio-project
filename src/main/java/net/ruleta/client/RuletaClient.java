package net.ruleta.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.client.AvisoPop;
import com.supernova.client.ClientSetup;
import com.supernova.client.SupernovaDebugCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
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
import net.ruleta.client.burbuja.BurbujaRenderer;
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
import net.ruleta.client.mobs.BombaWardenRenderer;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.client.reviil.AnimacionReviil;
import net.ruleta.client.reviil.CieloReviil;
import net.ruleta.client.reviil.ComandoCieloReviil;
import net.ruleta.client.ruleta.RuletaHud;
import net.ruleta.client.tornado.TornadoCliente;
import net.ruleta.creeperes.client.RegistroClienteCreeperes;
import net.ruleta.creeperrojo.client.RegistroClienteCreeperRojo;
import net.ruleta.estatua.client.EstatuaNutriaCliente;
import net.ruleta.herobrine.client.RegistroClienteHerobrine;
import net.ruleta.meteoritos.MeteoritosMod;
import net.ruleta.mobs.MobsNuevos;
import net.ruleta.nuclear.client.ClientEvents;
import net.ruleta.phora.PhoraMod;
import net.ruleta.puerta.client.PuertaBunkerCliente;
import net.ruleta.tornado.TornadoPayload;
import net.ruleta.tragamonedas.client.TragamonedasCliente;

public final class RuletaClient implements ClientModInitializer {
   public void onInitializeClient() {
      OpcionesCliente.leer();
      Red.cliente = RuletaClient::recibir;
      RedCliente.registrar();
      KeyBindingHelper.registerKeyBinding(AvisoHud.OCULTAR);
      KeyBindingHelper.registerKeyBinding(LanzadorRuleta.ABRIR);
      KeyBindingHelper.registerKeyBinding(MapaCliente.TECLA);
      BlockRenderLayerMap.INSTANCE.putBlock(Candados.PUERTA.get(), RenderType.cutout());
      registrarShaders();
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

   private static void registrarShaders() {
      CoreShaderRegistrationCallback.EVENT
         .register(
            (CoreShaderRegistrationCallback)ctx -> {
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "ruleta_hd"), DefaultVertexFormat.POSITION_TEX_COLOR, RuletaHud::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "cielo_reviil"), DefaultVertexFormat.POSITION, CieloReviil::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "dedsafio_cielo"), DefaultVertexFormat.POSITION, DedsafioCielo::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "linterna_haz"), DefaultVertexFormat.POSITION, LinternaCliente::shaderHaz);
               ctx.register(
                  ResourceLocation.fromNamespaceAndPath("ruletafanproject", "linterna_negro"), DefaultVertexFormat.POSITION, LinternaCliente::shaderNegro
               );
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "nutria_arco"), DefaultVertexFormat.POSITION_TEX, MomentoNutria::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "agujero_cielo"), DefaultVertexFormat.POSITION, AgujeroCliente::shader);
               ctx.register(
                  ResourceLocation.fromNamespaceAndPath("ruletafanproject", "agujero_objeto"), DefaultVertexFormat.POSITION_TEX, AgujeroCliente::shaderObjeto
               );
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "agujero_bake"), DefaultVertexFormat.POSITION, CieloBake::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "eclipse"), DefaultVertexFormat.POSITION, EclipseCliente::shader);
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "phora_cielo"), DefaultVertexFormat.POSITION, PhoraCliente::shaderCielo);
               ctx.register(
                  ResourceLocation.fromNamespaceAndPath("ruletafanproject", "tormenta_cielo"), DefaultVertexFormat.POSITION, TornadoCliente::shaderCielo
               );
               ctx.register(
                  ResourceLocation.fromNamespaceAndPath("ruletafanproject", "tornado_embudo"),
                  DefaultVertexFormat.POSITION_TEX_COLOR,
                  TornadoCliente::shaderEmbudo
               );
               ctx.register(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "phora"), DefaultVertexFormat.POSITION_TEX_COLOR, PhoraCliente::shader);
               ctx.register(
                  ResourceLocation.fromNamespaceAndPath("ruletafanproject", "meteoritos"), DefaultVertexFormat.POSITION_TEX_COLOR, MeteoritosCliente::shader
               );
            }
         );
   }

   private static void registrarDibujos() {
      EntityRendererRegistry.register(PhoraMod.MISIL.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MeteoritosMod.METEORITO.get(), NoopRenderer::new);
      EntityRendererRegistry.register(MobsNuevos.BOMBA_WARDEN.get(), BombaWardenRenderer::new);
      EntityRendererRegistry.register(Burbujas.BURBUJA.get(), BurbujaRenderer::new);
      EntityRendererRegistry.register(EntityType.LIGHTNING_BOLT, RayoRealista::new);
      AgujeroCliente.Registro.registrar();
      RegistroClienteCreeperes.registrar();
      RegistroClienteHerobrine.registrar();
      RegistroClienteCreeperRojo.registrar();
      PuertaBunkerCliente.registrar();
      TragamonedasCliente.registrar();
      EstatuaNutriaCliente.registrar();
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

            LanzadorRuleta.tecla(Minecraft.getInstance());
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
