package net.owleafstudio.modminds.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModmindsModParticleTypes {
   public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "modminds");
   public static final RegistryObject<SimpleParticleType> HYDRA_PARTICULA = REGISTRY.register("hydra_particula", () -> new SimpleParticleType(false) {});
   public static final RegistryObject<SimpleParticleType> BOSS_FLARE = REGISTRY.register("boss_flare", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> SHOCKWAVE_RING = REGISTRY.register("shockwave_ring", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> VOID_SIGIL = REGISTRY.register("void_sigil", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> GUN_SPARK = REGISTRY.register("gun_spark", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> METEOR_FLASH = REGISTRY.register("meteor_flash", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> METEOR_TRAIL = REGISTRY.register("meteor_trail", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> SIGIL_DESERT = REGISTRY.register("sigil_desert", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> PULSE_EM = REGISTRY.register("pulse_em", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> EXPLOSION_LIGHT = REGISTRY.register("explosion_light", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> EXPLOSION_FIRE = REGISTRY.register("explosion_fire", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> BLAST_DUST = REGISTRY.register("blast_dust", () -> new SimpleParticleType(true) {});
   public static final RegistryObject<SimpleParticleType> HYDRA_DAMAGE = REGISTRY.register("hydra_damage", () -> new SimpleParticleType(true) {});
}
