package com.supernova;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;

public final class SupernovaState {
   public static final float FADE_START = 0.8F;
   public static final int SKY_FADE_IN = 100;
   public static final float SHOCK_FACTOR = 4.0F;
   public static final float DEFAULT_RADIUS = 250.0F;
   public static final float IDLE_MIN_RADIUS = 6.0F;
   public static final float IDLE_MAX_RADIUS = 14.0F;
   public static final float IDLE_GROW_TICKS = 6000.0F;
   public static final float COUNTDOWN_RADIUS = 2.5F;
   public static final float HOVER = 12.0F;
   public final int id;
   public final String dim;
   public final double x;
   public final double y;
   public final double z;
   public final float maxRadius;
   public final int born;
   public final int detonated;
   public final int hold;

   public static int countdown() {
      return Math.max(20, Ajustes.entero("supernova.cuenta") * 20);
   }

   public static int collapse() {
      return Math.min(200, countdown() / 2);
   }

   public static int blast() {
      return Math.max(20, Ajustes.entero("supernova.duracion") * 20);
   }

   public static int expand() {
      return Math.round(blast() * 0.75F);
   }

   public static int shockTicks() {
      return Math.round(blast() * 0.625F);
   }

   public SupernovaState(int id, String dim, double x, double y, double z, float maxRadius, int born, int detonated, int hold) {
      this.id = id;
      this.dim = dim;
      this.x = x;
      this.y = y;
      this.z = z;
      this.maxRadius = maxRadius;
      this.born = born;
      this.detonated = detonated;
      this.hold = Math.max(0, hold);
   }

   public static float sat(float v) {
      return Mth.clamp(v, 0.0F, 1.0F);
   }

   public static float smooth(float v) {
      v = sat(v);
      return v * v * (3.0F - 2.0F * v);
   }

   public Vec3 position() {
      return new Vec3(this.x, this.y, this.z);
   }

   public Vec3 center() {
      return new Vec3(this.x, this.y + 12.0, this.z);
   }

   public boolean isDetonating() {
      return this.detonated >= 0;
   }

   public float age(float now) {
      return Math.max(0.0F, now - this.born);
   }

   public float countTicks(float now) {
      return this.isDetonating() ? Math.max(0.0F, now - this.detonated) : 0.0F;
   }

   private float idleAge(float now) {
      float end = this.isDetonating() ? this.detonated : now;
      return Math.max(0.0F, end - this.born);
   }

   public float charge(float now) {
      return this.isDetonating() ? sat((this.countTicks(now) - (countdown() - collapse())) / collapse()) : 0.0F;
   }

   public boolean isBlasting(float now) {
      return this.isDetonating() && this.countTicks(now) >= countdown();
   }

   public float blastTicks(float now) {
      return this.isDetonating() ? Math.max(0.0F, this.countTicks(now) - countdown()) : 0.0F;
   }

   public int total() {
      return blast() + this.hold;
   }

   public float progress(float now) {
      return sat(this.blastTicks(now) / this.total());
   }

   public float expand(float now) {
      return sat(this.blastTicks(now) / expand());
   }

   public float fade(float now) {
      float fadeLen = blast() * 0.19999999F;
      float k = sat((this.blastTicks(now) - (blast() * 0.8F + this.hold)) / fadeLen);
      return 1.0F - k * k * (3.0F - 2.0F * k);
   }

   public float radius(float now) {
      if (!this.isBlasting(now)) {
         return 0.0F;
      }

      float q = 1.0F - this.expand(now);
      return Mth.lerp(1.0F - q * q, Math.min(2.5F, this.maxRadius), this.maxRadius);
   }

   public float shockRadius(float now) {
      if (!this.isBlasting(now)) {
         return 0.0F;
      }

      float q = 1.0F - sat(this.blastTicks(now) / shockTicks());
      return Math.max(this.radius(now), this.maxRadius * Ajustes.decimal("supernova.onda") * (1.0F - q * q));
   }

   public float shockMax() {
      return this.maxRadius * Ajustes.decimal("supernova.onda");
   }

   public float starRadius(float now) {
      float idle = 6.0F + 8.0F * (1.0F - (float)Math.exp(-this.idleAge(now) / 6000.0F));
      if (!this.isDetonating()) {
         return idle;
      }

      float k = smooth(this.charge(now));
      return Mth.lerp(k, idle, Math.min(2.5F, idle));
   }

   public float breath(float now) {
      float secs = now / 20.0F;
      if (this.isDetonating() && !this.isBlasting(now)) {
         float T = countdown() / 20.0F;
         float t = this.countTicks(now) / 20.0F;
         float f0 = 0.2857143F;
         float f1 = 3.125F;
         float fase = f0 * t + (f1 - f0) * t * t * t / (3.0F * T * T);
         float p = Math.min(1.0F, t / T);
         float amp = 0.05F + 0.05F * p;
         float encoge = 1.0F - 0.22F * smooth(p);
         return encoge * (1.0F + amp * Mth.sin(fase * (float) (Math.PI * 2)));
      } else {
         return 1.0F + 0.05F * Mth.sin(secs * (float) (Math.PI * 2) / 3.5F);
      }
   }

   public float activation(float now) {
      return this.isDetonating() ? sat(this.countTicks(now) / (countdown() * 0.35F)) : 0.0F;
   }

   public boolean expired(float now) {
      return this.isDetonating() && this.countTicks(now) > countdown() + this.total() + 60;
   }
}
