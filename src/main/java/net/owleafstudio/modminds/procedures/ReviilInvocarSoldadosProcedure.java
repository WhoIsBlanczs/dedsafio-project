package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.SoldadoReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilInvocarSoldadosProcedure {
   private static final int SOLDADOS = 4;
   private static final int AVISO_TICKS = 28;
   private static final int ESCALON_TICKS = 9;
   private static final double RADIO_MIN = 10.0;
   private static final double RADIO_MAX = 23.0;
   private static final int RISE_TICKS = 24;
   private static final double SEARCH = 64.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         ServerLevel faltan = BossFx.server(world);
         if (faltan instanceof ServerLevel) {
            ServerLevel level = faltan;
            ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (!reviil.isGiant()) {
               BossFx.subtitle(faltan, BossFx.centre(reviil), 140.0, "REVIIL SOLO LLAMA A SUS SOLDADOS SIENDO GIGANTE", 50);
               return;
            }

            if (reviil.escenaTicks > 0) {
               BossFx.subtitle(faltan, BossFx.centre(reviil), 140.0, "REVIIL NO LLAMA A NADIE DURANTE LA ESCENA", 46);
               return;
            }

            int faltanx = 4 - count(faltan, reviil);
            if (faltanx <= 0) {
               BossFx.subtitle(faltan, BossFx.centre(reviil), 140.0, "SUS SOLDADOS SIGUEN EN LA ARENA", 46);
               return;
            }

            BossFx.alert(faltan, BossFx.centre(reviil), 160.0, "⚠ REVIIL: LLAMA A SUS SOLDADOS");
            BossFx.sound(faltan, BossFx.centre(reviil), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 2.2F, 0.42F);
            reviil.setAnimation("emote.giant.llamada");
            Vec3 from = BossFx.ground(reviil);
            LivingEntity objetivo = reviil.getTarget();
            double giro = faltan.getRandom().nextDouble() * Math.PI * 2.0;

            for (int slot = 0; slot < 4; slot++) {
               double angle = giro + slot * Math.PI * 2.0 / 4.0 + (level.getRandom().nextDouble() - 0.5) * 0.7;
               double radio = 10.0 + 13.0 * level.getRandom().nextDouble();
               Vec3 at = surface(level, from.x + Math.cos(angle) * radio, from.z + Math.sin(angle) * radio);
               boolean first = slot == 0;
               BossFx.marker(level, at, 2.6, 28 + slot * 9);
               ModmindsMod.queueServerWork(28 + slot * 9, () -> {
                  if (reviil.isAlive()) {
                     erupt(level, at, objetivo, first);
                  }
               });
            }

            ModmindsMod.queueServerWork(79, () -> {
               if (reviil.isAlive()) {
                  reviil.releasePose();
               }
            });
            return;
         }
      }
   }

   private static void erupt(ServerLevel world, Vec3 at, LivingEntity target, boolean first) {
      SoldadoReviilEntity soldado = (SoldadoReviilEntity)ModmindsModEntities.SOLDADO_REVIIL.get().create(world);
      if (soldado != null) {
         soldado.setPos(at.x, at.y + 0.05, at.z);
         soldado.riseTicks = 24;
         soldado.setAnimation("animation.SoldadoReviil.rise");
         world.addFreshEntity(soldado);
         BossFx.smashSmall(world, at, 3.6, first ? 0.3 : 0.14, BossFx.ALERT_DOT);
         BossFx.groundSwell(world, at, 3.2, 16);
         BossFx.burst(world, at.add(0.0, 0.9, 0.0), ParticleTypes.ASH, 10, 0.7, 0.12);
         BossFx.sound(world, at, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 1.6F, 0.7);
         BossFx.sound(world, at, SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.0F, 0.6);
         if (first) {
            BossFx.shake(world, at, 42.0, 12, 0.3);
         }

         if (target != null) {
            soldado.setTarget(target);
         }

         ModmindsMod.queueServerWork(28, () -> {
            if (soldado.isAlive()) {
               soldado.releasePose();
            }
         });
      }
   }

   private static int count(ServerLevel world, ReviilEntity reviil) {
      Vec3 at = reviil.position();
      return world.getEntitiesOfClass(SoldadoReviilEntity.class, new AABB(at, at).inflate(64.0), LivingEntity::isAlive).size();
   }

   private static Vec3 surface(ServerLevel world, double x, double z) {
      return new Vec3(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z)), z);
   }
}
