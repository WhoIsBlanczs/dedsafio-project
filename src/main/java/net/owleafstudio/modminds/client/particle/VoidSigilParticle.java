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
public class VoidSigilParticle extends SingleQuadParticle {
   private final float spin;

   public static VoidSigilParticle.VoidSigilParticleProvider provider(SpriteSet spriteSet) {
      return new VoidSigilParticle.VoidSigilParticleProvider(spriteSet);
   }

   protected VoidSigilParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.lifetime = 30 + this.random.nextInt(16);
      this.quadSize = 0.5F + this.random.nextFloat() * 0.35F;
      this.spin = (this.random.nextBoolean() ? 1.0F : -1.0F) * (0.1F + this.random.nextFloat() * 0.08F);
      this.gravity = 0.0F;
      this.friction = 0.96F;
      this.hasPhysics = false;
      this.rCol = 0.62F;
      this.gCol = 0.32F;
      this.bCol = 0.9F;
      this.setSprite(spriteSet.get(this.random));
   }

   public void tick() {
      this.roll = this.roll + this.spin * 0.1F;
      float life = (float)this.age / this.lifetime;
      this.alpha = (float)Math.sin(Math.PI * life);
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class VoidSigilParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public VoidSigilParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new VoidSigilParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
