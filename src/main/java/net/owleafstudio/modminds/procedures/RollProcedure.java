package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class RollProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null && entity instanceof LimboTitanEntity) {
         if (entity instanceof LimboTitanEntity) {
            ((LimboTitanEntity)entity).setAnimation("roll_1");
         }

         ServerLevel var3 = BossFx.server(world);
         if (var3 instanceof ServerLevel) {
            BossPresentation.windup(var3, entity, BossStyle.LIMBO_TITAN);
            BossPresentation.announce(var3, entity, BossStyle.LIMBO_TITAN);
         }

         if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            _livingEntity2.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         }

         ModmindsMod.queueServerWork(40, () -> {
            if (entity instanceof LimboTitanEntity) {
               ((LimboTitanEntity)entity).setAnimation("roll_2");
            }

            if (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
               _livingEntity4.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
            }

            ModmindsMod.queueServerWork(200, () -> {
               if (entity instanceof LivingEntity _livingEntity5 && _livingEntity5.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                  _livingEntity5.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
               }

               if (entity instanceof LimboTitanEntity) {
                  ((LimboTitanEntity)entity).setAnimation("empty");
               }

               ModmindsMod.queueServerWork(10, () -> {
                  if (entity instanceof LimboTitanEntity) {
                     ((LimboTitanEntity)entity).setAnimation("empty");
                  }
               });
            });
         });
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
      }
   }
}
