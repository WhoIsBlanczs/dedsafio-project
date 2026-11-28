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
public class BossFlareParticle extends SingleQuadParticle {
   private final float growth;

   public static BossFlareParticle.BossFlareParticleProvider provider(SpriteSet spriteSet) {
      return new BossFlareParticle.BossFlareParticleProvider(spriteSet);
   }

   protected BossFlareParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.lifetime = 12 + this.random.nextInt(8);
      this.growth = 0.05F + this.random.nextFloat() * 0.04F;
      this.quadSize = 0.25F + this.random.nextFloat() * 0.15F;
      this.gravity = 0.0F;
      this.friction = 0.9F;
      this.hasPhysics = false;
      this.roll = this.random.nextFloat() * (float) Math.PI;
      this.rCol = 0.85F;
      this.gCol = 0.72F;
      this.bCol = 1.0F;
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      this.quadSize = this.quadSize + this.growth;
      this.alpha = 1.0F - (float)this.age / this.lifetime;
      this.roll += 0.12F;
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class BossFlareParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public BossFlareParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new BossFlareParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
