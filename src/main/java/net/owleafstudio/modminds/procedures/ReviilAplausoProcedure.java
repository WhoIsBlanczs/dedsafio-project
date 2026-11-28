package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAplausoProcedure {
   private static final int PRIMER_APLAUSO_TICKS = 8;
   private static final int ENTRE_APLAUSOS_TICKS = 16;
   private static final int BLANCO_TICKS = 52;
   private static final int ZUMBIDO_ESPERA_TICKS = 22;
   private static final int ZUMBIDO_TICKS = 210;
   private static final double ONDA_MAX = 220.0;
   private static final double FRENTE_PASO = 5.0;
   private static final int PASO_ONDA_TICKS = 2;
   private static final double GOLPE_DANO = 6.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            reviil.setAnimation("emote.giant.aplauso");
            BossFx.alert(level, BossFx.centre(reviil), 160.0, "⚠ REVIIL APLAUDE");
            ModmindsMod.queueServerWork(8, () -> clap(level, reviil, 1));
            ModmindsMod.queueServerWork(24, () -> clap(level, reviil, 2));
            ModmindsMod.queueServerWork(40, () -> clap(level, reviil, 3));
            ModmindsMod.queueServerWork(48, () -> {
               if (reviil.isAlive()) {
                  reviil.setAnimation("emote.giant.aplauso.eco");
               }
            });
            ModmindsMod.queueServerWork(100, () -> {
               if (reviil.isAlive()) {
                  reviil.releasePose();
               }
            });
         }
      }
   }

   private static void clap(ServerLevel level, ReviilEntity reviil, int number) {
      if (reviil.isAlive()) {
         Vec3 hands = BossFx.mouth(reviil, 1.5, reviil.getBbHeight() * 0.55);
         boolean final_del_aplauso = number == 3;
         BossFx.burst(level, hands, (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), final_del_aplauso ? 4 : 2, 0.4, 0.08);
         BossFx.burst(level, hands, (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), final_del_aplauso ? 3 : 1, 0.3, 0.04);
         BossFx.burst(level, hands, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), final_del_aplauso ? 14 : 6, 0.5, 0.2);
         BossFx.sound(
            level,
            hands,
            ModmindsModSounds.APLAUSO.get(),
            SoundSource.HOSTILE,
            final_del_aplauso ? 3.4F : 1.6F,
            final_del_aplauso ? 0.72 : 1.0 + level.getRandom().nextDouble() * 0.12
         );
         BossFx.shake(level, hands, final_del_aplauso ? 120.0 : 55.0, final_del_aplauso ? 26 : 8, final_del_aplauso ? 0.8 : 0.18);
         BossFx.groundSwell(level, BossFx.ground(reviil), final_del_aplauso ? 12.0 : 5.0, 12);
         if (final_del_aplauso) {
            Vec3 desde = BossFx.ground(reviil);
            BossFx.onda(
               level,
               desde,
               220.0,
               5.0,
               2,
               radio -> BossFx.ring(level, desde.add(0.0, 0.2, 0.0), radio, 26, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.5),
               (jugador, alcance) -> {
                  double cerca = 1.0 - alcance / 221.0;
                  BossFx.flashea((ServerPlayer)jugador, cerca, 52, 210);
                  BossFx.blastDamage(
                     level, level.damageSources().mobAttack(reviil), BossFx.ground(jugador), 3.2, reviil.phaseDamage(6.0F), 1.4F, 0.8, 0.4, reviil::sting
                  );
                  BossFx.sound(level, jugador.position(), (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.6);
               }
            );
            BossFx.shockwave(level, desde, reviil.phaseBlastRadius(20.0), 18);
            BossFx.groundCrack(level, desde, reviil.getBbHeight() * 1.6, 110);
            BossFx.groundCrack(level, desde, reviil.getBbHeight() * 1.1, 110);
         }
      }
   }
}
