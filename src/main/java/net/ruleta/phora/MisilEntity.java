package net.ruleta.phora;

import java.util.Iterator;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.Ajustes;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.Ruleta;

public class MisilEntity extends Entity {
   private static final float POTENCIA = 10.0F;
   private static final double RADIO_ONDA = 48.0;
   private static final double RADIO_DANO = 26.0;
   private static final float DANO_EXTRA = 30.0F;
   private static final float DESGASTE = 0.45F;
   private static final int VIDA_MAX = 600;
   private int edad;
   private float tamano = 1.0F;

   static float tamanoAlAzar(RandomSource r) {
      float x = r.nextFloat();
      return x < 0.45F ? 0.4F + r.nextFloat() * 0.2F : (x < 0.8F ? 0.9F + r.nextFloat() * 0.3F : 2.0F + r.nextFloat() * 0.5F);
   }

   public MisilEntity(EntityType<? extends MisilEntity> tipo, Level nivel) {
      super(tipo, nivel);
      this.noPhysics = true;
   }

   public static MisilEntity lanzar(ServerLevel nivel, Vec3 objetivo, RandomSource r) {
      return lanzar(nivel, objetivo, r, null);
   }

   public static MisilEntity lanzar(ServerLevel nivel, Vec3 objetivo, RandomSource r, Vec3 cerca) {
      MisilEntity m = new MisilEntity(PhoraMod.MISIL.get(), nivel);
      double az = r.nextDouble() * Math.PI * 2.0;
      if (cerca != null) {
         double hacia = Math.atan2(cerca.z - objetivo.z, cerca.x - objetivo.x);
         az = hacia + (r.nextDouble() - 0.5) * Math.toRadians(80.0);
      }

      double alto = 230.0 + r.nextDouble() * 70.0;
      double lado = 75.0 + r.nextDouble() * 30.0;
      Vec3 desde = objetivo.add(Math.cos(az) * lado, alto, Math.sin(az) * lado);
      double velocidad = 3.8 + r.nextDouble() * 1.0;
      Vec3 v = objetivo.subtract(desde).normalize().scale(velocidad);
      m.snapTo(desde.x, desde.y, desde.z, 0.0F, 0.0F);
      m.setDeltaMovement(v);
      m.tamano = tamanoAlAzar(r);
      nivel.addFreshEntity(m);
      return m;
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput t) {
   }

   protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput t) {
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return true;
   }

   public boolean isOnFire() {
      return false;
   }

   public void tick() {
      super.tick();
      this.edad++;
      Vec3 v = this.getDeltaMovement();
      Vec3 p = this.position();
      Vec3 sig = p.add(v);
      Level nivel = this.level();
      if (!nivel.isClientSide()) {
         if (this.edad > Ajustes.entero("misil.vida") * 20 || p.y < nivel.getMinY() - 16) {
            this.discard();
            return;
         }

         BlockHitResult hit = nivel.clip(new ClipContext(p, sig, Block.COLLIDER, Fluid.ANY, this));
         Vec3 donde = hit.getType() == Type.MISS ? null : hit.getLocation();
         if (donde == null) {
            AABB caja = this.getBoundingBox().expandTowards(v).inflate(0.6);
            Iterator var8 = nivel.getEntities(this, caja, ex -> ex instanceof LivingEntity && ex.isAlive() && !ex.isSpectator()).iterator();
            if (var8.hasNext()) {
               Entity e = (Entity)var8.next();
               donde = e.position().add(0.0, e.getBbHeight() * 0.5, 0.0);
            }
         }

         if (donde != null) {
            this.reventar((ServerLevel)nivel, donde);
            return;
         }
      } else {
         for (int i = 0; i < 3; i++) {
            double k = this.random.nextDouble();
            nivel.addAlwaysVisibleParticle(
               i == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.FLAME,
               true,
               p.x - v.x * k + (this.random.nextDouble() - 0.5) * 1.2,
               p.y - v.y * k + (this.random.nextDouble() - 0.5) * 1.2,
               p.z - v.z * k + (this.random.nextDouble() - 0.5) * 1.2,
               -v.x * 0.05,
               -v.y * 0.05 + 0.02,
               -v.z * 0.05
            );
         }
      }

      this.setPos(sig.x, sig.y, sig.z);
   }

   private void reventar(ServerLevel nivel, Vec3 c) {
      this.discard();
      float t = this.tamano;
      double radioOnda = 48.0 * Math.sqrt(t);
      double radioDano = Ajustes.entero("misil.radio") * Math.sqrt(t);
      nivel.explode(this, c.x, c.y, c.z, Ajustes.decimal("misil.potencia") * Math.min(1.2F, 0.6F + 0.4F * t), true, ExplosionInteraction.TNT);
      Crater.abrir(nivel, BlockPos.containing(c), this.random, t);

      for (Entity e : nivel.getEntities(this, new AABB(c, c).inflate(radioOnda), x -> x.isAlive() && !x.isSpectator())) {
         Vec3 d = e.position().subtract(c);
         double dist = d.length();
         if (!(dist > radioOnda) && !(dist < 0.001)) {
            double k = 1.0 - dist / radioOnda;
            Vec3 empuje = d.normalize().scale(2.6 * k).add(0.0, 0.9 * k, 0.0);
            e.setDeltaMovement(e.getDeltaMovement().add(empuje));
            e.needsSync = true;
            if (!(e instanceof ServerPlayer sp && sp.isCreative())) {
               e.igniteForSeconds(3 + (int)Math.round(9.0 * k));
            }
         }
      }

      BlockPos centro = BlockPos.containing(c);

      for (int i = 0; i < 70; i++) {
         BlockPos q = centro.offset(this.random.nextInt(27) - 13, this.random.nextInt(9) - 4, this.random.nextInt(27) - 13);
         if (nivel.isEmptyBlock(q) && BaseFireBlock.canBePlacedAt(nivel, q, Direction.UP)) {
            nivel.setBlockAndUpdate(q, BaseFireBlock.getState(nivel, q));
         }
      }

      for (LivingEntity e : nivel.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(radioDano))) {
         double d = e.position().add(0.0, e.getBbHeight() * 0.5, 0.0).distanceTo(c);
         if (!(d > radioDano) && e.isAlive()) {
            float k = (float)(1.0 - d / radioDano);

            for (EquipmentSlot s : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
               ItemStack pieza = e.getItemBySlot(s);
               if (!pieza.isEmpty() && pieza.isDamageableItem()) {
                  int dano = Math.max(1, Math.round(pieza.getMaxDamage() * Ajustes.decimal("misil.armadura") * k));
                  pieza.hurtAndBreak(dano, e, s);
               }
            }

            e.igniteForSeconds(Math.round(4.0F + 6.0F * k));
            if (e instanceof ServerPlayer p) {
               if (!p.isCreative() && !Ruleta.totemGastadoAhora(p)) {
                  p.setInvulnerableTime(0);
                  p.hurt(Ruleta.fuenteLetalPublica(p, "ruleta.misil", " fue aplastado por un misil de los Phora"), Ajustes.decimal("misil.dano") * 2.0F * k);
               }
            } else {
               e.setInvulnerableTime(0);
               e.hurt(nivel.damageSources().explosion(this, null), Ajustes.decimal("misil.dano") * 2.0F * k);
            }
         }
      }

      ReviilPayload aviso = new ReviilPayload(
         "impacto", Math.round(t * 100.0F), Component.literal(String.format(Locale.ROOT, "%.2f %.2f %.2f", c.x, c.y, c.z)), Component.empty()
      );

      for (ServerPlayer p : nivel.players()) {
         if (p.position().distanceToSqr(c) < 102400.0) {
            Red.enviar(p, aviso);
         }
      }
   }

   @Override
   public boolean hurtServer(net.minecraft.server.level.ServerLevel nivel, net.minecraft.world.damagesource.DamageSource fuente, float cantidad) {
      return false;
   }
}
