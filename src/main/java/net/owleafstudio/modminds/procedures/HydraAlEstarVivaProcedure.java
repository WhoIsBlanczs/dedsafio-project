package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double magnitud = 0.0;
         if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 1.0) {
            ScreamProcedure.execute(world, x, y, z, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 2.0) {
            RayoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 3.0) {
            AreaAttackHydraProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 4.0) {
            WaterChargeProcedureProcedure.execute(world, x, y, z, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 5.0) {
            HydraMareaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 6.0) {
            HydraMordidaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 7.0) {
            HydraAbismoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).HydraAtaques == 8.0) {
            HydraChorroProcedure.execute(world, entity);
         }

         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(102.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (entityiterator.getVehicle() instanceof HydraEntity
               && (new Object() {
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
               entityiterator.stopRiding();
            }
         }

         if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y + 2.0, z))
               .findFirst()
               .orElse(null)
            != null) {
            magnitud = Math.sqrt(
               Math.pow(
                     x
                        - ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getX(),
                     2.0
                  )
                  + Math.pow(
                     y
                        - ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getY(),
                     2.0
                  )
                  + Math.pow(
                     z
                        - ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getZ(),
                     2.0
                  )
            );
            if (magnitud > 0.0) {
               ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                     .stream()
                     .sorted((new Object() {
                        Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                           return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                        }
                     }).compareDistOf(x, y + 2.0, z))
                     .findFirst()
                     .orElse(null))
                  .setDeltaMovement(
                     new Vec3(
                        (
                              x
                                 - ((Player)world.getEntitiesOfClass(
                                          Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true
                                       )
                                       .stream()
                                       .sorted((new Object() {
                                          Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                             return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                          }
                                       }).compareDistOf(x, y + 2.0, z))
                                       .findFirst()
                                       .orElse(null))
                                    .getX()
                           )
                           / magnitud
                           * -0.3,
                        0.0,
                        (
                              z
                                 - ((Player)world.getEntitiesOfClass(
                                          Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true
                                       )
                                       .stream()
                                       .sorted((new Object() {
                                          Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                             return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                          }
                                       }).compareDistOf(x, y + 2.0, z))
                                       .findFirst()
                                       .orElse(null))
                                    .getZ()
                           )
                           / magnitud
                           * -0.3
                     )
                  );
            }
         } else if (world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
               .stream()
               .sorted((new Object() {
                  Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                     return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                  }
               }).compareDistOf(x, y + 2.0, z))
               .findFirst()
               .orElse(null)
            != null) {
            magnitud = Math.sqrt(
               Math.pow(
                     x
                        - ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getX(),
                     2.0
                  )
                  + Math.pow(
                     y
                        - ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getY(),
                     2.0
                  )
                  + Math.pow(
                     z
                        - ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                              .stream()
                              .sorted((new Object() {
                                 Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                    return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                 }
                              }).compareDistOf(x, y + 2.0, z))
                              .findFirst()
                              .orElse(null))
                           .getZ(),
                     2.0
                  )
            );
            if (magnitud > 0.0) {
               ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
                     .stream()
                     .sorted((new Object() {
                        Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                           return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                        }
                     }).compareDistOf(x, y + 2.0, z))
                     .findFirst()
                     .orElse(null))
                  .setDeltaMovement(
                     new Vec3(
                        (
                              x
                                 - ((ServerPlayer)world.getEntitiesOfClass(
                                          ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true
                                       )
                                       .stream()
                                       .sorted((new Object() {
                                          Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                             return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                          }
                                       }).compareDistOf(x, y + 2.0, z))
                                       .findFirst()
                                       .orElse(null))
                                    .getX()
                           )
                           / magnitud
                           * -0.3,
                        0.0,
                        (
                              z
                                 - ((ServerPlayer)world.getEntitiesOfClass(
                                          ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true
                                       )
                                       .stream()
                                       .sorted((new Object() {
                                          Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
                                             return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
                                          }
                                       }).compareDistOf(x, y + 2.0, z))
                                       .findFirst()
                                       .orElse(null))
                                    .getZ()
                           )
                           / magnitud
                           * -0.3
                     )
                  );
            }
         }
      }
   }
}
