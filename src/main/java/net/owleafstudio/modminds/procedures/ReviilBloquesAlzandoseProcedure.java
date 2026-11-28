package net.owleafstudio.modminds.procedures;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.ZonaPeligroEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilBloquesAlzandoseProcedure {
   private static final double RADIO = 19.0;
   private static final int COLUMNAS = 12;
   private static final int SUBIDA_TICKS = 18;
   private static final double ALTO = 6.5;
   private static final int VIDA_TICKS = 130;
   private static final double DAÑO = 15.0;
   private static final double RADIO_GOLPE = 1.9;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 at = BossFx.aim(level, reviil, 200.0);
            BossFx.miraA(reviil, at);
            reviil.setAnimation("emote.giant.bloques");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.alert(level, at, 170.0, "⚠ REVIIL LEVANTA EL SUELO");
            BossFx.warningAt(level, at, 19.0, 22);
            BossFx.sound(level, at, SoundEvents.ARROW_SHOOT, SoundSource.HOSTILE, 1.0F, 0.3);
            ModmindsMod.queueServerWork(22, () -> {
               if (reviil.isAlive()) {
                  List<Vec3> puestos = BossFx.blockColumns(level, at, 19.0, 12, 6.5, 18, 130);
                  BossFx.shake(level, at, 110.0, 18, 0.34);
                  BossFx.sound(level, at, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 2.2F, 0.35);

                  for (int c = 0; c < puestos.size(); c++) {
                     Vec3 spot = puestos.get(c);
                     int columna = c;
                     int arrivesAt = (int)Math.round(ZonaPeligroEntity.columnArrival(columna, 18.0));
                     ModmindsMod.queueServerWork(arrivesAt, () -> {
                        if (reviil.isAlive()) {
                           sube(level, reviil, spot, columna);
                        }
                     });
                  }

                  ModmindsMod.queueServerWork(llega(puestos.size()), () -> reviil.releasePose());
               }
            });
         }
      }
   }

   private static int llega(int cantidad) {
      return (int)Math.round(ZonaPeligroEntity.columnArrival(Math.max(0, cantidad - 1), 18.0)) + 20;
   }

   private static void sube(ServerLevel world, ReviilEntity reviil, Vec3 spot, int numero) {
      BossFx.sound(world, spot, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 1.5F, 0.35 + numero * 0.02);
      BossFx.groundCrack(world, spot, 3.4, 60);
      BossFx.burst(world, spot.add(0.0, 0.5, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, 1.1, 0.06);

      for (ServerPlayer sp : world.getEntitiesOfClass(
         ServerPlayer.class, new AABB(spot, spot).inflate(2.9), p -> p.isAlive() && !p.isSpectator() && !p.isPassenger()
      )) {
         double lado = Math.hypot(sp.getX() - spot.x, sp.getZ() - spot.z);
         if (!(lado > 1.9 + sp.getBbWidth() * 0.5)) {
            sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(15.0F));
            reviil.sting(sp);
            sp.setDeltaMovement(sp.getDeltaMovement().multiply(0.25, 0.0, 0.25).add(0.0, 0.85, 0.0));
            sp.needsSync = true;
         }
      }
   }
}
