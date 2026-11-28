package net.owleafstudio.modminds.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.client.particle.BlastParticle;
import net.owleafstudio.modminds.client.particle.BossFlareParticle;
import net.owleafstudio.modminds.client.particle.DesertSigilParticle;
import net.owleafstudio.modminds.client.particle.GunSparkParticle;
import net.owleafstudio.modminds.client.particle.HydraParticulaParticle;
import net.owleafstudio.modminds.client.particle.MeteorFlashParticle;
import net.owleafstudio.modminds.client.particle.MeteorTrailParticle;
import net.owleafstudio.modminds.client.particle.PulseRingParticle;
import net.owleafstudio.modminds.client.particle.ShockwaveRingParticle;
import net.owleafstudio.modminds.client.particle.VoidSigilParticle;

@Mod.EventBusSubscriber(
   bus = Mod.EventBusSubscriber.Bus.MOD,
   value = {Dist.CLIENT}
)
public class ModmindsModParticles {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.HYDRA_PARTICULA.get(), HydraParticulaParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.HYDRA_DAMAGE.get(), HydraParticulaParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.BOSS_FLARE.get(), BossFlareParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), ShockwaveRingParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.VOID_SIGIL.get(), VoidSigilParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.GUN_SPARK.get(), GunSparkParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.METEOR_FLASH.get(), MeteorFlashParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.METEOR_TRAIL.get(), MeteorTrailParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.SIGIL_DESERT.get(), DesertSigilParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.PULSE_EM.get(), PulseRingParticle::provider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.EXPLOSION_LIGHT.get(), BlastParticle::lightProvider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.EXPLOSION_FIRE.get(), BlastParticle::fireProvider);
      event.registerSpriteSet((ParticleType)ModmindsModParticleTypes.BLAST_DUST.get(), BlastParticle::gritProvider);
   }
}
