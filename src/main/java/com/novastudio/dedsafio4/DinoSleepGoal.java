package com.novastudio.dedsafio4;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal;

final class DinoSleepGoal extends Goal {
   private final Mob mob;
   private final boolean nightOnly;
   private int ticksLeft;

   DinoSleepGoal(Mob var1, boolean var2) {
      this.mob = var1;
      this.nightOnly = var2;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
   }

   private boolean calm() {
      return this.mob.getTarget() == null && this.mob.hurtTime == 0 && this.mob.onGround() && !this.mob.isInWater();
   }

   public boolean canUse() {
      if (!this.calm()) {
         return false;
      } else {
         return this.nightOnly
            ? this.mob.level().isDarkOutside() && this.mob.getRandom().nextInt(40) == 0
            : this.mob.getRandom().nextInt(1500) == 0;
      }
   }

   public boolean canContinueToUse() {
      return this.calm() && (this.nightOnly ? this.mob.level().isDarkOutside() : this.ticksLeft > 0);
   }

   public void start() {
      this.ticksLeft = 600 + this.mob.getRandom().nextInt(900);
      this.mob.getNavigation().stop();
      ((DinoSleepGoal.Sleeper)this.mob).setDinoSleeping(true);
   }

   public void tick() {
      this.ticksLeft--;
   }

   public void stop() {
      ((DinoSleepGoal.Sleeper)this.mob).setDinoSleeping(false);
   }

   interface Sleeper {
      void setDinoSleeping(boolean var1);
   }
}
