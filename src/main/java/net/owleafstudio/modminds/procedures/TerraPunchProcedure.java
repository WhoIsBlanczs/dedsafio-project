package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TerraPunchProcedure {
   private static final int STEPS = 11;
   private static final double FIRST_DISTANCE = 10.0;
   private static final double STEP_DISTANCE = 5.0;
   private static final int WINDUP_TICKS = 30;
   private static final int STEP_TICKS = 5;
   private static final float BLAST_RADIUS = 4.0F;
   private static final float POUND_DAMAGE = 80.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel level) {
         titan.setAnimation("meteorite_attack");
         BossPresentation.windup(level, entity, BossStyle.LIMBO_TITAN);
         BossPresentation.announce(level, entity, BossStyle.LIMBO_TITAN);
         titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);

         for (int step = 0; step < 11; step++) {
            double distance = 10.0 + step * 5.0;
            boolean last = step == 10;
            ModmindsMod.queueServerWork(30 + step * 5, () -> pound(level, titan, distance, last));
         }
      }
   }

   private static void pound(ServerLevel world, LimboTitanEntity titan, double distance, boolean last) {
      if (!titan.isRemoved()) {
         Vec3 look = titan.getLookAngle();
         Vec3 at = BossFx.groundAt(world, look.x * distance + titan.getX(), look.z * distance + titan.getZ());
         BossPresentation.sweep(world, titan, at, BossStyle.LIMBO_TITAN);
         world.explode(null, at.x, at.y, at.z, 4.0F, Level.ExplosionInteraction.NONE);

         for (Entity victim : world.getEntitiesOfClass(Entity.class, new AABB(at, at).inflate(4.0), e -> e instanceof Player && e != titan)) {
            victim.hurt(world.damageSources().generic(), 80.0F);
         }

         if (last) {
            titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
            titan.setAnimation("empty");
         }
      }
   }
}
