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
public class HydraParticulaParticle extends SingleQuadParticle {
   public static HydraParticulaParticle.HydraParticulaParticleProvider provider(SpriteSet spriteSet) {
      return new HydraParticulaParticle.HydraParticulaParticleProvider(spriteSet);
   }

   protected HydraParticulaParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.setSize(0.2F, 0.2F);
      this.lifetime = 7;
      this.gravity = 0.0F;
      this.hasPhysics = true;
      this.setSprite(spriteSet.get(this.random));
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class HydraParticulaParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public HydraParticulaParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, net.minecraft.util.RandomSource aleatorio$) {
         return new HydraParticulaParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
