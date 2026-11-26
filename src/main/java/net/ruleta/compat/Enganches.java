package net.ruleta.compat;

import com.supernova.SupernovaEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.Unload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.Before;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.StartTracking;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;
import net.ruleta.CambiosEfectos;
import net.ruleta.MomentosBunker;
import net.ruleta.Muertes;
import net.ruleta.OndasExplosion;
import net.ruleta.Ruleta;
import net.ruleta.bunker.Bunkers;
import net.ruleta.candado.CandadoEventos;
import net.ruleta.cosmeticos.CosmeticosServidor;
import net.ruleta.creeperes.MobsNuevos;
import net.ruleta.creeperrojo.CreeperRojo;
import net.ruleta.mobs.BombaWarden;
import net.ruleta.nuclear.NuclearEvents;
import net.ruleta.nuclear.TotemEvents;

public final class Enganches {
   private static boolean hecho;

   private Enganches() {
   }

   private static InteractionResult resultado(PlayerInteractEvent ev) {
      InteractionResult r = ev.getCancellationResult();
      return r == InteractionResult.PASS ? InteractionResult.FAIL : r;
   }

   public static synchronized void registrar() {
      if (!hecho) {
         hecho = true;
         Bus.registrar(
            SupernovaEvents.class,
            CambiosEfectos.class,
            MomentosBunker.class,
            Muertes.class,
            OndasExplosion.class,
            Ruleta.Eventos.class,
            Bunkers.class,
            CandadoEventos.class,
            CosmeticosServidor.class,
            MobsNuevos.class,
            CreeperRojo.Eventos.class,
            BombaWarden.Eventos.class,
            NuclearEvents.class,
            TotemEvents.class
         );
         ServerEntityEvents.ENTITY_UNLOAD.register((Unload)(entidad, nivel) -> {
            if (Bus.hayOyentes(EntityLeaveLevelEvent.class)) {
               Bus.post(new EntityLeaveLevelEvent(entidad, nivel));
            }
         });
         UseEntityCallback.EVENT.register((UseEntityCallback)(jugador, nivel, mano, objetivo, golpe) -> {
            if (!Bus.hayOyentes(PlayerInteractEvent.EntityInteractSpecific.class) && !Bus.hayOyentes(PlayerInteractEvent.EntityInteract.class)) {
               return InteractionResult.PASS;
            }

            Vec3 local = golpe != null ? golpe.getLocation().subtract(objetivo.position()) : Vec3.ZERO;
            PlayerInteractEvent.EntityInteractSpecific especifico = new PlayerInteractEvent.EntityInteractSpecific(jugador, mano, objetivo, local);
            if (Bus.post(especifico)) {
               return resultado(especifico);
            }

            PlayerInteractEvent.EntityInteract normal = new PlayerInteractEvent.EntityInteract(jugador, mano, objetivo);
            return Bus.post(normal) ? resultado(normal) : InteractionResult.PASS;
         });
         UseBlockCallback.EVENT.register((UseBlockCallback)(jugador, nivel, mano, golpe) -> {
            if (!nivel.isClientSide()) {
               ItemBloqueado.limpiar();
            }

            if (!Bus.hayOyentes(PlayerInteractEvent.RightClickBlock.class)) {
               return InteractionResult.PASS;
            }

            PlayerInteractEvent.RightClickBlock ev = new PlayerInteractEvent.RightClickBlock(jugador, mano, golpe, nivel);
            if (Bus.post(ev)) {
               return resultado(ev);
            }

            if (ev.getUseItem() == Evento.Result.DENY && !nivel.isClientSide()) {
               ItemBloqueado.marcar(jugador.getUUID());
            }

            return InteractionResult.PASS;
         });
         ServerTickEvents.END_SERVER_TICK.register((EndTick)servidor -> ItemBloqueado.limpiar());
         AttackBlockCallback.EVENT.register((AttackBlockCallback)(jugador, nivel, mano, pos, cara) -> {
            if (!Bus.hayOyentes(PlayerInteractEvent.LeftClickBlock.class)) {
               return InteractionResult.PASS;
            }

            PlayerInteractEvent.LeftClickBlock ev = new PlayerInteractEvent.LeftClickBlock(jugador, mano, pos, cara, nivel);
            return Bus.post(ev) ? resultado(ev) : InteractionResult.PASS;
         });
         PlayerBlockBreakEvents.BEFORE
            .register(
               (Before)(nivel, jugador, pos, estado, entidadBloque) -> !Bus.hayOyentes(BlockEvent.BreakEvent.class)
                  ? true
                  : !Bus.post(new BlockEvent.BreakEvent(nivel, pos, estado, jugador))
            );
         EntityTrackingEvents.START_TRACKING.register((StartTracking)(objetivo, jugador) -> {
            if (Bus.hayOyentes(PlayerEvent.StartTracking.class)) {
               Bus.post(new PlayerEvent.StartTracking(jugador, objetivo));
            }
         });
      }
   }
}
