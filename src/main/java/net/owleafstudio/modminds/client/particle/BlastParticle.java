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
public class BlastParticle extends SingleQuadParticle {
   private final SpriteSet film;
   private final int frames;
   private final float growPerTick;
   private final boolean hot;

   public static ParticleProvider<SimpleParticleType> lightProvider(SpriteSet set) {
      return new BlastParticle.Provider(set, 8, 13, 1.5F, 0.3F, true);
   }

   public static ParticleProvider<SimpleParticleType> fireProvider(SpriteSet set) {
      return new BlastParticle.Provider(set, 8, 20, 2.1F, 0.16F, true);
   }

   public static ParticleProvider<SimpleParticleType> gritProvider(SpriteSet set) {
      return new BlastParticle.Provider(set, 6, 30, 1.6F, 0.06F, false);
   }

   private BlastParticle(ClientLevel world, double x, double y, double z, SpriteSet set, int frames, int lifetime, float size, float grow, boolean hot) {
      super(world, x, y, z, 0.0, 0.0, 0.0, (net.minecraft.client.renderer.texture.TextureAtlasSprite)null);
      this.film = set;
      this.frames = frames;
      this.growPerTick = grow;
      this.hot = hot;
      this.lifetime = lifetime + this.random.nextInt(5);
      this.quadSize = size * (0.85F + this.random.nextFloat() * 0.4F);
      this.gravity = hot ? 0.0F : 0.06F;
      this.friction = hot ? 0.96F : 0.86F;
      this.hasPhysics = false;
      if (hot) {
         this.roll = this.random.nextFloat() * (float) Math.PI;
      } else {
         double a = this.random.nextDouble() * Math.PI * 2.0;
         double out = 0.1 + this.random.nextDouble() * 0.16;
         this.setParticleSpeed(Math.cos(a) * out, 0.1 + this.random.nextDouble() * 0.12, Math.sin(a) * out);
      }

      this.setSprite(set.get(this.random.nextInt(frames), frames));
   }

   public void tick() {
      float life = (float)this.age / this.lifetime;
      this.quadSize = this.quadSize * (1.0F + this.growPerTick * (1.0F - life));
      this.alpha = life < 0.22F ? 1.0F : (float)Math.pow(1.0 - (life - 0.22F) / 0.78F, this.hot ? 1.4 : 0.9);
      this.setSprite(this.film.get(Math.min(this.frames - 1, (int)(life * this.frames)), this.frames));
      super.tick();
   }

   protected int getLightCoords(float partialTick) {
      return this.hot ? 15728880 : super.getLightCoords(partialTick);
   }

   protected SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   private static final class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet set;
      private final int frames;
      private final int lifetime;
      private final float size;
      private final float grow;
      private final boolean hot;

      private Provider(SpriteSet set, int frames, int lifetime, float size, float grow, boolean hot) {
         this.set = set;
         this.frames = frames;
         this.lifetime = lifetime;
         this.size = size;
         this.grow = grow;
         this.hot = hot;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, net.minecraft.util.RandomSource aleatorio$) {
         return new BlastParticle(world, x, y, z, this.set, this.frames, this.lifetime, this.size, this.grow, this.hot);
      }
   }
}
