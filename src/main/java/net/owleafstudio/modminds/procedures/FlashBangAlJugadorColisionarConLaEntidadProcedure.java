package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.FlashBangEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class FlashBangAlJugadorColisionarConLaEntidadProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
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
            .checkGamemode(sourceentity)) {
         if (!entity.level().isClientSide()) {
            entity.discard();
         }

         if (entity instanceof FlashBangEntity) {
            ((FlashBangEntity)entity).setAnimation("flashbang.explode");
         }

         if (world instanceof Level _level && !_level.isClientSide()) {
            _level.explode(null, x, y, z, 1.0F, Level.ExplosionInteraction.NONE);
         }

         if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.FLASH_BANG_0.get()), 80, 0, true, false));
         }
      }
   }
}
