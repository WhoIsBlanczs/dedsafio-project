package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ScreamProcedure {
   private static final double SHOUT_RADIUS = 25.0;
   private static final float SHOUT_PEAK = 14.0F;
   private static final float SHOUT_EDGE = 4.0F;
   private static final int LEAD_TICKS = 30;

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && entity instanceof HydraEntity) {
         if (entity instanceof HydraEntity) {
            ((HydraEntity)entity).setAnimation("screem_attack");
         }

         ServerLevel var9 = BossFx.server(world);
         if (var9 instanceof ServerLevel) {
            BossPresentation.windup(var9, entity, BossStyle.HYDRA);
            BossFx.warningCircle(var9, entity, 25.0, 30);
            BossPresentation.announce(var9, entity, BossStyle.HYDRA);
            ModmindsMod.queueServerWork(30, () -> {
               if (entity.isAlive()) {
                  BossFx.smash(var9, BossFx.ground(entity), 25.0, 1.4, BossStyle.HYDRA.flare);
                  shoutBlast(var9, entity);
               }
            });
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(250.0), e -> true)
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
               if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()), Integer.MAX_VALUE, 2, true, false));
               }

               if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
                  _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.PANTALLA_GRIS_EFECTO.get()), Integer.MAX_VALUE, 2, true, false));
               }

               if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
                  CompatAccess.server(entityiterator)
                     .getCommands()
                     .performPrefixedCommand(
                        net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                        "revolver @p"
                     );
               }

               ModmindsMod.queueServerWork(
                  15,
                  () -> {
                     if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
                        CompatAccess.server(entityiterator)
                           .getCommands()
                           .performPrefixedCommand(
                              net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                              "revolver @p"
                           );
                     }

                     ModmindsMod.queueServerWork(
                        15,
                        () -> {
                           if (!entityiterator.level().isClientSide() && CompatAccess.server(entityiterator) != null) {
                              CompatAccess.server(entityiterator)
                                 .getCommands()
                                 .performPrefixedCommand(
                                    net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entityiterator.position(), entityiterator.getRotationVector(), entityiterator.level() instanceof ServerLevel ? (ServerLevel)entityiterator.level() : null, 4, entityiterator.getName().getString(), entityiterator.getDisplayName(), entityiterator.level().getServer(), entityiterator),
                                    "revolver @p"
                                 );
                           }
                        }
                     );
                  }
               );
            }
         }

         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
      }
   }

   private static void shoutBlast(ServerLevel world, Entity hydra) {
      Vec3 at = BossFx.ground(hydra);
      BossFx.blastDamage(world, world.damageSources().sonicBoom(hydra), at, 25.0, 14.0F, 4.0F, 1.0, 0.4);
   }
}
