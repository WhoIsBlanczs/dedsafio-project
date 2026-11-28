package net.owleafstudio.modminds.procedures;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilPunoMarcadoProcedure {
   private static final double MARK_RADIUS = 7.5;
   private static final double REACH = 55.0;
   private static final int WINDUP_TICKS = 44;
   private static final int LIFT_TICKS = 16;
   private static final int LEVANTA_TICKS = 10;
   private static final int MIRA_TICKS = 40;
   private static final int PLANTA_TICKS = 12;
   private static final int SQUEEZE_TICKS = 90;
   private static final int PRESS_EFFORT = 9;
   private static final double DECAY_EFFORT = 0.9;
   private static final int NEEDS = 100;
   private static final double GRAB_DAMAGE = 20.0;
   private static final double CRUSH_DAMAGE = 46.0;
   private static final int GAUGE_EVERY_TICKS = 2;
   private static final double PLANTA_RADIO = 3.4;
   private static final int PLANTA_MURO = 3;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Player buscado = BossFx.objetivo(level, reviil, 55.0);
            if (buscado == null) {
               BossFx.subtitle(level, BossFx.centre(reviil), 150.0, "FUERA DEL ALCANCE DE SU MANO", 50);
            } else {
               Vec3 landing = surface(level, buscado.getX(), buscado.getZ());
               BossFx.miraA(reviil, landing);
               reviil.setAnimation("emote.giant.puno");
               reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
               BossFx.marker(level, landing, 7.5, 44);
               BossFx.marker(level, landing, 2.8, 22);
               BossFx.warningCircle(level, reviil, 10.0, 44);
               BossFx.alert(level, landing, 160.0, "⚠ REVIIL MARCA EL SUELO");
               BossFx.sound(level, BossFx.centre(reviil), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.0F, 0.8);
               ReviilFx.grabWindup(level, reviil, landing);
               ModmindsMod.queueServerWork(38, () -> {
                  if (reviil.isAlive()) {
                     reviil.setAnimation("emote.giant.puno.golpe");
                  }
               });
               ModmindsMod.queueServerWork(44, () -> fall(level, reviil, landing));
            }
         }
      }
   }

   private static void fall(ServerLevel level, ReviilEntity reviil, Vec3 landing) {
      if (reviil.isAlive()) {
         BossFx.shake(level, landing, 90.0, 16, 0.62);
         BossFx.groundCrack(level, landing, 22.0, 90);
         BossFx.smash(level, landing, 7.5, 0.34, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
         BossFx.shockwave(level, landing, 18.0, 16);
         Player victim = nearest(level, landing);
         if (victim == null) {
            BossFx.alert(level, landing, 120.0, "REVIIL: NADIE EN LA MARCA");
            ModmindsMod.queueServerWork(20, () -> reviil.releasePose());
         } else {
            BossFx.blastDamage(level, level.damageSources().mobAttack(reviil), landing, 12.75, reviil.phaseDamage(14.0F), 2.0F, 1.15, 0.55, reviil::sting);
            lift(level, reviil, victim);
         }
      }
   }

   private static void lift(ServerLevel level, ReviilEntity reviil, Player victim) {
      if (!victim.isAlive()) {
         reviil.releasePose();
      } else {
         victim.hurt(level.damageSources().mobAttack(reviil), reviil.phaseDamage(20.0F));
         reviil.sting(victim);
         victim.setDeltaMovement(Vec3.ZERO);
         victim.startRiding(reviil, true, true);
         BossFx.alert(level, BossFx.centre(reviil), 160.0, "REVIIL CIERRA EL PUÑO SOBRE " + victim.getName().getString());
         ModmindsMod.queueServerWork(10, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("emote.giant.puno.levanta");
            }
         });
         ModmindsMod.queueServerWork(26, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("emote.giant.puno.tenue");
               if (held(reviil, victim)) {
                  ReviilFx.grabStare(level, reviil, victim);
               }
            }
         });

         for (int step = 0; step <= 4; step++) {
            int at = step;
            ModmindsMod.queueServerWork(step * 4, () -> {
               if (held(reviil, victim)) {
                  reviil.setGrabHold(5, at * 25);
               }
            });
         }

         ModmindsMod.queueServerWork(40, () -> plant(level, reviil, victim));
      }
   }

   private static void plant(ServerLevel level, ReviilEntity reviil, Player victim) {
      if (!held(reviil, victim)) {
         reviil.releasePose();
      } else {
         reviil.setAnimation("emote.giant.puno.remate");
         BossFx.alert(level, BossFx.centre(reviil), 160.0, "REVIIL CLAVA EL PUÑO EN EL SUELO");
         BossFx.hint(victim instanceof ServerPlayer sp ? sp : null, line(ChatFormatting.RED, "REVIIL BAJA EL PUÑO"));
         BossFx.sound(level, reviil.grabFistPosition(), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.2F, 0.5);
         plantTick(level, reviil, victim, 0);
      }
   }

   private static void plantTick(ServerLevel level, ReviilEntity reviil, Player victim, int step) {
      if (!held(reviil, victim)) {
         reviil.releasePose();
      } else if (step >= 12) {
         Vec3 at = BossFx.groundAt(level, victim.getX(), victim.getZ());
         reviil.setGrabHold(6, 0);
         reviil.setAnimation("emote.giant.dosbrazos.apoya");
         victim.fallDistance = 0.0F;
         victim.setDeltaMovement(Vec3.ZERO);
         ReviilFx.grabPlant(level, reviil, at, 3.4, 3);
         BossFx.blastDamage(level, level.damageSources().mobAttack(reviil), at, 8.84, reviil.phaseDamage(9.0F), 2.0F, 1.3, 0.8, reviil::sting);
         BossFx.hint(victim instanceof ServerPlayer sp ? sp : null, line(ChatFormatting.GOLD, "DA AL ESPACIO PARA SALIR"));
         squeeze(level, reviil, victim, 0);
      } else {
         int progreso = (int)Math.round(100.0 * (11 - step) / 11.0);
         reviil.setGrabHold(6, progreso);
         victim.fallDistance = 0.0F;
         Vec3 mano = reviil.grabFistPosition();
         BossFx.burst(level, mano, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 2, 0.5, 0.1);
         if (step % 4 == 0) {
            BossFx.shake(level, mano, 60.0, 3, 0.14);
         }

         ModmindsMod.queueServerWork(1, () -> plantTick(level, reviil, victim, step + 1));
      }
   }

   private static void squeeze(ServerLevel level, ReviilEntity reviil, Player victim, int tick) {
      if (!held(reviil, victim)) {
         reviil.releasePose();
      } else {
         reviil.setEscapeEffort((int)(reviil.escapeEffort() - 0.9));
         int effort = reviil.escapeEffort();
         if (effort >= 100) {
            breaks(level, reviil, victim);
         } else {
            reviil.setGrabHold(6, 0);
            victim.fallDistance = 0.0F;
            if (tick % 2 == 0 && victim instanceof ServerPlayer sp) {
               BossFx.hint(sp, gauge(effort));
            }

            Vec3 mano = reviil.grabFistPosition();
            if (tick % 6 == 0) {
               BossFx.burst(level, mano, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 3, 1.3, 0.12);
               BossFx.ring(level, mano, 2.4, 8, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.3);
               BossFx.shake(level, mano, 55.0, 3, 0.12);
            }

            if (tick % 14 == 0) {
               BossFx.sound(level, mano, (SoundEvent)SoundEvents.NOTE_BLOCK_SNARE.value(), SoundSource.HOSTILE, 1.5F, 0.45);
               BossFx.sound(level, mano, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.5F, 0.32);
            }

            if (tick >= 90) {
               crush(level, reviil, victim, mano);
            } else {
               ModmindsMod.queueServerWork(1, () -> squeeze(level, reviil, victim, tick + 1));
            }
         }
      }
   }

   private static void breaks(ServerLevel level, ReviilEntity reviil, Player victim) {
      Vec3 mano = reviil.grabFistPosition();
      victim.stopRiding();
      reviil.setGrabHold(0, 100);
      reviil.setEscapeEffort(0);
      Vec3 toward = victim.position().subtract(reviil.position()).normalize();
      victim.setPos(mano.x, mano.y + 1.4, mano.z);
      victim.setDeltaMovement(toward.x * 1.05, 0.95, toward.z * 1.05);
      victim.needsSync = true;
      victim.fallDistance = 0.0F;
      BossFx.alert(level, BossFx.centre(reviil), 160.0, victim.getName().getString() + " ROMPE EL PUÑO");
      ReviilFx.grabBreak(level, reviil, mano);
      BossFx.hint(victim instanceof ServerPlayer sp ? sp : null, line(ChatFormatting.GREEN, "LIBRE"));
      ModmindsMod.queueServerWork(26, () -> reviil.releasePose());
   }

   private static void crush(ServerLevel level, ReviilEntity reviil, Player victim, Vec3 mano) {
      reviil.setAnimation("emote.giant.dosbrazos.golpe");
      BossFx.alert(level, BossFx.centre(reviil), 160.0, "REVIIL APLASTA EL PUÑO");
      BossFx.hint(victim instanceof ServerPlayer sp ? sp : null, line(ChatFormatting.DARK_RED, "REVIIL APLASTA LA MANO"));
      Vec3 landing = BossFx.groundAt(level, mano.x, mano.z);
      BossFx.shockwave(level, landing, 18.0, 12);
      BossFx.marker(level, landing, 13.0, 12);
      BossFx.blastDamage(level, level.damageSources().mobAttack(reviil), landing, 14.25, reviil.phaseDamage(16.0F), 3.0F, 1.6, 0.9, reviil::sting);
      BossFx.smash(level, landing, 9.0, 0.4, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      ModmindsMod.queueServerWork(8, () -> {
         if (!victim.isAlive()) {
            reviil.releasePose();
         } else {
            victim.hurt(level.damageSources().mobAttack(reviil), reviil.phaseDamage(46.0F));
            reviil.sting(victim);
            victim.stopRiding();
            reviil.setGrabHold(0, 100);
            reviil.setEscapeEffort(0);
            Vec3 fuera = victim.position().subtract(reviil.position()).normalize();
            victim.setPos(landing.x, landing.y + 0.4, landing.z);
            victim.setDeltaMovement(fuera.x * 1.5, 0.5, fuera.z * 1.5);
            victim.needsSync = true;
            victim.fallDistance = 0.0F;
            BossFx.blast(level, landing, 7.0);
            BossFx.hint(victim instanceof ServerPlayer spx ? spx : null, line(ChatFormatting.DARK_RED, "ATURDIDO"));
            ModmindsMod.queueServerWork(30, () -> reviil.releasePose());
         }
      });
   }

   public static void pressEscape(Player player) {
      if (player.level() instanceof ServerLevel level) {
         if (player.getVehicle() instanceof ReviilEntity reviil && reviil.isGiantHeld(player)) {
            reviil.addEscapeEffort(9);
            Vec3 mano = reviil.grabFistPosition();
            BossFx.sound(level, mano, (SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1.0F, 1.7);
            ReviilFx.grabPress(level, reviil, mano, reviil.escapeEffort());
         }
      }
   }

   private static boolean held(ReviilEntity reviil, Player victim) {
      return victim.isPassenger() && !victim.isRemoved() && victim.isAlive() && reviil.isAlive() && reviil.isGiantHeld(victim);
   }

   private static Component gauge(int effort) {
      int filled = Math.max(0, Math.min(10, effort / 10));
      return Component.literal("FUERA DEL PUÑO [")
         .withStyle(ChatFormatting.GRAY)
         .append(Component.literal("=".repeat(filled)).withStyle(ChatFormatting.GREEN))
         .append(Component.literal("-".repeat(10 - filled)).withStyle(ChatFormatting.DARK_GRAY))
         .append(Component.literal("] ").withStyle(ChatFormatting.GRAY))
         .append(Component.translatable("key.modminds.espacio_reviil").withStyle(ChatFormatting.YELLOW))
         .append(Component.literal(" ¡DA AL ESPACIO!").withStyle(ChatFormatting.WHITE));
   }

   private static Component line(ChatFormatting colour, String text) {
      return Component.literal(text).withStyle(colour);
   }

   private static Player nearest(ServerLevel level, Vec3 at) {
      Player best = null;
      double bestDistance = 7.5;

      for (ServerPlayer sp : level.getEntitiesOfClass(
         ServerPlayer.class, new AABB(at, at).inflate(8.5), e -> e.isAlive() && !e.isSpectator() && !e.isCreative() && !e.isPassenger()
      )) {
         double distance = sp.position().distanceTo(at);
         if (distance <= bestDistance) {
            bestDistance = distance;
            best = sp;
         }
      }

      return best;
   }

   private static Vec3 surface(ServerLevel level, double x, double z) {
      return new Vec3(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z)), z);
   }
}
