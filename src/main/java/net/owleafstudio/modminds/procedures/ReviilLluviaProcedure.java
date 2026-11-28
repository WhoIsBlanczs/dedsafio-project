package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilLluviaProcedure {
   private static final int COUNT = 12;
   private static final int LEAD_TICKS = 34;
   private static final int STAGGER_TICKS = 10;
   private static final int FALL_TICKS = 12;
   private static final double SPREAD = 13.0;
   private static final double BLAST_RADIUS = 4.5;
   private static final float METEOR_DAMAGE = 16.0F;
   private static final float SCORCH = 4.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.setAnimation("emote.lluvia");
         ReviilFx.rainCharge(serverLevel, reviil);

         for (int i = 0; i < 12; i++) {
            ModmindsMod.queueServerWork(i * 10, () -> {
               if (reviil.isAlive()) {
                  suelta(serverLevel, reviil);
               }
            });
         }

         ModmindsMod.queueServerWork(164, () -> reviil.releasePose());
      }
   }

   private static void suelta(ServerLevel world, ReviilEntity reviil) {
      Vec3 foco = BossFx.aim(world, reviil, 200.0);
      RandomSource random = world.getRandom();
      double angle = random.nextDouble() * Math.PI * 2.0;
      double distance = 1.5 + random.nextDouble() * 13.0;
      Vec3 landing = BossFx.groundAt(world, foco.x + Math.cos(angle) * distance, foco.z + Math.sin(angle) * distance);
      ReviilFx.meteorTelegraph(world, landing, 34);
      ModmindsMod.queueServerWork(22, () -> {
         if (reviil.isAlive()) {
            ReviilFx.meteorFall(world, landing, 12);
         }
      });
      ModmindsMod.queueServerWork(34, () -> {
         if (reviil.isAlive()) {
            meteoritoCae(world, reviil, landing);
         }
      });
   }

   private static void meteoritoCae(ServerLevel world, ReviilEntity reviil, Vec3 landing) {
      Vec3 crater = landing.add(0.0, 1.1, 0.0);
      ReviilFx.meteorImpact(world, landing, 4.5);
      BossFx.shake(world, landing, 48.0, 8, 0.5);
      BossFx.blastDamage(world, world.damageSources().mobAttack(reviil), crater, 4.5, reviil.phaseDamage(16.0F), reviil.phaseDamage(4.0F), 0.9, 0.6, hit -> {
         hit.igniteForSeconds(3.0F);
         reviil.sting(hit);
      });
   }
}
