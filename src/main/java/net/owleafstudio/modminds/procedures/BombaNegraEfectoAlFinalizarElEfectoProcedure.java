package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class BombaNegraEfectoAlFinalizarElEfectoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).BombaNegraContadorDeEspacios != 40.0) {
            entity.hurt(
               new DamageSource(
                  world.registryAccess()
                     .lookupOrThrow(Registries.DAMAGE_TYPE)
                     .getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.parse("modminds:bomba_negra_dano")))
               ),
               entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1.0F
            );
            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof BombaNegraSinAlasEntity) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _level) {
                     Entity entityToSpawn = ModmindsModEntities.BOMBA_NEGRA_SIN_ALAS_ENTIDAD
                        .get()
                        .spawn(_level, BlockPos.containing(x, y + 1.8, z), EntitySpawnReason.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                     }
                  }

                  if (world instanceof Level _levelx && !_levelx.isClientSide()) {
                     _levelx.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
                  }
               }

               if (entityiterator instanceof BombaRojaSinAlasEntity) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ModmindsModEntities.BOMBA_ROJA_SIN_ALAS_ENTIDAD
                        .get()
                        .spawn(_levelx, BlockPos.containing(x, y + 1.8, z), EntitySpawnReason.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                     }
                  }

                  if (world instanceof Level _levelxx && !_levelxx.isClientSide()) {
                     _levelxx.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
                  }
               }

               if (entityiterator instanceof BombaAmarillaSinAlasEntity) {
                  if (!entityiterator.level().isClientSide()) {
                     entityiterator.discard();
                  }

                  if (world instanceof ServerLevel _levelxx) {
                     Entity entityToSpawn = ModmindsModEntities.BOMBA_AMARILLA_SIN_ALAS_ENTIDAD
                        .get()
                        .spawn(_levelxx, BlockPos.containing(x, y + 1.8, z), EntitySpawnReason.MOB_SUMMONED);
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                     }
                  }

                  if (world instanceof Level _levelxxx && !_levelxxx.isClientSide()) {
                     _levelxxx.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
                  }
               }
            }
         }

         double _setval = 0.0;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.BombaNegraContadorDeEspacios = _setval;
            capability.syncPlayerVariables(entity);
         });
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "attribute @p minecraft:generic.movement_speed base set 0.1"
               );
         }
      }
   }
}
