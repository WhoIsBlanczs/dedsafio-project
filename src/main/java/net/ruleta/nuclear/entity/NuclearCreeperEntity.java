package net.ruleta.nuclear.entity;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.ruleta.bunker.Bunkers;

public class NuclearCreeperEntity extends Creeper {
   public static final int OUTLINE_COLOR = 16777215;
   public static final double FACE_TO_FACE_GAP = 1.0;
   private static final int PROGRESS_INTERVAL = 6;
   private static final double MIN_PROGRESS = 0.3;
   private static final double DIG_REACH = 1.8;
   private static final int MAX_BLOCKS_PER_DIG = 12;
   private boolean tunneling = false;
   private double lastDist = Double.MAX_VALUE;

   public NuclearCreeperEntity(EntityType<? extends Creeper> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new NuclearCreeperEntity.NuclearSwellGoal(this));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Ocelot.class, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Cat.class, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, false, false, null) {
         {
         }
      });
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   public boolean isCharging() {
      return this.getSwellDir() > 0 || this.isIgnited();
   }

   public boolean isFaceToFace(LivingEntity other) {
      return this.getBoundingBox().inflate(1.0).intersects(other.getBoundingBox());
   }

   public void setSwellDir(int dir) {
      if (dir > 0 || this.getSwellDir() <= 0) {
         super.setSwellDir(dir);
      }
   }

   public boolean isInvulnerableTo(DamageSource source) { return false; }

   protected void customServerAiStep() {
      super.customServerAiStep((net.minecraft.server.level.ServerLevel)this.level());
      if (this.isCharging()) {
         this.tunneling = false;
         this.getNavigation().stop();
         this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0);
      } else {
         LivingEntity target = this.getTarget();
         if (target == null || !target.isAlive() || !this.level().getGameRules().get(GameRules.MOB_GRIEFING)) {
            this.tunneling = false;
            this.lastDist = Double.MAX_VALUE;
         } else if (this.isFaceToFace(target)) {
            this.tunneling = false;
         } else {
            if (this.tickCount % 6 == 0) {
               double dist = this.distanceTo(target);
               boolean noProgress = this.lastDist - dist < 0.3;
               this.lastDist = dist;
               if (noProgress || this.tunneling) {
                  Path path = this.getNavigation().createPath(target, 0);
                  this.tunneling = path == null || !path.canReach();
               }
            }

            if (this.tunneling) {
               this.getNavigation().stop();
               this.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0);
               this.getLookControl().setLookAt(target, 30.0F, 30.0F);
               if (this.tickCount % 2 == 0) {
                  this.digAhead(target);
               }
            }
         }
      }
   }

   private void digAhead(LivingEntity target) {
      Vec3 from = new Vec3(this.getX(), this.getY() + 0.6, this.getZ());
      Vec3 to = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ());
      Vec3 dir = to.subtract(from);
      if (!(dir.lengthSqr() < 1.0E-4)) {
         dir = dir.normalize();
         Set<BlockPos> cells = new LinkedHashSet<>();

         for (double t = 0.4; t <= 1.8; t += 0.4) {
            BlockPos p = BlockPos.containing(from.add(dir.scale(t)));
            cells.add(p);
            cells.add(p.above());
         }

         int broken = 0;

         for (BlockPos p : cells) {
            if (broken >= 12) {
               break;
            }

            BlockState state = this.level().getBlockState(p);
            if (!state.isAir()
               && !(state.getBlock() instanceof LiquidBlock)
               && !Bunkers.protegido(this.level(), p, state)
               && this.level().destroyBlock(p, false, this)) {
               broken++;
            }
         }
      }
   }

   public boolean isCurrentlyGlowing() {
      return true;
   }

   public int getTeamColor() {
      return 16777215;
   }

   private static class NuclearSwellGoal extends Goal {
      private final NuclearCreeperEntity creeper;

      NuclearSwellGoal(NuclearCreeperEntity creeper) {
         this.creeper = creeper;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         LivingEntity target = this.creeper.getTarget();
         return this.creeper.isCharging() || target != null && target.isAlive() && this.creeper.isFaceToFace(target);
      }

      public void start() {
         this.creeper.getNavigation().stop();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (!this.creeper.isCharging()) {
            LivingEntity target = this.creeper.getTarget();
            if (target != null && target.isAlive() && this.creeper.isFaceToFace(target)) {
               this.creeper.setSwellDir(1);
            } else {
               this.creeper.setSwellDir(-1);
            }
         }
      }
   }
}
