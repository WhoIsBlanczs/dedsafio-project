package net.owleafstudio.modminds.boss;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.RegistryObject;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import org.joml.Vector3f;

public enum BossStyle {
   HYDRA(
      new Vector3f(0.05F, 0.65F, 0.5F),
      ParticleTypes.NAUTILUS,
      ParticleTypes.BUBBLE,
      SoundEvents.ELDER_GUARDIAN_AMBIENT,
      SoundEvents.ELDER_GUARDIAN_HURT,
      SoundEvents.ELDER_GUARDIAN_DEATH,
      SoundEvents.RAVAGER_ATTACK,
      SoundEvents.GUARDIAN_ATTACK,
      ModmindsModParticleTypes.HYDRA_DAMAGE
   ),
   REACTOR_HYDRA(
      new Vector3f(0.95F, 0.55F, 0.1F),
      ParticleTypes.ELECTRIC_SPARK,
      ParticleTypes.LARGE_SMOKE,
      SoundEvents.BEACON_AMBIENT,
      SoundEvents.ANVIL_HIT,
      SoundEvents.IRON_GOLEM_DEATH,
      SoundEvents.ANVIL_LAND,
      SoundEvents.BEACON_POWER_SELECT,
      ModmindsModParticleTypes.GUN_SPARK
   ),
   LIMBO_TITAN(
      new Vector3f(0.8F, 0.35F, 0.15F),
      ParticleTypes.FLAME,
      ParticleTypes.ASH,
      SoundEvents.WARDEN_AMBIENT,
      SoundEvents.WARDEN_HURT,
      SoundEvents.WARDEN_DEATH,
      SoundEvents.WARDEN_ATTACK_IMPACT,
      SoundEvents.WARDEN_SONIC_CHARGE,
      ModmindsModParticleTypes.BOSS_FLARE
   ),
   GORGONA(
      new Vector3f(0.55F, 0.85F, 0.75F),
      ParticleTypes.SNOWFLAKE,
      ParticleTypes.CLOUD,
      SoundEvents.CAT_SOUNDS.get(net.minecraft.world.entity.animal.feline.CatSoundVariants.SoundSet.CLASSIC).adultSounds().hissSound().value(),
      SoundEvents.PHANTOM_HURT,
      SoundEvents.PHANTOM_DEATH,
      SoundEvents.ELDER_GUARDIAN_CURSE,
      SoundEvents.AMETHYST_BLOCK_RESONATE,
      ModmindsModParticleTypes.VOID_SIGIL
   ),
   SAPO(
      new Vector3f(0.35F, 0.8F, 0.25F),
      ParticleTypes.ITEM_SLIME,
      ParticleTypes.SPLASH,
      SoundEvents.FROG_AMBIENT,
      SoundEvents.FROG_HURT,
      SoundEvents.FROG_DEATH,
      SoundEvents.SLIME_ATTACK,
      SoundEvents.FROG_TONGUE,
      ModmindsModParticleTypes.SHOCKWAVE_RING
   ),
   WARDEN_MOUNSTROSITY(
      new Vector3f(0.05F, 0.75F, 0.7F),
      ParticleTypes.SCULK_CHARGE_POP,
      ParticleTypes.SCULK_SOUL,
      SoundEvents.WARDEN_AMBIENT,
      SoundEvents.WARDEN_HURT,
      SoundEvents.WARDEN_DEATH,
      SoundEvents.WARDEN_ATTACK_IMPACT,
      SoundEvents.WARDEN_SONIC_CHARGE,
      ModmindsModParticleTypes.VOID_SIGIL
   ),
   BOMBA_NEGRA(
      new Vector3f(0.15F, 0.1F, 0.2F),
      ParticleTypes.SMOKE,
      ParticleTypes.WHITE_ASH,
      SoundEvents.GHAST_WARN,
      SoundEvents.GHAST_HURT,
      SoundEvents.GHAST_DEATH,
      SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
      SoundEvents.FIREWORK_ROCKET_LAUNCH,
      ModmindsModParticleTypes.BOSS_FLARE
   ),
   ORB(
      new Vector3f(0.8F, 0.6F, 0.95F),
      ParticleTypes.END_ROD,
      ParticleTypes.PORTAL,
      SoundEvents.SHULKER_AMBIENT,
      SoundEvents.SHULKER_HURT,
      SoundEvents.SHULKER_DEATH,
      SoundEvents.SHULKER_BULLET_HIT,
      SoundEvents.SHULKER_SHOOT,
      ModmindsModParticleTypes.BOSS_FLARE
   ),
   COLOSO(
      new Vector3f(0.7F, 0.65F, 0.55F),
      ParticleTypes.CLOUD,
      ParticleTypes.MYCELIUM,
      SoundEvents.RAVAGER_AMBIENT,
      SoundEvents.RAVAGER_HURT,
      SoundEvents.RAVAGER_DEATH,
      SoundEvents.RAVAGER_ROAR,
      SoundEvents.GOAT_PREPARE_RAM,
      ModmindsModParticleTypes.SHOCKWAVE_RING
   ),
   TIKI(
      new Vector3f(0.3F, 0.75F, 0.25F),
      ParticleTypes.SOUL_FIRE_FLAME,
      ParticleTypes.COMPOSTER,
      SoundEvents.BLAZE_AMBIENT,
      SoundEvents.MAGMA_CUBE_HURT,
      SoundEvents.MAGMA_CUBE_DEATH,
      SoundEvents.WOOD_BREAK,
      SoundEvents.FIRECHARGE_USE,
      ModmindsModParticleTypes.BOSS_FLARE
   );

   public final Vector3f aura;
   public final ParticleOptions flare;
   public final ParticleOptions mote;
   public final SoundEvent ambient;
   public final SoundEvent hurt;
   public final SoundEvent death;
   public final SoundEvent impact;
   public final SoundEvent charge;
   private final RegistryObject<SimpleParticleType> sparkType;

   private BossStyle(
      Vector3f aura,
      ParticleOptions flare,
      ParticleOptions mote,
      SoundEvent ambient,
      SoundEvent hurt,
      SoundEvent death,
      SoundEvent impact,
      SoundEvent charge,
      RegistryObject<SimpleParticleType> sparkType
   ) {
      this.aura = aura;
      this.flare = flare;
      this.mote = mote;
      this.ambient = ambient;
      this.hurt = hurt;
      this.death = death;
      this.impact = impact;
      this.charge = charge;
      this.sparkType = sparkType;
   }

   public ParticleOptions spark() {
      return (ParticleOptions)this.sparkType.get();
   }

   public String label() {
      return this.name().replace('_', ' ');
   }
}
