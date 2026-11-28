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
import net.owleafstudio.modminds.entity.CreeperCakeEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class CreeperCakeAlJugadorColisionarConLaEntidadProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
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

         if (entity instanceof CreeperCakeEntity) {
            ((CreeperCakeEntity)entity).setAnimation("explode");
         }

         if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.CREEPER_CAKE_EFECTO.get()), 100, 0, true, false));
         }

         if (world instanceof Level _level && !_level.isClientSide()) {
            _level.explode(null, entity.getX(), entity.getY(), entity.getZ(), 2.0F, Level.ExplosionInteraction.MOB);
         }
      }
   }
}
