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

   public static void registrar() { }

   private static void lanzar(WorldRenderContext ctx, RenderLevelStageEvent.Stage etapa) {
      if (Bus.hayOyentes(RenderLevelStageEvent.class) && ctx.matrixStack() != null) {
         Bus.post(
            new RenderLevelStageEvent(
               etapa, ctx.matrixStack(), ctx.positionMatrix(), ctx.projectionMatrix(), ctx.camera(), ctx.tickCounter().getGameTimeDeltaPartialTick(false)
            )
         );
      }
   }
}
