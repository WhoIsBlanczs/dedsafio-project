package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class FakePlayer extends Player {
   public FakePlayer(Level level, BlockPos pos, GameProfile profile) {
      super(level, profile);
      this.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
   }

   @Override
   public net.minecraft.world.level.GameType gameMode() {
      return net.minecraft.world.level.GameType.SURVIVAL;
   }

   public boolean isCreative() {
      return false;
   }

   public boolean isSpectator() {
      return false;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      return false;
   }

   public void die(DamageSource source) {
   }

   public boolean startRiding(Entity entity, boolean force) {
      return false;
   }

   public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel level, DamageSource source) {
      return true;
   }
}
