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
public class MeteorFlashParticle extends SingleQuadParticle {
   private static final int FRAMES = 7;
   private final SpriteSet frames;
   private final double grow;

   public static MeteorFlashParticle.MeteorFlashParticleProvider provider(SpriteSet spriteSet) {
      return new MeteorFlashParticle.MeteorFlashParticleProvider(spriteSet);
   }

   protected MeteorFlashParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.frames = spriteSet;
      this.lifetime = 10 + this.random.nextInt(6);
      this.quadSize = 1.1F + this.random.nextFloat() * 0.9F;
      this.grow = 1.9 + this.random.nextDouble();
      this.gravity = 0.0F;
      this.friction = 0.98F;
      this.hasPhysics = false;
      this.setSprite(spriteSet.get(0, 6));
   }

   public void tick() {
      double life = (double)this.age / this.lifetime;
      this.quadSize = this.quadSize * (float)(1.0 + life * this.grow * 0.12);
      this.alpha = (float)(1.0 - life * life);
      this.setSprite(this.frames.get(Math.min(6, (int)(life * 7.0)), 6));
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class MeteorFlashParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public MeteorFlashParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new MeteorFlashParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
