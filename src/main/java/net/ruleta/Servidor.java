package net.ruleta;

import com.supernova.SupernovaAjustes;
import com.supernova.SupernovaCommand;
import com.supernova.SupernovaEvents;
import com.supernova.SupernovaRomper;
import java.util.List;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents.AfterPlayerChange;
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

   public static void registrar() { }
}
