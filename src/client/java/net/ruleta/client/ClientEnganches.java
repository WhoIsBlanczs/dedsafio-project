package net.ruleta.client;

import com.supernova.client.ScreenEffectHandler;
import com.supernova.client.SupernovaGhosts;
import com.supernova.client.SupernovaRenderHandler;
import com.supernova.client.SupernovaSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;
import net.ruleta.client.agujero.AgujeroCliente;
import net.ruleta.client.burbuja.BurbujaCliente;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.TickEvent;
import net.ruleta.client.linterna.LinternaCliente;
import net.ruleta.client.meteoritos.MeteoritosCliente;
import net.ruleta.client.phora.PhoraCliente;
import net.ruleta.creeperrojo.client.PantallaAzul;
import net.ruleta.nuclear.client.RadiationSoundHandler;

/** Conecta los eventos de Fabric 26.3 con el bus de eventos del mod. */
public final class ClientEnganches {
   private static boolean hecho;

   private ClientEnganches() {
   }

   public static void registrar() {
      if (hecho) {
         return;
      }
      hecho = true;
      Bus.registrar(
         ScreenEffectHandler.class,
         SupernovaGhosts.class,
         SupernovaRenderHandler.class,
         SupernovaSounds.class,
         AgujeroCliente.class,
         BurbujaCliente.Teclas.class,
         LinternaCliente.class,
         MeteoritosCliente.class,
         PhoraCliente.class,
         PantallaAzul.Control.class,
         RadiationSoundHandler.class
      );
      ClientTickEvents.START_CLIENT_TICK.register(mc -> Bus.post(new TickEvent.ClientTickEvent(TickEvent.Phase.START)));
      ClientTickEvents.END_CLIENT_TICK.register(mc -> Bus.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END)));
      ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> Bus.post(new ClientPlayerNetworkEvent.LoggingOut()));
      HudElementRegistry.addLast(
         Identifier.fromNamespaceAndPath("ruletafanproject", "efectos"),
         (grafico, tiempo) -> Bus.post(new RenderGuiEvent.Post(grafico, tiempo.getGameTimeDeltaPartialTick(false)))
      );
   }
}
