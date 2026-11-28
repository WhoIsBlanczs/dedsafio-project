package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanQuiebraProcedure {
   private static final int SWEEPS = 2;
   private static final int LEAD_TICKS = 38;
   private static final int NODES = 13;
   private static final int NODE_TICKS = 3;
   private static final int STAGGER_TICKS = 103;
   private static final double HALF = 26.0;
   private static final double WIDTH = 4.5;
   private static final float DAMAGE = 55.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: FALLA");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         BossFx.sound(serverLevel, BossFx.centre(titan), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.6F, 0.6);
         LivingEntity target = titan.getTarget();
         Vec3 focus = target != null ? target.position() : titan.position();
         double base = Math.atan2(focus.z - titan.getZ(), focus.x - titan.getX());

         for (int sweep = 0; sweep < 2; sweep++) {
            double angle = base + sweep * Math.PI * 0.5;
            Vec3 line = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            int at = sweep * 103;
            ModmindsMod.queueServerWork(at, () -> {
               if (titan.isAlive()) {
                  traceFault(serverLevel, titan, line);
               }
            });
         }
      }
   }

   private static void traceFault(ServerLevel world, LimboTitanEntity titan, Vec3 direction) {
      Vec3 anchor = BossFx.ground(titan);
      Vec3 from = surface(world, anchor.subtract(direction.scale(26.0)));
      Vec3 to = surface(world, anchor.add(direction.scale(26.0)));

      for (int tick = 0; tick < 38; tick += 4) {
         ModmindsMod.queueServerWork(tick, () -> BossFx.beam(world, from, to, BossFx.ALERT_DOT, 26, 0.25));
      }

      BossFx.sound(world, anchor, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.6F, 0.6);
      Set<UUID> crushed = new HashSet<>();

      for (int node = 0; node < 13; node++) {
         int step = node;
         ModmindsMod.queueServerWork(38 + node * 3, () -> {
            if (titan.isAlive()) {
               breakGround(world, titan, from, to, step, crushed);
            }
         });
      }
   }

   private static void breakGround(ServerLevel world, LimboTitanEntity titan, Vec3 from, Vec3 to, int node, Set<UUID> crushed) {
      double t = node / 12.0;
      Vec3 at = surface(world, from.lerp(to, t));
      BossFx.smashSmall(world, at, 5.0, 0.22, BossStyle.LIMBO_TITAN.flare);
      if (node == 0) {
         BossFx.shake(world, at, 44.0, 20, 0.5);
      }

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(6.5), e -> e != titan && e.isAlive() && !(e instanceof LimboTitanEntity)
      )) {
         if (BossFx.distanceToSegment(victim.position(), from, to) <= 4.5 && crushed.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(titan), 55.0F);
            victim.setDeltaMovement(victim.getDeltaMovement().add(0.0, 0.85, 0.0));
         }
      }
   }

   private static Vec3 surface(ServerLevel world, Vec3 at) {
      int x = (int)Math.floor(at.x);
      int z = (int)Math.floor(at.z);
      return new Vec3(at.x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), at.z);
   }
}
