package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class PunchProcedure {
   private static final double CRATER_RADIUS = 20.0;
   private static final float PEAK_DAMAGE = 26.0F;
   private static final float EDGE_DAMAGE = 5.0F;

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && entity instanceof LimboTitanEntity) {
         if (entity instanceof LimboTitanEntity) {
            ((LimboTitanEntity)entity).setAnimation("meteorite_attack");
         }

         ServerLevel var9 = BossFx.server(world);
         if (var9 instanceof ServerLevel) {
            BossPresentation.windup(var9, entity, BossStyle.LIMBO_TITAN);
            BossPresentation.telegraph(var9, entity, 20.0, 60, BossStyle.LIMBO_TITAN);
         }

         if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            _livingEntity2.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         }

         ModmindsMod.queueServerWork(
            60,
            () -> {
               if (entity instanceof LivingEntity _livingEntity3 && _livingEntity3.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                  _livingEntity3.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
               }

               Vec3 _center = new Vec3(x, y, z);
               ServerLevel patt0$temp = BossFx.server(world);
               if (patt0$temp instanceof ServerLevel) {
                  BossFx.smash(patt0$temp, _center, 20.0, 1.7, BossStyle.LIMBO_TITAN.flare);
                  craterBlast(patt0$temp, (LivingEntity)entity, _center);
               }

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(20.0), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if ((new Object() {
                        public boolean checkGamemode(Entity _ent) {
                           if (_ent instanceof ServerPlayer _serverPlayer) {
                              return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL;
                           } else {
                              return _ent.level().isClientSide() && _ent instanceof Player _player
                                 ? Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()) != null
                                    && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode() == GameType.SURVIVAL
                                 : false;
                           }
                        }
                     })
                     .checkGamemode(entityiterator)) {
                     if (world instanceof ServerLevel _fx) {
                        BossPresentation.sweep(
                           _fx,
                           entity,
                           new Vec3(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()),
                           BossStyle.LIMBO_TITAN
                        );
                     }

                     if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()), 200, 1, true, false));
                     }

                     if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                        _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.RED_SCREEN.get()), 200, 1, true, false));
                     }
                  }
               }
            }
         );
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
      }
   }

   private static void craterBlast(ServerLevel world, LivingEntity titan, Vec3 at) {
      BossFx.blastDamage(world, world.damageSources().mobAttack(titan), at, 20.0, 26.0F, 5.0F, 1.6, 0.55);
   }
}
