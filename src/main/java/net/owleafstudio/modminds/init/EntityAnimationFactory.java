package net.owleafstudio.modminds.init;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import net.owleafstudio.modminds.entity.BombaAcuaticaGrandeEntity;
import net.owleafstudio.modminds.entity.BombaAcuaticaPequenaEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaEntity;
import net.owleafstudio.modminds.entity.BombaNegraEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaRojaEntity;
import net.owleafstudio.modminds.entity.CandadoTikiEntity;
import net.owleafstudio.modminds.entity.CasinoEntity;
import net.owleafstudio.modminds.entity.ColosoEntity;
import net.owleafstudio.modminds.entity.CreeperCakeEntity;
import net.owleafstudio.modminds.entity.FantasmaAmarilloEntity;
import net.owleafstudio.modminds.entity.FantasmaNegroEntity;
import net.owleafstudio.modminds.entity.FantasmaRojoEntity;
import net.owleafstudio.modminds.entity.FlashBangEntity;
import net.owleafstudio.modminds.entity.GloboEntity;
import net.owleafstudio.modminds.entity.GorgonaEntity;
import net.owleafstudio.modminds.entity.GreamReaperPequenaEntity;
import net.owleafstudio.modminds.entity.GrimReaperEntity;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.InfernalBullEntity;
import net.owleafstudio.modminds.entity.IntrusoEntity;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.entity.MurcielagoWardenEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaRojaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAmarillaAcorazadaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAtomizadoraDeAstrosBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaAzulClaroBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCasitaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaCortaBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaDePulsoBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaPerforadoraDelVacioBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaTanqueBorgEntity;
import net.owleafstudio.modminds.entity.NaveEnemigaTripulsadoraDeIonesBorgEntity;
import net.owleafstudio.modminds.entity.NereidaAguaEntity;
import net.owleafstudio.modminds.entity.NereidaEntity;
import net.owleafstudio.modminds.entity.OjoDeLimboEntity;
import net.owleafstudio.modminds.entity.OrbEntity;
import net.owleafstudio.modminds.entity.PuertaBorgAmarilloEntity;
import net.owleafstudio.modminds.entity.PuertaBorgAzulEntity;
import net.owleafstudio.modminds.entity.PuertaBorgMoradaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgRojaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgRosaEntity;
import net.owleafstudio.modminds.entity.PuertaBorgVerdeEntity;
import net.owleafstudio.modminds.entity.RadicoraEntity;
import net.owleafstudio.modminds.entity.ReactorBorgEntity;
import net.owleafstudio.modminds.entity.ReactorEntity;
import net.owleafstudio.modminds.entity.ReactorHydraEntity;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.SapitoEntity;
import net.owleafstudio.modminds.entity.SapoEntity;
import net.owleafstudio.modminds.entity.SerpienteEntity;
import net.owleafstudio.modminds.entity.SoldadoReviilEntity;
import net.owleafstudio.modminds.entity.SpaceshipEntity;
import net.owleafstudio.modminds.entity.Taxi23DeCuatroAsientosEntity;
import net.owleafstudio.modminds.entity.Taxi23Entity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.entity.TorretaBorgEntity;
import net.owleafstudio.modminds.entity.TorretaTikiEntity;
import net.owleafstudio.modminds.entity.TortugaNetheritaEntity;
import net.owleafstudio.modminds.entity.WardenMounstrosityEntity;
import net.owleafstudio.modminds.entity.WildFireEntity;

@Mod.EventBusSubscriber
public class EntityAnimationFactory {
   @SubscribeEvent
   public static void onEntityTick(LivingEvent.LivingTickEvent event) {
      if (event != null && event.getEntity() != null) {
         if (event.getEntity() instanceof CasinoEntity syncable) {
            String animation = syncable.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncable.setAnimation("undefined");
               syncable.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TortugaNetheritaEntity syncablex) {
            String animation = syncablex.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablex.setAnimation("undefined");
               syncablex.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SapitoEntity syncablexx) {
            String animation = syncablexx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexx.setAnimation("undefined");
               syncablexx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof GorgonaEntity syncablexxx) {
            String animation = syncablexxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxx.setAnimation("undefined");
               syncablexxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaNegraEntity syncablexxxx) {
            String animation = syncablexxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxx.setAnimation("undefined");
               syncablexxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaNegraSinAlasEntity syncablexxxxx) {
            String animation = syncablexxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxx.setAnimation("undefined");
               syncablexxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof InfernalBullEntity syncablexxxxxx) {
            String animation = syncablexxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxx.setAnimation("undefined");
               syncablexxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof MurcielagoWardenEntity syncablexxxxxxx) {
            String animation = syncablexxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxx.setAnimation("undefined");
               syncablexxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof Taxi23Entity syncablexxxxxxxx) {
            String animation = syncablexxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof Taxi23DeCuatroAsientosEntity syncablexxxxxxxxx) {
            String animation = syncablexxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaDePulsoBorgEntity syncablexxxxxxxxxx) {
            String animation = syncablexxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaTanqueBorgEntity syncablexxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaAcorazadaBorgEntity syncablexxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaAtomizadoraDeAstrosBorgEntity syncablexxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaCortaBorgEntity syncablexxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaAmarillaAcorazadaBorgEntity syncablexxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaPerforadoraDelVacioBorgEntity syncablexxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaAcorazadaRojaBorgEntity syncablexxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaTripulsadoraDeIonesBorgEntity syncablexxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaCasitaBorgEntity syncablexxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NaveEnemigaAzulClaroBorgEntity syncablexxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SerpienteEntity syncablexxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SapoEntity syncablexxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FantasmaRojoEntity syncablexxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgVerdeEntity syncablexxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FantasmaAmarilloEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FantasmaNegroEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgAmarilloEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgRosaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgAzulEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgRojaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof PuertaBorgMoradaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaRojaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaAmarillaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CreeperCakeEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof FlashBangEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaAcuaticaGrandeEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof BombaAcuaticaPequenaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof WildFireEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof WardenMounstrosityEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ReactorEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TikiGuardianEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined") && !animation.equals(syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure)) {
               if (syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.level().isClientSide()) {
                  syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               }

               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SoldadoReviilEntity soldado) {
            String animation = soldado.getSyncedAnimation();
            if (!animation.equals("undefined") && !animation.equals(soldado.animationprocedure)) {
               if (soldado.level().isClientSide()) {
                  soldado.setAnimation("undefined");
               }

               soldado.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof GloboEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof HydraEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof LimboTitanEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ReviilEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined") && !animation.equals(syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure)) {
               if (syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.level().isClientSide()) {
                  syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               }

               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NereidaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof NereidaAguaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TorretaTikiEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof OrbEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof AlmaPerdidaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof GrimReaperEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof TorretaBorgEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof SpaceshipEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof RadicoraEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof GreamReaperPequenaEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof OjoDeLimboEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof CandadoTikiEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof IntrusoEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ColosoEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ReactorBorgEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }

         if (event.getEntity() instanceof ReactorHydraEntity syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx) {
            String animation = syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.getSyncedAnimation();
            if (!animation.equals("undefined")) {
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.setAnimation("undefined");
               syncablexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.animationprocedure = animation;
            }
         }
      }
   }
}
