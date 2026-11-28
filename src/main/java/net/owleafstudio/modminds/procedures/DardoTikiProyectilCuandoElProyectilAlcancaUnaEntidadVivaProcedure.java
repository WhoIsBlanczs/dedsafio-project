package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossStyle;

public class DardoTikiProyectilCuandoElProyectilAlcancaUnaEntidadVivaProcedure {
   public static void execute(Entity immediatesourceentity) {
      if (immediatesourceentity != null && !immediatesourceentity.level().isClientSide()) {
         if (immediatesourceentity.level() instanceof ServerLevel _fx) {
            BossFx.burst(_fx, immediatesourceentity.position(), BossStyle.TIKI.flare, 8, 0.35, 0.12);
            BossFx.burst(_fx, immediatesourceentity.position(), ParticleTypes.CRIT, 6, 0.3, 0.1);
            BossFx.sound(_fx, immediatesourceentity.position(), SoundEvents.ARROW_HIT, SoundSource.HOSTILE, 1.4F, 0.8, 0.3);
         }

         immediatesourceentity.discard();
      }
   }
}
