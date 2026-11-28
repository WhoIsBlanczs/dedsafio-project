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
public class DesertSigilParticle extends SingleQuadParticle {
   private static final int FRAMES = 9;
   private static final int TICKS_PER_FRAME = 3;
   private final SpriteSet frames;
   private final float spin;

   public static DesertSigilParticle.DesertSigilParticleProvider provider(SpriteSet spriteSet) {
      return new DesertSigilParticle.DesertSigilParticleProvider(spriteSet);
   }

   protected DesertSigilParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
      super(world, x, y, z, vx, vy, vz, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.frames = spriteSet;
      this.lifetime = 40 + this.random.nextInt(24);
      this.quadSize = 1.5F + this.random.nextFloat() * 1.3F;
      this.spin = (this.random.nextBoolean() ? 1.0F : -1.0F) * (0.05F + this.random.nextFloat() * 0.05F);
      this.rCol = 1.0F;
      this.gCol = 0.55F;
      this.bCol = 0.45F;
      this.gravity = 0.0F;
      this.friction = 0.99F;
      this.hasPhysics = false;
      this.setSprite(spriteSet.get(0, 8));
   }

   public void tick() {
      double life = (double)this.age / this.lifetime;
      this.roll = this.roll + this.spin;
      this.alpha = (float)Math.sin(Math.PI * Math.min(1.0, life * 1.15));
      this.quadSize *= 1.006F;
      this.setSprite(this.frames.get(this.age / 3 % 9, 8));
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return 15728880;
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class DesertSigilParticleProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public DesertSigilParticleProvider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new DesertSigilParticle(world, x, y, z, vx, vy, vz, this.spriteSet);
      }
   }
}
