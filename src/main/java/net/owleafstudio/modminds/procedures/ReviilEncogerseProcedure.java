package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilEncogerseProcedure {
   private static final int RAMP_STEPS = 10;
   private static final int RAMP_STEP_TICKS = 4;
   private static final int LAND_TICKS = 40;
   private static final int CLIP_TICKS = 42;
   private static final int DOBLON_TICKS = 22;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel holdsSomeone = BossFx.server(world);
         if (holdsSomeone instanceof ServerLevel) {
            ServerLevel level = holdsSomeone;
            if (reviil.isAlive()) {
               ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
               ModmindsModVariables.MapVariables.get(world).syncData(world);
               if (reviil.escenaTicks > 0) {
                  BossFx.subtitle(holdsSomeone, BossFx.centre(reviil), 140.0, "REVIIL NO CAMBIA DE FASE DURANTE LA ESCENA", 50);
                  return;
               }

               if (!reviil.exitGiant()) {
                  BossFx.subtitle(holdsSomeone, BossFx.centre(reviil), 140.0, "REVIIL NO ES GIGANTE", 46);
                  return;
               }

               reviil.ruletaHasta = 0L;
               boolean holdsSomeonex = reviil.grabBeat() > 0;
               reviil.setAnimation("emote.giant.encoge");
               BossFx.alert(holdsSomeone, BossFx.centre(reviil), 160.0, "⚠ REVIIL SE HACE PEQUEÑO");
               ReviilFx.giantLeave(holdsSomeone, reviil, 0, 10);
               float to = reviil.phaseScale();

               for (int step = 1; step <= 10; step++) {
                  int at = step;
                  ModmindsMod.queueServerWork(at * 4, () -> {
                     if (reviil.isAlive()) {
                        double travel = at / 10.0;
                        double eased = travel * travel * (3.0 - 2.0 * travel);
                        reviil.setSize((float)(3.4F - (3.4F - to) * eased));
                        ReviilFx.giantLeave(level, reviil, at, 10);
                     }
                  });
               }

               ModmindsMod.queueServerWork(22, () -> {
                  if (reviil.isAlive()) {
                     Vec3 ground = BossFx.groundAt(level, reviil.getX(), reviil.getZ());
                     BossFx.sound(level, ground, SoundEvents.GRAVEL_FALL, SoundSource.BLOCKS, 1.8F, 0.6);
                     BossFx.vortex(level, ground, reviil.getBbWidth() * 2.4, 26, BossFx.DUST_PILLAR, 0.14);
                     BossFx.shake(level, BossFx.centre(reviil), 150.0, 8, 0.3);
                  }
               });
               ModmindsMod.queueServerWork(40, () -> {
                  if (reviil.isAlive()) {
                     ReviilFx.giantLeaveLand(level, reviil);
                     if (holdsSomeonex) {
                        abreLaMano(level, reviil);
                     }
                  }
               });
               ModmindsMod.queueServerWork(42, () -> {
                  if (reviil.isAlive()) {
                     reviil.releasePose();
                  }
               });
               return;
            }
         }
      }
   }

   private static void abreLaMano(ServerLevel level, ReviilEntity reviil) {
      double yaw = Math.toRadians(reviil.getYRot());
      double forward = reviil.getBbWidth() * 0.5 + 1.2;
      Vec3 suelo = BossFx.groundAt(level, reviil.getX() - Math.sin(yaw) * forward, reviil.getZ() + Math.cos(yaw) * forward);

      for (Entity passenger : reviil.getPassengers()) {
         if (passenger instanceof Player player) {
            player.stopRiding();
            player.teleportTo(suelo.x, suelo.y + 0.2, suelo.z);
            player.resetFallDistance();
            BossFx.subtitle(level, BossFx.centre(player), 32.0, "REVIIL ABRE LA MANO", 50);
         }
      }
   }
}
