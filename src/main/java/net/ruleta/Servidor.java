package net.ruleta;

import com.supernova.SupernovaAjustes;
import com.supernova.SupernovaCommand;
import com.supernova.SupernovaEvents;
import com.supernova.SupernovaRomper;
import java.util.List;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AfterPlayerChange;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.CopyFrom;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.Before;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.agujero.ComandoAgujero;
import net.ruleta.bunker.Bunkers;
import net.ruleta.candado.CandadoEventos;
import net.ruleta.cosmeticos.CosmeticosServidor;
import net.ruleta.dedsafio.ComandoDedsafio;
import net.ruleta.dedsafio.EscenaServidor;
import net.ruleta.herobrine.HerobrineAparicion;
import net.ruleta.meteoritos.MomentoMeteoritos;
import net.ruleta.nuclear.HeartEvents;
import net.ruleta.nuclear.NuclearEvents;
import net.ruleta.nuclear.TotemEvents;
import net.ruleta.objetos.Luces;
import net.ruleta.phora.MomentoPhora;
import net.ruleta.tornado.ComandoTornado;

public final class Servidor {
   private static volatile MinecraftServer actual;

   private Servidor() {
   }

   public static MinecraftServer actual() {
      return actual;
   }

   public static void registrar() {
      EscenaServidor.registrar();
      ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)servidor -> actual = servidor);
      ServerLifecycleEvents.SERVER_STARTING.register(Ajustes::alArrancar);
      ServerLifecycleEvents.SERVER_STOPPING.register(Luces::alParar);
      ServerLifecycleEvents.SERVER_STOPPED.register(ComandoEclipse::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentosBunker::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentosReviil::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(Ruleta.Eventos::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(Sanciones::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentoPhora::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(ComandoAgujero::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(NuclearEvents::onServerStopped);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentoMeteoritos::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(Bunkers::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(ComandoTornado::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(SupernovaAjustes::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(SupernovaRomper::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)servidor -> actual = null);
      ServerTickEvents.END_SERVER_TICK.register(ComandoEclipse::alTick);
      ServerTickEvents.END_SERVER_TICK.register(MomentosReviil::alTick);
      ServerTickEvents.END_SERVER_TICK.register(Ruleta.Eventos::alTick);
      ServerTickEvents.END_SERVER_TICK.register(Sanciones::alTick);
      ServerTickEvents.END_SERVER_TICK.register(HerobrineAparicion::alTick);
      ServerTickEvents.END_SERVER_TICK.register(MomentoPhora::alTick);
      ServerTickEvents.END_SERVER_TICK.register(ComandoAgujero::alTick);
      ServerTickEvents.END_SERVER_TICK.register(Luces::alTick);
      ServerTickEvents.END_SERVER_TICK.register(NuclearEvents::onServerTick);
      ServerTickEvents.END_SERVER_TICK.register(MomentoMeteoritos::alTick);
      ServerTickEvents.END_SERVER_TICK.register(Bunkers::alTickServidor);
      ServerTickEvents.END_SERVER_TICK.register(ComandoTornado::alTick);
      ServerTickEvents.END_SERVER_TICK.register(SupernovaRomper::alTick);
      ServerTickEvents.END_SERVER_TICK.register((EndTick)servidor -> {
         for (ServerPlayer jugador : List.copyOf(servidor.getPlayerList().getPlayers())) {
            CambiosEfectos.alTickJugador(jugador);
            Ruleta.Eventos.alTickJugador(jugador);
            TotemEvents.onPlayerTick(jugador);
            CosmeticosServidor.alTick(jugador);
            Bunkers.alTick(jugador);
         }
      });
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, entorno) -> {
         ComandoEclipse.alRegistrar(dispatcher);
         ComandoReviil.alRegistrar(dispatcher);
         ComandoDedsafio.alRegistrar(dispatcher);
         ComandoTemporizador.alRegistrar(dispatcher);
         Ruleta.alRegistrarComandos(dispatcher);
         CandadoEventos.alRegistrarComandos(dispatcher);
         MomentoPhora.alRegistrar(dispatcher);
         ComandoAgujero.alRegistrar(dispatcher);
         MomentoMeteoritos.alRegistrar(dispatcher);
         CosmeticosServidor.alRegistrarComandos(dispatcher);
         Bunkers.alRegistrar(dispatcher);
         ComandoTornado.alRegistrar(dispatcher);
         SupernovaCommand.onRegister(dispatcher);
      });
      PlayerBlockBreakEvents.BEFORE
         .register((Before)(nivel, jugador, pos, estado, bloqueEntidad) -> !Proteccion.protegido(estado.getBlock()) || Proteccion.puede(jugador));
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, servidor) -> {
         ServerPlayer jugador = handler.getPlayer();
         Ajustes.alEntrar(jugador);
         ComandoEclipse.alEntrar(jugador);
         MomentosReviil.alEntrar(jugador);
         Ruleta.Eventos.alEntrar(jugador);
         MomentoPhora.alEntrar(jugador);
         ComandoAgujero.alEntrar(jugador);
         HeartEvents.onLogin(jugador);
         MomentoMeteoritos.alEntrar(jugador);
         Bunkers.alEntrar(jugador);
         ComandoTornado.alEntrar(jugador);
         SupernovaEvents.onLogin(jugador);
      });
      ServerPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, servidor) -> {
         ServerPlayer jugador = handler.getPlayer();
         CambiosEfectos.alDesconectarse(jugador);
         Luces.alSalir(jugador);
         CosmeticosServidor.alSalir(jugador);
      });
      ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((AfterPlayerChange)(jugador, origen, destino) -> {
         Luces.alCambiarDimension(jugador);
         SupernovaEvents.onDimension(jugador);
      });
      ServerPlayerEvents.AFTER_RESPAWN.register((AfterRespawn)(viejo, nuevo, vivo) -> {
         HeartEvents.onRespawn(nuevo, vivo);
         SupernovaEvents.onRespawn(nuevo, vivo);
      });
      ServerPlayerEvents.COPY_FROM.register((CopyFrom)(viejo, nuevo, vivo) -> {
         HeartEvents.onClone(viejo, nuevo, !vivo);
         CosmeticosServidor.alClonar(viejo, nuevo, !vivo);
      });
   }
}
