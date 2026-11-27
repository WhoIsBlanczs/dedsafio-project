package net.ruleta.meteoritos;

import java.util.Iterator;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.Ajustes;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.Ruleta;
import net.ruleta.compat.ExplosionInmune;
import net.ruleta.phora.Crater;

public class MeteoritoEntity extends Entity implements ExplosionInmune {
   private static final double VELOCIDAD = 2.6;
   private int edad;
   private static final EntityDataAccessor<Float> TAMANO = SynchedEntityData.defineId(MeteoritoEntity.class, EntityDataSerializers.FLOAT);

   public MeteoritoEntity(EntityType<? extends MeteoritoEntity> tipo, Level nivel) {
      super(tipo, nivel);
      this.noPhysics = true;
   }

   public static Vec3 dondeMira(Player jugador) {
      Vec3 ojos = jugador.getEyePosition();
      Vec3 fin = ojos.add(jugador.getViewVector(1.0F).scale(160.0));
      BlockHitResult hit = jugador.level().clip(new ClipContext(ojos, fin, Block.OUTLINE, Fluid.ANY, jugador));
      return hit.getType() == Type.MISS ? fin : hit.getLocation();
   }

   private static float tamanoAlAzar(RandomSource r) {
      float x = r.nextFloat();
      return x < 0.3F ? 0.8F + r.nextFloat() * 0.2F : (x < 0.85F ? 1.0F + r.nextFloat() * 0.3F : 1.5F + r.nextFloat() * 0.4F);
   }

   public static MeteoritoEntity lanzar(ServerLevel nivel, Vec3 objetivo, RandomSource r, Vec3 cerca) {
      MeteoritoEntity m = new MeteoritoEntity(MeteoritosMod.METEORITO.get(), nivel);
      Vec3 base = cerca != null ? cerca : objetivo;
      double az;
      if (cerca != null) {
         double haciaObj = Math.atan2(objetivo.z - cerca.z, objetivo.x - cerca.x);
         az = haciaObj + Math.PI + (r.nextDouble() - 0.5) * Math.toRadians(70.0);
      } else {
         az = r.nextDouble() * Math.PI * 2.0;
      }

      int zona = Math.min(nivel.getServer().getPlayerList().getViewDistance(), nivel.getServer().getPlayerList().getSimulationDistance());
      double maxLado = Math.max(56.0, Math.min(420.0, zona * 16.0 - 28.0));
      double lado = maxLado * (0.8 + r.nextDouble() * 0.2);
      double alto = objetivo.y + 345.0 + r.nextDouble() * 10.0;
      lado = Math.min(lado, alto / Math.tan(Math.toRadians(37.0 + r.nextDouble() * 6.0)));
      Vec3 desde = new Vec3(base.x + Math.cos(az) * lado, alto, base.z + Math.sin(az) * lado);
      m.moveTo(desde.x, desde.y, desde.z, 0.0F, 0.0F);
      m.setDeltaMovement(objetivo.subtract(desde).normalize().scale(2.6));
      m.entityData.set(TAMANO, tamanoAlAzar(r));
      nivel.addFreshEntity(m);
      return m;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(TAMANO, 1.0F);
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

   public boolean isPushedByFluid() {
      return false;
   }

   public void push(double x, double y, double z) {
   }

   public float tamano() {
      return (Float)this.entityData.get(TAMANO);
   }

   public void tick() {
      super.tick();
      this.edad++;
      Vec3 v = this.getDeltaMovement();
      Vec3 p = this.position();
      Vec3 sig = p.add(v);
      Level nivel = this.level();
      if (!nivel.isClientSide()) {
         if (this.edad > Math.max(Ajustes.entero("meteorito.vida") * 20, 1200) || p.y < nivel.getMinY() - 16) {
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
         for (int i = 0; i < 2; i++) {
            double k = this.random.nextDouble();
            nivel.addAlwaysVisibleParticle(
               i == 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.FLAME,
               true,
               p.x - v.x * k + (this.random.nextDouble() - 0.5),
               p.y - v.y * k + (this.random.nextDouble() - 0.5),
               p.z - v.z * k + (this.random.nextDouble() - 0.5),
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
      float t = this.tamano();
      double radioOnda = 40.0 * Math.sqrt(t);
      double radioDano = Ajustes.entero("meteorito.radio") * Math.sqrt(t);
      float radioHueco = Math.max(5, Ajustes.entero("meteorito.hueco"));
      float hondoHueco = Math.max(1, Ajustes.entero("meteorito.hondo"));
      Crater.abrir(nivel, BlockPos.containing(c), this.random, t * radioHueco / 25.0F, hondoHueco / (1.2F * radioHueco));

      for (Entity e : nivel.getEntities(this, new AABB(c, c).inflate(radioOnda), x -> x.isAlive() && !x.isSpectator() && !(x instanceof MeteoritoEntity))) {
         Vec3 d = e.position().subtract(c);
         double dist = d.length();
         if (!(dist > radioOnda) && !(dist < 0.001)) {
            double k = 1.0 - dist / radioOnda;
            e.setDeltaMovement(e.getDeltaMovement().add(d.normalize().scale(2.4 * k).add(0.0, 0.8 * k, 0.0)));
            e.needsSync = true;
            if (!(e instanceof ServerPlayer sp && sp.isCreative())) {
               e.igniteForSeconds(3 + (int)Math.round(8.0 * k));
            }
         }
      }

      BlockPos centro = BlockPos.containing(c);

      for (int i = 0; i < 60; i++) {
         BlockPos q = centro.offset(this.random.nextInt(25) - 12, this.random.nextInt(9) - 4, this.random.nextInt(25) - 12);
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
                  int dano = Math.max(1, Math.round(pieza.getMaxDamage() * Ajustes.decimal("meteorito.armadura") * k));
                  pieza.hurtAndBreak(dano, e, s);
               }
            }

            e.igniteForSeconds(Math.round(4.0F + 6.0F * k));
            if (e instanceof ServerPlayer p) {
               if (!p.isCreative() && !Ruleta.totemGastadoAhora(p)) {
                  p.setInvulnerableTime(0);
                  p.hurt(Ruleta.fuenteLetalPublica(p, "ruleta.meteorito", " fue alcanzado por un meteorito"), Ajustes.decimal("meteorito.dano") * 2.0F * k);
               }
            } else {
               e.setInvulnerableTime(0);
               e.hurt(nivel.damageSources().explosion(this, null), Ajustes.decimal("meteorito.dano") * 2.0F * k);
            }
         }
      }

      ReviilPayload aviso = new ReviilPayload(
         "meteorito_impacto", Math.round(t * 100.0F), Component.literal(String.format(Locale.ROOT, "%.2f %.2f %.2f", c.x, c.y, c.z)), Component.empty()
      );

      for (ServerPlayer p : nivel.players()) {
         if (p.position().distanceToSqr(c) < 102400.0) {
            Red.enviar(p, aviso);
         }
      }
   }
}
