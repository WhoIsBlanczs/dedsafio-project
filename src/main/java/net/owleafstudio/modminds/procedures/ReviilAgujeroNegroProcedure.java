package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAgujeroNegroProcedure {
   private static final double RADIO = 22.0;
   private static final double GARGANTA = 7.0;
   private static final int AVISO_TICKS = 30;
   private static final int SUCCION_TICKS = 92;
   private static final int PASO_TICKS = 2;
   private static final double TIRÓN = 0.24;
   private static final double DAÑO_EXPLOSIÓN = 34.0;
   private static final int BLANCO_TICKS = 46;
   private static final int ZUMBIDO_TICKS = 240;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 at = BossFx.aim(level, reviil, 200.0);
            BossFx.miraA(reviil, at);
            reviil.setAnimation("emote.giant.hoyo");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.alert(level, at, 170.0, "⚠ REVIIL ABRE UN HOYO EN LA ARENA");
            BossFx.sound(level, at, SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 2.4F, 0.5);
            BossFx.warningAt(level, at, 22.0, 30);
            ModmindsMod.queueServerWork(30, () -> {
               if (reviil.isAlive()) {
                  BossFx.golpeViaja(level, BossFx.ground(reviil), at, 12, () -> abre(level, reviil, at));
               }
            });
         }
      }
   }

   private static void abre(ServerLevel world, ReviilEntity reviil, Vec3 at) {
      if (reviil.isAlive()) {
         BossFx.blackHole(world, at, 22.0, 7.0, 116);
         BossFx.burst(world, at.add(0.0, 1.0, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 4, 16.5, 0.02);
         BossFx.shake(world, at, 120.0, 20, 0.28);
         BossFx.sound(world, at, SoundEvents.PORTAL_TRAVEL, SoundSource.HOSTILE, 2.2F, 0.35);
         pull(world, reviil, at, 0);
      }
   }

   private static void pull(ServerLevel world, ReviilEntity reviil, Vec3 at, int tick) {
      if (reviil.isAlive()) {
         if (tick % 20 < 2) {
            BossFx.sound(world, at, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 1.3F, 0.45);
         }

         for (ServerPlayer sp : world.players()) {
            if (sp.isAlive() && !sp.isSpectator() && !sp.isCreative() && !sp.isPassenger()) {
               Vec3 offset = at.subtract(sp.position());
               double reach = Math.hypot(offset.x, offset.z);
               if (!(reach > 22.0) && !(reach < 0.6)) {
                  double fuerza = 0.24 * (1.25 - 0.55 * reach / 22.0);
                  Vec3 movimiento = sp.getDeltaMovement();
                  sp.setDeltaMovement(
                     movimiento.x + offset.x / reach * fuerza,
                     movimiento.y + (tick % 6 == 0 ? 0.06 : 0.0) - 0.01,
                     movimiento.z + offset.z / reach * fuerza
                  );
                  sp.needsSync = true;
                  sp.fallDistance = 0.0F;
                  if (reach < 2.6) {
                     sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(3.0F));
                  }
               }
            }
         }

         if (tick >= 92) {
            detonate(world, reviil, at);
         } else {
            ModmindsMod.queueServerWork(2, () -> pull(world, reviil, at, tick + 2));
         }
      }
   }

   private static void detonate(ServerLevel world, ReviilEntity reviil, Vec3 at) {
      BossFx.shockwave(world, at, 33.0, 16, 8010728);
      BossFx.burst(world, at.add(0.0, 1.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 5, 15.399999999999999, 0.05);
      BossFx.burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, 8.8, 0.07);
      BossFx.shake(world, at, 150.0, 30, 0.9);
      BossFx.sound(world, at, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 2.8F, 0.6);
      BossFx.groundCrack(world, at, 30.0, 120);
      BossFx.groundCrack(world, at, 22.0, 120);

      for (ServerPlayer sp : world.players()) {
         if (sp.isAlive() && !sp.isSpectator()) {
            double reach = sp.position().distanceTo(at);
            if (!(reach > 33.0) && reach < 22.0) {
               sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(34.0F));
               reviil.sting(sp);
            }
         }
      }

      BossFx.blastDamage(world, world.damageSources().mobAttack(reviil), at, 33.0, reviil.phaseDamage(12.0F), 2.0F, 1.6, 0.85, reviil::sting);
      BossFx.flashbang(world, at, 48.400000000000006, 46, 240);
      ModmindsMod.queueServerWork(34, () -> reviil.releasePose());
   }
}
