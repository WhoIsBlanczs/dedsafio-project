package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class RayoDeVacioReviilProcedure {
   private static final int CHARGE_TICKS = 30;
   private static final int SWEEP_TICKS = 70;
   private static final double RANGE = 45.0;
   private static final double ALCANCE_MAX = 120.0;
   private static final double SWEEP_ARC = Math.PI * 2.0 / 3.0;
   private static final float DAMAGE = 9.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         Player objetivo = BossFx.objetivo(serverLevel, reviil, 132.0);
         if (objetivo != null) {
            reviil.setAnimation("emote.rayo");
            double angle = Math.atan2(objetivo.getZ() - reviil.getZ(), objetivo.getX() - reviil.getX());
            double height = objetivo.getY() + objetivo.getBbHeight() * 0.5;
            double largo = Math.min(120.0, Math.max(45.0, BossFx.distanciaPlana(BossFx.ground(reviil), objetivo.position()) + 16.0));
            BossFx.miraA(reviil, objetivo.position());
            ReviilFx.voidBeamCharge(serverLevel, reviil, angle, largo, Math.PI * 2.0 / 3.0, 30);
            Set<UUID> burned = new HashSet<>();

            for (int tick = 0; tick < 70; tick++) {
               int step = tick;
               ModmindsMod.queueServerWork(30 + tick, () -> sweepFrame(serverLevel, reviil, angle, height, largo, step, burned));
            }

            ModmindsMod.queueServerWork(140, () -> {
               if (reviil.isAlive()) {
                  reviil.releasePose();
               }
            });
         } else if (reviil.isGiant()) {
            BossFx.subtitle(serverLevel, BossFx.centre(reviil), 140.0, "REVIIL NO LO VE DESDE AHÍ", 44);
         }
      }
   }

   private static void sweepFrame(ServerLevel world, ReviilEntity reviil, double lockOn, double height, double largo, int step, Set<UUID> burned) {
      if (reviil.isAlive()) {
         Vec3 origin = BossFx.centre(reviil);
         double angle = lockOn + (step / 70.0 - 0.5) * (Math.PI * 2.0 / 3.0);
         Vec3 end = new Vec3(reviil.getX() + Math.cos(angle) * largo, height, reviil.getZ() + Math.sin(angle) * largo);
         if (step == 0) {
            ReviilFx.voidBeamRelease(world, reviil);
         }

         ReviilFx.voidBeamFrame(world, origin, end);

         for (Player hit : world.getEntitiesOfClass(Player.class, new AABB(origin, end).inflate(3.0), LivingEntity::isAlive)) {
            if (burned.add(hit.getUUID()) && BossFx.distanceToSegment(BossFx.centre(hit), origin, end) < hit.getBbWidth() * 0.5 + 1.6) {
               hit.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(9.0F));
               hit.setDeltaMovement(hit.getDeltaMovement().add(0.0, 0.35, 0.0));
               hit.igniteForSeconds(5.0F);
            }
         }
      }
   }
}
