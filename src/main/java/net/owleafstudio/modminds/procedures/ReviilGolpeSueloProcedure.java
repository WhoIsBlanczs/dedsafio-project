package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilGolpeSueloProcedure {
   private static final int WINDUP_TICKS = 55;
   private static final int IMPACT_TICKS = 10;
   private static final int GOLPE_ADELANTO = 18;
   private static final int PUNCH_TICKS = 48;
   private static final double RADIUS = 16.0;
   private static final float PEAK_DAMAGE = 30.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel reach = BossFx.server(world);
         if (reach instanceof ServerLevel) {
            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            double var9 = reviil.phaseBlastRadius(16.0);
            boolean lanzado = reviil.isGiant();
            Vec3 centro = lanzado ? BossFx.aim(reach, reviil, 200.0) : BossFx.ground(reviil);
            int adelanto = lanzado ? 18 : 10;
            reviil.setAnimation(lanzado ? "emote.giant.dosbrazos" : "emote.puno.carga");
            if (lanzado) {
               BossFx.miraA(reviil, centro);
               ReviilFx.groundPoundChargeAt(reach, reviil, centro, var9, 45);
            } else {
               ReviilFx.groundPoundCharge(reach, reviil, var9, 55);
            }

            ModmindsMod.queueServerWork(55 - adelanto, () -> {
               if (reviil.isAlive()) {
                  reviil.setAnimation(lanzado ? "emote.giant.dosbrazos.golpe" : "emote.puno.baja");
                  if (lanzado) {
                     BossFx.golpeViaja(reach, BossFx.ground(reviil), centro, adelanto, () -> {
                        if (reviil.isAlive()) {
                           revienta(reach, reviil, centro, var9, lanzado);
                        }
                     });
                  } else {
                     ModmindsMod.queueServerWork(adelanto, () -> {
                        if (reviil.isAlive()) {
                           revienta(reach, reviil, BossFx.ground(reviil), var9, lanzado);
                        }
                     });
                  }

                  ModmindsMod.queueServerWork(48, () -> {
                     if (reviil.isAlive()) {
                        reviil.releasePose();
                     }
                  });
               }
            });
            return;
         }
      }
   }

   private static void revienta(ServerLevel world, ReviilEntity reviil, Vec3 at, double reach, boolean lanzado) {
      if (lanzado) {
         ReviilFx.groundPoundImpactAt(world, reviil, at, reach);
      } else {
         ReviilFx.groundPoundImpact(world, reviil, reach);
      }

      BossFx.blastDamage(
         world, world.damageSources().sonicBoom(reviil), at, reach, reviil.phaseDamage(30.0F), reviil.phaseDamage(3.0F), 1.35, 1.1, reviil::sting
      );
   }
}
