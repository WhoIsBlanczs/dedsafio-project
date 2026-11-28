package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class UltimateJumpProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null && entity instanceof LimboTitanEntity) {
         if (entity instanceof LimboTitanEntity) {
            ((LimboTitanEntity)entity).setAnimation("ultimate_jump");
         }

         ServerLevel var3 = BossFx.server(world);
         if (var3 instanceof ServerLevel) {
            BossPresentation.windup(var3, entity, BossStyle.LIMBO_TITAN);
            BossPresentation.telegraph(var3, entity, 30.0, 70, BossStyle.LIMBO_TITAN);
         }

         if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            _livingEntity2.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         }

         ModmindsMod.queueServerWork(
            60,
            () -> {
               if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
                  entity.teleportTo(
                     (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                     (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                     (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
                  );
                  if (entity instanceof ServerPlayer _serverPlayer) {
                     _serverPlayer.connection
                        .teleport(
                           (entity instanceof Mob _mobEntxxxxxx ? _mobEntxxxxxx.getTarget() : null).getX(),
                           (entity instanceof Mob _mobEntxxxxx ? _mobEntxxxxx.getTarget() : null).getY(),
                           (entity instanceof Mob _mobEntxxxx ? _mobEntxxxx.getTarget() : null).getZ(),
                           entity.getYRot(),
                           entity.getXRot()
                        );
                  }
               }

               ModmindsMod.queueServerWork(
                  10,
                  () -> {
                     Vec3 _center = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                     ServerLevel patt0$temp = BossFx.server(world);
                     if (patt0$temp instanceof ServerLevel) {
                        BossFx.smash(patt0$temp, BossFx.ground(entity), 30.0, 1.8, BossStyle.LIMBO_TITAN.flare);
                     }

                     for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(30.0), e -> true)
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
                                          && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().id()).getGameMode()
                                             == GameType.SURVIVAL
                                       : false;
                                 }
                              }
                           })
                           .checkGamemode(entityiterator)) {
                           if (world instanceof Level _level && !_level.isClientSide()) {
                              if (world instanceof ServerLevel _fx) {
                                 BossPresentation.sweep(
                                    _fx,
                                    entity,
                                    new Vec3(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()),
                                    BossStyle.LIMBO_TITAN
                                 );
                              }

                              _level.explode(
                                 null,
                                 entityiterator.getX(),
                                 entityiterator.getY(),
                                 entityiterator.getZ(),
                                 8.0F,
                                 Level.ExplosionInteraction.NONE
                              );
                           }

                           if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()), 400, 0, true, false));
                           }

                           if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                              _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.RED_SCREEN.get()), 400, 0, true, false));
                           }

                           entityiterator.hurt(
                              new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), 100.0F
                           );
                        }
                     }

                     if (entity instanceof LivingEntity _livingEntity25 && _livingEntity25.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                        _livingEntity25.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
                     }
                  }
               );
            }
         );
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
      }
   }
}
