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
public class ShockwaveRingParticle extends SingleQuadParticle {
   private final float growth;
   private final float spin;

   public static ShockwaveRingParticle.ShockwaveRingParticleProvider provider(SpriteSet spriteSet) {
      return new ShockwaveRingParticle.ShockwaveRingParticleProvider(spriteSet);
   }

   protected ShockwaveRingParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.lifetime = 16 + this.random.nextInt(6);
      this.growth = 0.16F + this.random.nextFloat() * 0.06F;
      this.quadSize = 0.4F;
      this.spin = (this.random.nextFloat() - 0.5F) * 0.05F;
      this.gravity = 0.0F;
      this.friction = 1.0F;
      this.hasPhysics = false;
      this.rCol = 0.7F;
      this.gCol = 0.95F;
      this.bCol = 1.0F;
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      this.quadSize = this.quadSize + this.growth;
      float life = (float)this.age / this.lifetime;
      this.alpha = 1.0F - life * life;
      this.roll = this.roll + this.spin;
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 14737632;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ShockwaveRingParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public ShockwaveRingParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new ShockwaveRingParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
