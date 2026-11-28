package net.ruleta;

import com.supernova.SupernovaAjustes;
import com.supernova.SupernovaCommand;
import com.supernova.SupernovaEvents;
import com.supernova.SupernovaRomper;
import java.util.List;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.ruleta.agujero.ComandoAgujero;
import net.ruleta.candado.CandadoEventos;
import net.ruleta.herobrine.HerobrineAparicion;
import net.ruleta.meteoritos.MomentoMeteoritos;
import net.ruleta.nuclear.HeartEvents;
import net.ruleta.nuclear.NuclearEvents;
import net.ruleta.nuclear.TotemEvents;
import net.ruleta.objetos.Luces;
import net.ruleta.phora.MomentoPhora;

/** Enganches del servidor reducidos al contenido del catálogo (sin ruleta, misiones ni momentos de Reviil). */
public final class Servidor {
   private static volatile MinecraftServer actual;

   private Servidor() {
   }

   public static MinecraftServer actual() {
      return actual;
   }

   public static void registrar() {
      ServerLifecycleEvents.SERVER_STARTING.register(servidor -> actual = servidor);
      ServerLifecycleEvents.SERVER_STARTING.register(Ajustes::alArrancar);
      ServerLifecycleEvents.SERVER_STOPPING.register(Luces::alParar);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentoPhora::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(ComandoAgujero::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(NuclearEvents::onServerStopped);
      ServerLifecycleEvents.SERVER_STOPPED.register(MomentoMeteoritos::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(SupernovaAjustes::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(SupernovaRomper::alApagar);
      ServerLifecycleEvents.SERVER_STOPPED.register(servidor -> actual = null);
      ServerTickEvents.END_SERVER_TICK.register(HerobrineAparicion::alTick);
      ServerTickEvents.END_SERVER_TICK.register(MomentoPhora::alTick);
      ServerTickEvents.END_SERVER_TICK.register(ComandoAgujero::alTick);
      ServerTickEvents.END_SERVER_TICK.register(Luces::alTick);
      ServerTickEvents.END_SERVER_TICK.register(NuclearEvents::onServerTick);
      ServerTickEvents.END_SERVER_TICK.register(MomentoMeteoritos::alTick);
      ServerTickEvents.END_SERVER_TICK.register(SupernovaRomper::alTick);
      ServerTickEvents.END_SERVER_TICK.register(servidor -> {
         for (ServerPlayer jugador : List.copyOf(servidor.getPlayerList().getPlayers())) {
            TotemEvents.onPlayerTick(jugador);
         }
      });
      CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, entorno) -> {
         CandadoEventos.alRegistrarComandos(dispatcher);
         MomentoPhora.alRegistrar(dispatcher);
         ComandoAgujero.alRegistrar(dispatcher);
         MomentoMeteoritos.alRegistrar(dispatcher);
         SupernovaCommand.onRegister(dispatcher);
      });
      ServerPlayConnectionEvents.JOIN.register((handler, sender, servidor) -> {
         ServerPlayer jugador = handler.getPlayer();
         MomentoPhora.alEntrar(jugador);
         ComandoAgujero.alEntrar(jugador);
         HeartEvents.onLogin(jugador);
         MomentoMeteoritos.alEntrar(jugador);
         SupernovaEvents.onLogin(jugador);
      });
      ServerPlayConnectionEvents.DISCONNECT.register((handler, servidor) -> Luces.alSalir(handler.getPlayer()));
      ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((jugador, origen, destino) -> {
         Luces.alCambiarDimension(jugador);
         SupernovaEvents.onDimension(jugador);
      });
      ServerPlayerEvents.AFTER_RESPAWN.register((viejo, nuevo, vivo) -> {
         HeartEvents.onRespawn(nuevo, vivo);
         SupernovaEvents.onRespawn(nuevo, vivo);
      });
      ServerPlayerEvents.COPY_FROM.register((viejo, nuevo, vivo) -> HeartEvents.onClone(viejo, nuevo, !vivo));
   }
}
