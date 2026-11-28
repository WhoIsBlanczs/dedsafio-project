package net.owleafstudio.modminds.procedures;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
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

public class ReviilCirculosRayosProcedure {
   private static final int CIRCULOS = 6;
   private static final double RADIO_ANILLO = 13.0;
   private static final double RADIO_CIRCULO = 4.6;
   private static final double ALTO_RAYOS = 11.0;
   private static final int AVISO_TICKS = 60;
   private static final int[] DESCARGAS = new int[]{34, 48, 60};
   private static final double DAÑO = 22.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 centro = BossFx.aim(level, reviil, 200.0);
            BossFx.miraA(reviil, centro);
            reviil.setAnimation("emote.giant.rayos");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.alert(level, centro, 170.0, "⚠ REVIIL TIENDE LA JAULA");
            BossFx.sound(level, centro, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.6F, 1.5);
            List<Vec3> circulos = BossFx.rayRing(level, centro, 13.0, 6, 4.6, 11.0, 60);
            BossFx.rayCircle(level, centro, 4.6, 11.0, 60);
            circulos.add(centro);

            for (Vec3 at : circulos) {
               BossFx.groundCrack(level, at, 7.0, 100);
               BossFx.burst(level, at.add(0.0, 0.9, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 2, 3.2199999999999998, 0.02);
            }

            for (int descarga : DESCARGAS) {
               ModmindsMod.queueServerWork(descarga, () -> {
                  if (reviil.isAlive()) {
                     snap(level, reviil, centro, circulos, descarga == DESCARGAS[DESCARGAS.length - 1]);
                  }
               });
            }

            ModmindsMod.queueServerWork(90, () -> reviil.releasePose());
         }
      }
   }

   private static void snap(ServerLevel world, ReviilEntity reviil, Vec3 centro, List<Vec3> circulos, boolean ultima) {
      BossFx.sound(world, centro, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, ultima ? 2.6F : 1.7F, 1.9 + world.getRandom().nextDouble() * 0.2);
      BossFx.shake(world, centro, 130.0, ultima ? 16 : 8, ultima ? 0.55 : 0.28);

      for (Vec3 at : circulos) {
         BossFx.shockwave(world, at, 10.12, 8, 11824127);
         BossFx.burst(world, at.add(0.0, 1.1, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), ultima ? 3 : 1, 2.76, 0.05);
         BossFx.burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, 1.8399999999999999, 0.07);

         for (ServerPlayer sp : world.getEntitiesOfClass(
            ServerPlayer.class, new AABB(at, at).inflate(6.6), p -> p.isAlive() && !p.isSpectator() && !p.isCreative() && !p.isPassenger()
         )) {
            if (!(sp.getEyePosition().distanceTo(at) > 4.6 + sp.getBbWidth() * 0.5)) {
               sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(22.0F));
               reviil.sting(sp);
               Vec3 fuera = sp.position().subtract(at);
               double lado = Math.max(1.0E-4, Math.hypot(fuera.x, fuera.z));
               sp.setDeltaMovement(fuera.x / lado * 0.9, 0.75, fuera.z / lado * 0.9);
               sp.needsSync = true;
               sp.igniteForSeconds(ultima ? 5.0F : 2.0F);
            }
         }
      }
   }
}
