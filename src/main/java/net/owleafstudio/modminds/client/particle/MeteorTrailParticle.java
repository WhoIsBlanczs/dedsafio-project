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
public class MeteorTrailParticle extends SingleQuadParticle {
   private static final int FRAMES = 8;
   private final SpriteSet frames;

   public static MeteorTrailParticle.MeteorTrailParticleProvider provider(SpriteSet spriteSet) {
      return new MeteorTrailParticle.MeteorTrailParticleProvider(spriteSet);
   }

   protected MeteorTrailParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.frames = spriteSet;
      this.lifetime = 9 + this.random.nextInt(6);
      this.quadSize = 0.7F + this.random.nextFloat() * 0.6F;
      this.rCol = 1.0F;
      this.gCol = 0.72F;
      this.bCol = 0.42F;
      this.gravity = -0.014F;
      this.friction = 0.94F;
      this.hasPhysics = false;
      this.setSprite(spriteSet.get(0, 7));
   }

   public void tick() {
      double life = (double)this.age / this.lifetime;
      this.quadSize *= 0.97F;
      this.alpha = (float)(1.0 - life);
      this.setSprite(this.frames.get(Math.min(7, (int)(life * 8.0)), 7));
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class MeteorTrailParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public MeteorTrailParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new MeteorTrailParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
