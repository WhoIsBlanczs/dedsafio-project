package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

public class ReactorHydraAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      double magnitud = 0.0;
      if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true).stream().sorted((new Object() {
         Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
            return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
         }
      }).compareDistOf(x, y + 2.0, z)).findFirst().orElse(null) != null) {
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
                              - ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
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
                              - ((Player)world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
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
                              - ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
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
                              - ((ServerPlayer)world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y + 2.0, z), 6.0, 6.0, 6.0), e -> true)
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
