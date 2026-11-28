package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.WaterChargeEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class WaterChargeProcedureProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && entity instanceof HydraEntity && (entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
         if (entity instanceof HydraEntity) {
            ((HydraEntity)entity).setAnimation("beam_attack3");
         }

         ServerLevel _entityToSpawn = BossFx.server(world);
         if (_entityToSpawn instanceof ServerLevel) {
            BossPresentation.windup(_entityToSpawn, entity, BossStyle.HYDRA);
            BossPresentation.announce(_entityToSpawn, entity, BossStyle.HYDRA);
         }

         if (world instanceof ServerLevel projectileLevel) {
            Projectile _entityToSpawnx = (new Object() {
               public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                  AbstractArrow entityToSpawn = new WaterChargeEntity(ModmindsModEntities.WATER_CHARGE.get(), level);
                  entityToSpawn.setOwner(shooter);
                  entityToSpawn.setBaseDamage(damage);
                  ArrowCompat.setKnockback(entityToSpawn, knockback);
                  entityToSpawn.setSilent(true);
                  return entityToSpawn;
               }
            }).getArrow(projectileLevel, entity, 1.0F, 0);
            _entityToSpawnx.setPos(
               entity.getLookAngle().x * 6.0 + entity.getX(), y + 11.0, entity.getLookAngle().z * 6.0 + entity.getZ()
            );
            _entityToSpawnx.shoot(
               (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX() - x,
               (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY() - y,
               (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ() - z,
               2.2F,
               0.0F
            );
            projectileLevel.addFreshEntity(_entityToSpawnx);
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
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
                  .checkGamemode(entityiterator)
               && world instanceof ServerLevel _level) {
               Entity entityToSpawn = ModmindsModEntities.WATER_CHARGE_ENTIDAD
                  .get()
                  .spawn(
                     _level,
                     BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()),
                     EntitySpawnReason.MOB_SUMMONED
                  );
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }
         }

         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
      }
   }
}
