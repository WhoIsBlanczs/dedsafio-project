package net.owleafstudio.modminds.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PulseRingParticle extends SingleQuadParticle {
   private final double base;
   private final double open;

   public static PulseRingParticle.PulseRingParticleProvider provider(SpriteSet spriteSet) {
      return new PulseRingParticle.PulseRingParticleProvider(spriteSet);
   }

   protected PulseRingParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.lifetime = 12 + this.random.nextInt(8);
      this.base = 0.5 + this.random.nextDouble() * 0.4;
      this.open = 9.0 + this.random.nextDouble() * 5.0;
      this.quadSize = (float)this.base;
      this.gravity = 0.0F;
      this.friction = 0.9F;
      this.hasPhysics = false;
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      double life = (double)this.age / this.lifetime;
      double eased = 1.0 - (1.0 - life) * (1.0 - life);
      this.quadSize = (float)(this.base * (1.0 + eased * this.open));
      this.alpha = (float)Math.pow(1.0 - life, 1.6);
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class PulseRingParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public PulseRingParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new PulseRingParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
