package net.ruleta.client;

import com.supernova.client.ScreenEffectHandler;
import com.supernova.client.SupernovaGhosts;
import com.supernova.client.SupernovaRenderHandler;
import com.supernova.client.SupernovaSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.bunker.BunkerMarcador;
import net.ruleta.client.burbuja.BurbujaCliente;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.cosmeticos.CosmeticosCliente;
import net.ruleta.client.dedsafio.EventosDedsafio;
import net.ruleta.client.eclipse.EclipseCliente;
import net.ruleta.client.linterna.LinternaCliente;
import net.ruleta.client.mapa.MapaCliente;
import net.ruleta.client.mapa.MapaDatos;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.client.reviil.EventosReviil;
import net.ruleta.client.tornado.TornadoCliente;
import net.ruleta.creeperrojo.client.PantallaAzul;
import net.ruleta.nuclear.client.RadiationSoundHandler;

public final class ClientEnganches {
   private ClientEnganches() {
   }

   public static void registrar() {
      Bus.registrar(
         ScreenEffectHandler.class, SupernovaGhosts.class, SupernovaRenderHandler.class, SupernovaSounds.class,
         ChatBrillo.class, ChatRojo.class, LluviaRoja.class, MisOjos.class, NeblinaMundo.class, OndaExplosion.class,
         ProfundidadFabulosa.class, RuletaClient.Juego.class, VidaNombre.class, AgujeroCliente.class, BunkerMarcador.class,
         EventosDedsafio.class, BurbujaCliente.Teclas.class, CosmeticosCliente.Chispas.class, EclipseCliente.class,
         LinternaCliente.class, MapaCliente.class, MapaDatos.class, MeteoritosCliente.class, PhoraCliente.class,
         EventosReviil.class, TornadoCliente.class, PantallaAzul.Control.class, RadiationSoundHandler.class
      );
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.attachElementAfter(
         net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements.CHAT,
         net.minecraft.resources.Identifier.fromNamespaceAndPath("ruleta", "hud"),
         (grafico, tiempo) -> Bus.post(new RenderGuiEvent.Post(grafico, tiempo.getGameTimeDeltaPartialTick(false)))
      );
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc -> Bus.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END)));
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register((manejador, emisor, mc) -> Bus.post(new ClientPlayerNetworkEvent.LoggingIn()));
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.JOIN.register((manejador, emisor, mc) -> mc.execute(Precarga::alEntrar));
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(mc -> Precarga.tick());
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((manejador, mc) -> Bus.post(new ClientPlayerNetworkEvent.LoggingOut()));
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register(MapaDatos::alCargarChunk);
   }
}
