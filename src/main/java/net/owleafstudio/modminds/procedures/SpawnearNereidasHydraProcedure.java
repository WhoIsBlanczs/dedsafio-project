package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class SpawnearNereidasHydraProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, HashMap guistate) {
      if (entity != null && guistate != null) {
         CompatAccess.persistentData(entity).putDouble("NumberOfMobs", (new Object() {
            double convert(String s) {
               try {
                  return Double.parseDouble(s.trim());
               } catch (Exception var3) {
                  return 0.0;
               }
            }
         }).convert(guistate.containsKey("text:SummonHydra") ? ((EditBox)guistate.get("text:SummonHydra")).getValue() : ""));
         Vec3 _center = new Vec3(x, y, z);

         for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
            .stream()
            .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
            .toList()) {
            if (CompatAccess.persistentData(entity).getDoubleOr("NumberOfMobs", 0.0) == 0.0) {
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
                  Entity entityToSpawn = ModmindsModEntities.NEREIDA
                     .get()
                     .spawn(
                        _level,
                        BlockPos.containing(
                           entityiterator.getLookAngle().x * 5.0 + entityiterator.getX(),
                           entityiterator.getY(),
                           entity.getLookAngle().z * 5.0 + entityiterator.getZ()
                        ),
                        EntitySpawnReason.MOB_SUMMONED
                     );
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }
            } else if (CompatAccess.persistentData(entity).getDoubleOr("NumberOfMobs", 0.0) >= 1.0) {
               for (int index0 = 0; index0 < (int)CompatAccess.persistentData(entity).getDoubleOr("NumberOfMobs", 0.0); index0++) {
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
                        .checkGamemode(entityiterator)
                     && world instanceof ServerLevel _levelx) {
                     Entity entityToSpawn = ModmindsModEntities.NEREIDA
                        .get()
                        .spawn(
                           _levelx,
                           BlockPos.containing(
                              entityiterator.getLookAngle().x * 5.0 + entityiterator.getX(),
                              entityiterator.getY(),
                              entity.getLookAngle().z * 5.0 + entityiterator.getZ()
                           ),
                           EntitySpawnReason.MOB_SUMMONED
                        );
                     if (entityToSpawn != null) {
                        entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                     }
                  }
               }
            }
         }
      }
   }
}
