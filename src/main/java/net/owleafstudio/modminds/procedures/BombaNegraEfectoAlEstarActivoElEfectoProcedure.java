package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import net.owleafstudio.modminds.entity.BombaRojaSinAlasEntity;

public class BombaNegraEfectoAlEstarActivoElEfectoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         entity.setDeltaMovement(new Vec3(0.0, -0.35, 0.0));
         if (!entity.level().isClientSide() && CompatAccess.server(entity) != null) {
            CompatAccess.server(entity)
               .getCommands()
               .performPrefixedCommand(
                  net.owleafstudio.modminds.compat.Compat.fuente(CommandSource.NULL, entity.position(), entity.getRotationVector(), entity.level() instanceof ServerLevel ? (ServerLevel)entity.level() : null, 4, entity.getName().getString(), entity.getDisplayName(), entity.level().getServer(), entity),
                  "attribute @p minecraft:generic.movement_speed base set 0"
               );
         }

         if (world.getEntitiesOfClass(BombaNegraSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null)
            != null) {
            Entity _ent = world.getEntitiesOfClass(BombaNegraSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null);
            _ent.teleportTo(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z);
            if (_ent instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection
                  .teleport(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z, _ent.getYRot(), _ent.getXRot());
            }
         } else if (world.getEntitiesOfClass(BombaRojaSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null)
            != null) {
            Entity _ent = world.getEntitiesOfClass(BombaRojaSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null);
            _ent.teleportTo(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z);
            if (_ent instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection
                  .teleport(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z, _ent.getYRot(), _ent.getXRot());
            }
         } else if (world.getEntitiesOfClass(BombaAmarillaSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null)
            != null) {
            Entity _ent = world.getEntitiesOfClass(BombaAmarillaSinAlasEntity.class, AABB.ofSize(new Vec3(x, y, z), 4.0, 4.0, 4.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y, z))
               .findFirst()
               .orElse(null);
            _ent.teleportTo(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z);
            if (_ent instanceof ServerPlayer _serverPlayer) {
               _serverPlayer.connection
                  .teleport(x + entity.getLookAngle().x, y + 1.4, z + entity.getLookAngle().z, _ent.getYRot(), _ent.getXRot());
            }
         }
      }
   }
}
