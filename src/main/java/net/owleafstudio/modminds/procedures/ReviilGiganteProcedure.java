package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
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

public class ReviilGiganteProcedure {
   private static final int FALL_TICKS = 48;
   private static final int FALL_IMPACT_TICKS = 14;
   private static final int RAMP_STEPS = 10;
   private static final int RAMP_STEP_TICKS = 4;
   private static final int LAND_TICKS = 40;
   private static final int CLIP_TICKS = 52;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel var4 = BossFx.server(world);
         if (var4 instanceof ServerLevel && reviil.isAlive()) {
            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (reviil.escenaTicks > 0) {
               BossFx.subtitle(var4, BossFx.centre(reviil), 140.0, "REVIIL NO CAMBIA DE FASE DURANTE LA ESCENA", 50);
               return;
            }

            if (!reviil.enterGiant()) {
               BossFx.subtitle(var4, BossFx.centre(reviil), 140.0, "REVIIL YA ES GIGANTE", 46);
               return;
            }

            reviil.setAnimation("emote.giant.fall");
            BossFx.sound(var4, BossFx.centre(reviil), SoundEvents.WARDEN_HURT, SoundSource.HOSTILE, 3.0F, 0.5);
            ModmindsMod.queueServerWork(14, () -> {
               if (reviil.isAlive()) {
                  collapse(var4, reviil);
               }
            });
            ModmindsMod.queueServerWork(48, () -> {
               if (reviil.isAlive()) {
                  grow(var4, reviil);
               }
            });
            return;
         }
      }
   }

   private static void collapse(ServerLevel level, ReviilEntity reviil) {
      Vec3 at = BossFx.groundAt(level, reviil.getX(), reviil.getZ());
      BossFx.shake(level, BossFx.centre(reviil), 150.0, 20, 0.9);
      BossFx.blastSound(level, at, 1.1);
      BossFx.sound(level, at, SoundEvents.STONE_FALL, SoundSource.BLOCKS, 2.0F, 0.6F);
      BossFx.groundSwell(level, at, reviil.getBbWidth() * 1.6, 22);
      BossFx.groundCrack(level, at, reviil.getBbWidth() * 2.2, 90);
   }

   private static void grow(ServerLevel level, ReviilEntity reviil) {
      float from = reviil.getScale();
      reviil.setAnimation("emote.giant.enter");
      ReviilFx.giantEnter(level, reviil, 0, 10);

      for (int step = 1; step <= 10; step++) {
         int at = step;
         ModmindsMod.queueServerWork(at * 4, () -> {
            if (reviil.isAlive()) {
               double travel = at / 10.0;
               double eased = travel * travel * (3.0 - 2.0 * travel);
               reviil.setSize((float)(from + (3.4F - from) * eased));
               ReviilFx.giantEnter(level, reviil, at, 10);
            }
         });
      }

      ModmindsMod.queueServerWork(40, () -> {
         if (reviil.isAlive()) {
            ReviilFx.giantLand(level, reviil);
         }
      });
      ModmindsMod.queueServerWork(52, () -> {
         if (reviil.isAlive()) {
            reviil.releasePose();
         }
      });
   }
}
