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
public class GunSparkParticle extends SingleQuadParticle {
   public static GunSparkParticle.GunSparkParticleProvider provider(SpriteSet spriteSet) {
      return new GunSparkParticle.GunSparkParticleProvider(spriteSet);
   }

   protected GunSparkParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.lifetime = 8 + this.random.nextInt(8);
      this.quadSize = 0.08F + this.random.nextFloat() * 0.06F;
      this.gravity = 0.06F;
      this.friction = 0.86F;
      this.hasPhysics = false;
      this.roll = this.random.nextFloat() * (float) Math.PI;
      this.rCol = 1.0F;
      this.gCol = 0.78F;
      this.bCol = 0.45F;
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      this.alpha = 1.0F - (float)this.age / this.lifetime;
      this.roll += 0.3F;
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class GunSparkParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public GunSparkParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new GunSparkParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
