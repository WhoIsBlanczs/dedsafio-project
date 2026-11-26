package net.ruleta.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.Ruleta;
import net.ruleta.bunker.Bunkers;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.SubscribeEvent;

public class BombaWarden extends Vex {
   public static final String ETIQUETA = "ruleta_bomba_warden";
   private static final EntityDataAccessor<Boolean> ALERTA = SynchedEntityData.defineId(BombaWarden.class, EntityDataSerializers.BOOLEAN);
   private Vec3 merodeo;
   private int atascada;

   public BombaWarden(EntityType<? extends BombaWarden> tipo, Level nivel) {
      super(tipo, nivel);
      this.addTag("ruleta_bomba_warden");
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ALERTA, false);
   }

   public boolean enAlerta() {
      return (Boolean)this.entityData.get(ALERTA);
   }

   protected void registerGoals() {
   }

   protected Component getTypeName() {
      return Component.literal("Bomba Warden Voladora");
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WARDEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource fuente) {
      return SoundEvents.WARDEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WARDEN_DEATH;
   }

   public void playSound(SoundEvent sonido, float volumen, float tono) {
      if (sonido == SoundEvents.VEX_CHARGE) {
         super.playSound(SoundEvents.WARDEN_ANGRY, 0.8F, 1.4F);
      } else {
         super.playSound(sonido, volumen, tono);
      }
   }

   public void move(MoverType tipo, Vec3 movimiento) {
      this.noPhysics = false;
      super.move(tipo, movimiento);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel nivel && !this.isRemoved()) {
         double vel = Ajustes.decimal("cambios.bomba_velocidad");
         LivingEntity objetivo = this.getTarget();
         if (this.tickCount % 4 == 0 && (!esPresa(objetivo) || this.distanceToSqr(objetivo) > sq(Ajustes.entero("cambios.bomba_deteccion") + 8.0))) {
            Player cerca = nivel.getNearestPlayer(this.getX(), this.getY(), this.getZ(), Ajustes.entero("cambios.bomba_deteccion"), true);
            if (esPresa(cerca)) {
               objetivo = cerca;
               this.setTarget(cerca);
            } else {
               objetivo = null;
               this.setTarget(null);
            }
         }

         if (!esPresa(objetivo)) {
            objetivo = null;
            this.setTarget(null);
         }

         boolean alerta = objetivo != null;
         if (alerta != this.enAlerta()) {
            this.entityData.set(ALERTA, alerta);
            if (alerta) {
               nivel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_AGITATED, SoundSource.HOSTILE, 1.4F, 1.5F);
               nivel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_TAKEOFF, SoundSource.HOSTILE, 1.0F, 0.6F);
            }
         }

         Vec3 deseada;
         if (alerta) {
            Vec3 centroBomba = this.position().add(0.0, this.getBbHeight() * 0.5, 0.0);
            Vec3 centroObjetivo = objetivo.position().add(0.0, objetivo.getBbHeight() * 0.5, 0.0);
            Vec3 hacia = centroObjetivo.subtract(centroBomba);
            double dist = hacia.length();
            Vec3 dir = dist < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : hacia.scale(1.0 / dist);
            double zig = Math.min(1.0, dist / 6.0);
            Vec3 lado = new Vec3(-dir.z, 0.0, dir.x);
            double fase = this.tickCount * 0.55 + this.getId() * 1.7;
            Vec3 bamboleo = lado.scale(Math.sin(fase) * 0.22 * zig).add(0.0, Math.cos(fase * 1.3) * 0.14 * zig, 0.0);
            deseada = dir.add(bamboleo).normalize().scale(vel);
            this.facing(dir);
         } else {
            this.atascada = 0;
            BlockPos o = this.getBoundOrigin() != null ? this.getBoundOrigin() : this.blockPosition();
            if (this.merodeo == null || this.tickCount % 50 == 0 || this.position().distanceToSqr(this.merodeo) < 2.0) {
               this.merodeo = new Vec3(
                  o.getX() + (this.random.nextDouble() - 0.5) * 14.0,
                  o.getY() + (this.random.nextDouble() - 0.3) * 6.0,
                  o.getZ() + (this.random.nextDouble() - 0.5) * 14.0
               );
            }

            Vec3 hacia = this.merodeo.subtract(this.position());
            deseada = hacia.lengthSqr() < 1.0E-4 ? Vec3.ZERO : hacia.normalize().scale(vel * 0.28);
            if (hacia.lengthSqr() > 1.0E-4) {
               this.facing(hacia.normalize());
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().lerp(deseada, alerta ? 0.3 : 0.12));
         this.hurtMarked = true;
         if (this.tickCount % (alerta ? 5 : 11) == 0) {
            nivel.playSound(
               null, this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_TAKEOFF, SoundSource.HOSTILE, alerta ? 0.5F : 0.25F, alerta ? 0.7F : 0.5F
            );
         }

         if (alerta) {
            double d = Math.sqrt(this.distanceToSqr(objetivo));
            if (d < 14.0 && this.tickCount % Math.max(5, (int)(d * 2.2)) == 0) {
               float tono = 1.0F + (float)(14.0 - d) * 0.06F;
               nivel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 1.6F, tono);
            }
         }
      }
   }

   public static boolean esPresa(Entity e) {
      return e instanceof ServerPlayer p && p.isAlive() && !p.isCreative() && !p.isSpectator();
   }

   private static double sq(double v) {
      return v * v;
   }

   private void facing(Vec3 dir) {
      float yaw = (float)(Mth.atan2(-dir.x, dir.z) * (180.0 / Math.PI));
      this.setYRot(yaw);
      this.yBodyRot = yaw;
      this.yHeadRot = yaw;
   }

   public void explotar(ServerLevel nivel) {
      if (!this.isRemoved()) {
         double x = this.getX();
         double y = this.getY(0.5);
         double z = this.getZ();
         nivel.explode(this, x, y, z, 3.0F, ExplosionInteraction.NONE);
         nivel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         nivel.sendParticles(ParticleTypes.SONIC_BOOM, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         nivel.playSound(null, x, y, z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0F, 1.0F);
         nivel.playSound(null, x, y, z, SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE, 3.0F, 0.8F);
         nivel.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, 0.7F);
         float dano = Ajustes.decimal("cambios.bomba_dano") * 2.0F;

         for (ServerPlayer p : nivel.getEntitiesOfClass(ServerPlayer.class, new AABB(x - 5.0, y - 5.0, z - 5.0, x + 5.0, y + 5.0, z + 5.0))) {
            if (p.isAlive() && !p.isCreative() && !p.isSpectator() && !(p.distanceToSqr(x, y, z) > 25.0)) {
               p.invulnerableTime = 0;
               p.hurt(Ruleta.fuenteLetalPublica(p, "ruleta.bomba_warden", " fue volado por una Bomba Warden"), dano);
            }
         }

         this.destruirEsfera(nivel, BlockPos.containing(x, y, z), Ajustes.entero("cambios.bomba_radio"));

         for (ServerPlayer cercano : nivel.getEntitiesOfClass(ServerPlayer.class, new AABB(x - 12.0, y - 12.0, z - 12.0, x + 12.0, y + 12.0, z + 12.0))) {
            cercano.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
         }

         this.discard();
      }
   }

   private void destruirEsfera(ServerLevel nivel, BlockPos centro, int radio) {
      if (radio > 0 && nivel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         MutableBlockPos m = new MutableBlockPos();
         double limite = radio * radio + radio * 0.5;

         for (int dx = -radio; dx <= radio; dx++) {
            for (int dy = -radio; dy <= radio; dy++) {
               for (int dz = -radio; dz <= radio; dz++) {
                  if (!(dx * dx + dy * dy + dz * dz > limite)) {
                     m.set(centro.getX() + dx, centro.getY() + dy, centro.getZ() + dz);
                     if (nivel.isInWorldBounds(m)) {
                        BlockState estado = nivel.getBlockState(m);
                        if (!estado.isAir()
                           && !(estado.getDestroySpeed(nivel, m) < 0.0F)
                           && !(estado.getBlock() instanceof LiquidBlock)
                           && !Bunkers.protegido(nivel, m, estado)) {
                           if (nivel.getBlockEntity(m) != null) {
                              nivel.destroyBlock(m.immutable(), true, this);
                           } else {
                              nivel.setBlock(m, Blocks.AIR.defaultBlockState(), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static final class Eventos {
      private Eventos() {
      }

      @SubscribeEvent
      public static void alDetonar(ExplosionEvent.Detonate e) {
         if (e.getExplosion().getDirectSourceEntity() instanceof BombaWarden) {
            e.getAffectedEntities().clear();
            e.getAffectedBlocks().clear();
         }
      }
   }
}
