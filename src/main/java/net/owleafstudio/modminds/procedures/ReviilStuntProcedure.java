package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilStuntProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.MapVariables.get(world).ReviilMontar) {
            ModmindsModVariables.MapVariables.get(world).ReviilMontar = false;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
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
               .checkGamemode(entity)) {
               entity.stopRiding();
            }

            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Montar Desactivado"), true);
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(250.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiterator instanceof ReviilEntity) {
                  ModmindsModVariables.MapVariables.get(world).ReactorAlSerDanada = false;
                  ModmindsModVariables.MapVariables.get(world).syncData(world);
                  if (entityiterator instanceof ReviilEntity animatable) {
                     animatable.setTexture("reviil_boss");
                  }

                  if (entityiterator instanceof LivingEntity _livingEntity5 && _livingEntity5.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                     _livingEntity5.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(130.0);
                  }

                  if (entityiterator instanceof LivingEntity _livingEntity6 && _livingEntity6.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                     _livingEntity6.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23);
                  }
               }
            }
         } else if (!ModmindsModVariables.MapVariables.get(world).ReviilMontar) {
            ModmindsModVariables.MapVariables.get(world).ReviilMontar = true;
            ModmindsModVariables.MapVariables.get(world).syncData(world);
            if (entity instanceof Player _player && !_player.level().isClientSide()) {
               net.owleafstudio.modminds.compat.Compat.mensaje(_player, Component.literal("Montar Activado"), true);
            }

            Vec3 _center = new Vec3(x, y, z);

            for (Entity entityiteratorx : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(250.0), e -> true)
               .stream()
               .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
               .toList()) {
               if (entityiteratorx instanceof ReviilEntity) {
                  ModmindsModVariables.MapVariables.get(world).ReactorAlSerDanada = true;
                  ModmindsModVariables.MapVariables.get(world).syncData(world);
                  if (entityiteratorx instanceof ReviilEntity animatable) {
                     animatable.setTexture("revilprime");
                  }

                  if (entityiteratorx instanceof ReviilEntity) {
                     ((ReviilEntity)entityiteratorx).setAnimation("emote.stunt");
                  }

                  if (entityiteratorx instanceof LivingEntity _livingEntity12 && _livingEntity12.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                     _livingEntity12.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.0);
                  }

                  if (entityiteratorx instanceof LivingEntity _livingEntity13 && _livingEntity13.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
                     _livingEntity13.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.07);
                  }
               }
            }
         }
      }
   }
}
