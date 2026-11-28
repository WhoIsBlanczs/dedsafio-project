package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasProyectilEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasProyectilEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

@Mod.EventBusSubscriber
public class CadaTickDeLaBombaNegraProcedure {
   @SubscribeEvent
   public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null && ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).BombaNegraContadorDeEspacios == 40.0) {
         if (entity instanceof LivingEntity _entity) {
            _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.BOMBA_NEGRA_EFECTO.get()));
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator instanceof BombaNegraSinAlasEntity) {
               if (!entityiterator.level().isClientSide()) {
                  entityiterator.discard();
               }

               if (world instanceof ServerLevel projectileLevel) {
                  Projectile _entityToSpawn = (new Object() {
                     public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                        AbstractArrow entityToSpawn = new BombaNegraSinAlasProyectilEntity(ModmindsModEntities.BOMBA_NEGRA_SIN_ALAS_PROYECTIL.get(), level);
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        ArrowCompat.setKnockback(entityToSpawn, knockback);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        return entityToSpawn;
                     }
                  }).getArrow(projectileLevel, entity, 0.0F, 1);
                  _entityToSpawn.setPos(x, y + 1.8, z);
                  _entityToSpawn.shoot(entity.getLookAngle().x * 7.0, 1.8, entity.getLookAngle().z * 7.0, 1.0F, 0.0F);
                  projectileLevel.addFreshEntity(_entityToSpawn);
               }
            }

            if (entityiterator instanceof BombaRojaSinAlasEntity) {
               if (!entityiterator.level().isClientSide()) {
                  entityiterator.discard();
               }

               if (world instanceof ServerLevel projectileLevel) {
                  Projectile _entityToSpawn = (new Object() {
                     public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                        AbstractArrow entityToSpawn = new BombaRojaSinAlasProyectilEntity(ModmindsModEntities.BOMBA_ROJA_SIN_ALAS_PROYECTIL.get(), level);
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        ArrowCompat.setKnockback(entityToSpawn, knockback);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        return entityToSpawn;
                     }
                  }).getArrow(projectileLevel, entity, 0.0F, 1);
                  _entityToSpawn.setPos(x, y + 1.8, z);
                  _entityToSpawn.shoot(entity.getLookAngle().x * 7.0, 1.8, entity.getLookAngle().z * 7.0, 1.0F, 0.0F);
                  projectileLevel.addFreshEntity(_entityToSpawn);
               }
            }

            if (entityiterator instanceof BombaAmarillaSinAlasEntity) {
               if (!entityiterator.level().isClientSide()) {
                  entityiterator.discard();
               }

               if (world instanceof ServerLevel projectileLevel) {
                  Projectile _entityToSpawn = (new Object() {
                     public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
                        AbstractArrow entityToSpawn = new BombaAmarillaSinAlasProyectilEntity(ModmindsModEntities.BOMBA_AMARILLA_SIN_ALAS_PROYECTIL.get(), level);
                        entityToSpawn.setOwner(shooter);
                        entityToSpawn.setBaseDamage(damage);
                        ArrowCompat.setKnockback(entityToSpawn, knockback);
                        entityToSpawn.setSilent(true);
                        entityToSpawn.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        return entityToSpawn;
                     }
                  }).getArrow(projectileLevel, entity, 0.0F, 1);
                  _entityToSpawn.setPos(x, y + 1.8, z);
                  _entityToSpawn.shoot(entity.getLookAngle().x * 7.0, 1.8, entity.getLookAngle().z * 7.0, 1.0F, 0.0F);
                  projectileLevel.addFreshEntity(_entityToSpawn);
               }
            }
         }

         double _setval = 0.0;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.BombaNegraContadorDeEspacios = _setval;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
