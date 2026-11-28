package net.owleafstudio.modminds.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.network.PacketDistributor;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.ZonaPeligroEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.FlashMessage;
import net.owleafstudio.modminds.network.SubtitleMessage;
import org.joml.Vector3f;

public final class BossFx {
   public static final Vector3f ALERT = new Vector3f(0.95F, 0.07F, 0.07F);
   public static final DustParticleOptions ALERT_DOT = new DustParticleOptions(net.owleafstudio.modminds.compat.InvCompat.rgb(ALERT), 1.6F);
   public static final DustParticleOptions ALERT_CORE = new DustParticleOptions(net.owleafstudio.modminds.compat.InvCompat.rgb(ALERT), 1.0F);
   public static final int ALERT_RGB = 15864338;
   public static final DustParticleOptions CRUSHED_STONE = new DustParticleOptions(net.owleafstudio.modminds.compat.InvCompat.rgb(new Vector3f(0.66F, 0.62F, 0.58F)), 2.4F);
   public static final int CRACK_RGB = 657417;
   public static final BlockParticleOption DUST_PILLAR = new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.END_STONE.defaultBlockState());
   public static final int RAYO_RGB = 11824127;
   public static final int POZO_RGB = 8010728;
   public static final int BLOQUE_RGB = 14205066;
   public static final double LANCE = 200.0;

   private BossFx() {
   }

   public static void sound(ServerLevel world, Vec3 pos, SoundEvent event, SoundSource category, float volume, double pitch) {
      if (event != null) {
         world.playSound(null, pos.x, pos.y, pos.z, event, category, volume, (float)pitch);
      }
   }

   public static void sound(ServerLevel world, Vec3 pos, SoundEvent event, SoundSource category, float volume, double basePitch, double jitter) {
      sound(world, pos, event, category, volume, basePitch + (world.getRandom().nextDouble() - 0.5) * jitter);
   }

   public static void soundLater(ServerLevel world, Vec3 pos, SoundEvent event, SoundSource category, float volume, double pitch, int ticks) {
      ModmindsMod.queueServerWork(ticks, () -> sound(world, pos, event, category, volume, pitch));
   }

   public static void burst(ServerLevel world, Vec3 pos, ParticleOptions type, int count, double spread, double speed) {
      world.sendParticles(type, pos.x, pos.y, pos.z, count, spread, spread, spread, speed);
   }

   public static void burst(ServerLevel world, Entity entity, ParticleOptions type, int count, double spread, double speed) {
      burst(world, centre(entity), type, count, spread, speed);
   }

   public static void ring(ServerLevel world, Vec3 center, double radius, int points, ParticleOptions type, int perPoint, double jitter) {
      for (int i = 0; i < points; i++) {
         double angle = i * Math.PI * 2.0 / points;
         double x = center.x + Math.cos(angle) * radius;
         double z = center.z + Math.sin(angle) * radius;
         world.sendParticles(type, x, center.y, z, perPoint, jitter, jitter, jitter, 0.0);
      }
   }

   public static void vortex(ServerLevel world, Vec3 center, double radius, int points, ParticleOptions type, double inwardPull) {
      for (int i = 0; i < points; i++) {
         double angle = i * Math.PI * 2.0 / points + world.getRandom().nextDouble() * 0.4;
         double x = center.x + Math.cos(angle) * radius;
         double z = center.z + Math.sin(angle) * radius;
         world.sendParticles(type, x, center.y + radius * 0.35, z, 2, 0.1, 0.2, 0.1, -inwardPull);
      }
   }

   public static void beam(ServerLevel world, Vec3 from, Vec3 to, ParticleOptions type, int samples, double jitter) {
      for (int i = 0; i <= samples; i++) {
         double t = (double)i / samples;
         world.sendParticles(
            type,
            from.x + (to.x - from.x) * t,
            from.y + (to.y - from.y) * t,
            from.z + (to.z - from.z) * t,
            1,
            jitter,
            jitter,
            jitter,
            0.0
         );
      }
   }

   public static void colored(ServerLevel world, Vec3 pos, Vector3f rgb, float scale, int count, double spread) {
      burst(world, pos, new DustParticleOptions(net.owleafstudio.modminds.compat.InvCompat.rgb(rgb), scale), count, spread, 0.0);
   }

   public static void blastSound(ServerLevel world, Vec3 at, double scale) {
      sound(world, at, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, (float)(1.8 * scale), 0.55);
      sound(world, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, (float)(1.2 * scale), 0.48);
      sound(world, at, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, (float)(0.8 * scale), 0.45);
   }

   public static void blast(ServerLevel world, Vec3 at, double radius) {
      int petals = (int)Math.round(3.0 + radius * 1.4);
      world.sendParticles(net.owleafstudio.modminds.compat.InvCompat.FLASH, at.x, at.y + 0.4, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_LIGHT.get(), petals, radius * 0.2, 0.02);
      burst(world, at.add(0.0, 0.2, 0.0), (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_FIRE.get(), petals, radius * 0.32, 0.05);
      sound(world, at, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.8F, 0.55);
      sound(world, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 1.2F, 0.48);
      sound(world, at, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.8F, 0.45);
      ModmindsMod.queueServerWork(2, () -> {
         burst(world, at.add(0.0, 0.15, 0.0), (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), petals * 2, radius * 0.55, 0.08);
         ring(world, at.add(0.0, 0.3, 0.0), radius * 0.6, petals, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.25);
      });
      ModmindsMod.queueServerWork(6, () -> {
         ring(world, at.add(0.0, 0.4, 0.0), radius * 1.1, petals + 4, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.3);
         burst(world, at.add(0.0, 0.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_FIRE.get(), petals / 2 + 1, radius * 0.7, 0.03);
      });
   }

   public static void smash(ServerLevel world, Vec3 at, double radius, double strength, ParticleOptions wave) {
      blast(world, at, radius);
      burst(world, at, ParticleTypes.SWEEP_ATTACK, 8, radius * 0.3, 0.25);
      shake(world, at, radius * 1.5 + 16.0, 22, strength);
      ring(world, at.add(0.0, 1.1, 0.0), radius * 0.24, 8, wave, 1, 0.2);
      groundCrack(world, at, radius * 0.62, 90);

      for (int tick = 0; tick < 9; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double reach = radius * (step + 1) / 9.0;
            int points = 14 + step * 3;
            ring(world, at.add(0.0, 0.9, 0.0), reach, points, ParticleTypes.GUST_EMITTER_LARGE, 2, 0.3);
            ring(world, at, reach, points / 2, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.4);
            ring(world, at.add(0.0, 2.2, 0.0), reach * 0.86, points / 3, wave, 1, 0.5);
            ring(world, at.add(0.0, 0.5, 0.0), reach * 0.8, points / 2, wave, 1, 0.3);
            if (step % 3 == 0) {
               ring(world, at.add(0.0, 1.4, 0.0), reach, 6, ParticleTypes.SONIC_BOOM, 1, 0.25);
            }
         });
      }

      ModmindsMod.queueServerWork(9, () -> {
         ring(world, at.add(0.0, 0.6, 0.0), radius, 12, (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_LIGHT.get(), 1, 0.6);
         ring(world, at.add(0.0, 0.2, 0.0), radius * 1.15, 18, DUST_PILLAR, 1, 0.6);
         burst(world, at, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, radius * 0.4, 0.0);
         sound(world, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 2.2F, 0.42);
         sound(world, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.4F, 0.5);
      });
   }

   public static void smashSmall(ServerLevel world, Vec3 at, double radius, double strength, ParticleOptions wave) {
      impactBurst(world, at, radius, wave);
      shake(world, at, radius * 1.4 + 12.0, 10, strength);
   }

   public static void impactBurst(ServerLevel world, Vec3 at, double radius, ParticleOptions wave) {
      blast(world, at, radius * 0.8);
      ring(world, at.add(0.0, 0.6, 0.0), radius * 0.7, 8, wave, 1, 0.3);
      ModmindsMod.queueServerWork(3, () -> ring(world, at.add(0.0, 0.4, 0.0), radius * 1.2, 8, ParticleTypes.GUST_EMITTER_LARGE, 1, 0.4));
   }

   public static void blastDamage(ServerLevel world, DamageSource source, Vec3 at, double radius, float peak, float edge, double knock, double lift) {
      blastDamage(world, source, at, radius, peak, edge, knock, lift, null);
   }

   public static void blastDamage(
      ServerLevel world, DamageSource source, Vec3 at, double radius, float peak, float edge, double knock, double lift, @Nullable Consumer<LivingEntity> onHit
   ) {
      Entity attacker = source.getEntity();

      for (Player victim : world.getEntitiesOfClass(
         Player.class,
         new AABB(at, at).inflate(radius + 2.0),
         v -> v != attacker && v.isAlive() && !v.isSpectator() && (attacker == null || !attacker.hasPassenger(v))
      )) {
         double reach = victim.getEyePosition().distanceTo(at);
         if (!(reach > radius)) {
            double bite = 1.0 - reach / radius;
            victim.hurt(source, peak * (float)bite + edge);
            Vec3 thrown = victim.position().subtract(at);
            double sideways = Math.max(1.0E-4, Math.hypot(thrown.x, thrown.z));
            victim.setDeltaMovement(
               victim.getDeltaMovement().add(thrown.x / sideways * knock * bite, lift * bite + 0.1, thrown.z / sideways * knock * bite)
            );
            victim.needsSync = true;
            if (onHit != null) {
               onHit.accept(victim);
            }
         }
      }
   }

   public static void shake(ServerLevel world, Vec3 center, double radius, int ticks, double strength) {
      int hold = Math.max(1, ticks / 4);

      for (int tick = 0; tick < ticks; tick++) {
         double envelope = tick < hold ? 1.0 : Math.max(0.0, 1.0 - (double)(tick - hold) / Math.max(1, ticks - hold));
         double sign = tick % 2 == 0 ? 1.0 : -1.0;
         ModmindsMod.queueServerWork(
            tick,
            () -> {
               for (Player player : world.players()) {
                  if (player instanceof ServerPlayer sp && sp.isAlive() && !sp.isSpectator()) {
                     double distance = sp.position().distanceTo(center);
                     if (distance <= radius) {
                        double impulse = strength * envelope * (1.0 - distance / (radius + 1.0));
                        Vec3 motion = sp.getDeltaMovement();
                        double jolt = Math.max(-0.9, Math.min(0.42, motion.y + sign * 0.1 * impulse));
                        sp.setDeltaMovement(
                           motion.x + (world.getRandom().nextDouble() - 0.5) * 0.05 * impulse,
                           jolt,
                           motion.z + (world.getRandom().nextDouble() - 0.5) * 0.05 * impulse
                        );
                        sp.needsSync = true;
                     }
                  }
               }
            }
         );
      }
   }

   public static void flashbang(ServerLevel world, Vec3 at, double radius, int whiteTicks, int ringTicks) {
      for (ServerPlayer sp : world.players()) {
         if (sp.isAlive() && !sp.isSpectator()) {
            double distance = sp.getEyePosition().distanceTo(at);
            if (!(distance > radius) && sees(world, sp, at)) {
               double closeness = 1.0 - distance / (radius + 1.0);
               flashea(sp, closeness, whiteTicks, ringTicks);
            }
         }
      }
   }

   public static void flashea(ServerPlayer sp, double closeness, int whiteTicks, int ringTicks) {
      double cerca = Math.max(0.0, Math.min(1.0, closeness));
      int white = Math.max(6, (int)(whiteTicks * (0.45 + 0.55 * cerca)));
      int ringDelay = Math.max(4, white / 2);
      int ring = Math.max(20, (int)(ringTicks * (0.5 + 0.5 * cerca)));
      ModmindsMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> sp), new FlashMessage(white, ringDelay, ring));
      sp.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, white + ringDelay, 0, false, false));
   }

   private static boolean sees(ServerLevel world, Player viewer, Vec3 target) {
      Vec3 eye = viewer.getEyePosition();
      return world.clip(new ClipContext(eye, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, viewer)).getType() == HitResult.Type.MISS;
   }

   public static void warningCircle(ServerLevel world, Entity attacker, double radius, int leadTicks) {
      paint(world, ground(attacker), radius, leadTicks, 15864338, 0.0, 0.0, attacker);
      ModmindsMod.queueServerWork(Math.max(0, leadTicks - 10), () -> {
         sound(world, ground(attacker), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.4F, 0.7);
         burst(world, ground(attacker).add(0.0, 1.0, 0.0), ALERT_DOT, 6, 0.8, 0.12);
      });
   }

   public static void warningAt(ServerLevel world, Vec3 at, double radius, int leadTicks) {
      paint(world, at, radius, leadTicks, 15864338, 0.0, 0.0, null);
      ModmindsMod.queueServerWork(Math.max(0, leadTicks - 10), () -> {
         sound(world, at, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.4F, 0.7);
         burst(world, at.add(0.0, 1.0, 0.0), ALERT_DOT, 6, 0.8, 0.12);
      });
   }

   public static void warningBand(ServerLevel world, Entity attacker, double radius, double depth, int leadTicks) {
      Vec3 a = ground(attacker);
      paint(world, a, radius, leadTicks, 15864338, 0.0, depth, attacker);
      ModmindsMod.queueServerWork(Math.max(0, leadTicks - 10), () -> sound(world, a, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.4F, 0.7));
   }

   public static void warningBandAt(ServerLevel world, Vec3 centro, double radius, double depth, int leadTicks) {
      paint(world, centro, radius, leadTicks, 15864338, 0.0, depth, null);
      ModmindsMod.queueServerWork(Math.max(0, leadTicks - 10), () -> sound(world, centro, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.4F, 0.7));
   }

   public static void groundCrack(ServerLevel world, Vec3 at, double length, int ticks) {
      double angle = world.getRandom().nextDouble() * Math.PI * 2.0;
      int seed = world.getRandom().nextInt(997);
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setCrack(length, angle, seed, 657417, ticks);
         world.addFreshEntity(zone);
      }

      burst(world, at.add(0.0, 0.15, 0.0), ParticleTypes.SMOKE, 6, length * 0.35, 0.05);

      for (int i = 1; i <= 5; i++) {
         double d = length * i / 5.0;
         Vec3 enElSuelo = groundAt(world, at.x + Math.cos(angle) * d, at.z + Math.sin(angle) * d);
         ModmindsMod.queueServerWork(i + 1, () -> burst(world, enElSuelo.add(0.0, 0.22, 0.0), DUST_PILLAR, 3, 0.42, 0.3));
      }
   }

   public static void alert(ServerLevel world, Vec3 center, double radius, String text) {
      broadcast(world, center, radius, Component.literal(text).withStyle(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}));
   }

   public static void subtitle(ServerLevel world, Vec3 center, double radius, String text, int ticks) {
      for (ServerPlayer sp : world.players()) {
         if (sp.isAlive() && !sp.isSpectator() && sp.position().distanceTo(center) <= radius) {
            ModmindsMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> sp), new SubtitleMessage(text, ticks));
         }
      }
   }

   public static void hint(ServerPlayer viewer, Component line) {
      if (viewer != null && viewer.isAlive() && !viewer.isSpectator()) {
         viewer.connection.send(new ClientboundSetActionBarTextPacket(line));
      }
   }

   private static void broadcast(ServerLevel world, Vec3 center, double radius, Component line) {
      for (ServerPlayer sp : world.players()) {
         if (sp.isAlive() && !sp.isSpectator() && sp.position().distanceTo(center) <= radius) {
            sp.connection.send(new ClientboundSetActionBarTextPacket(line));
         }
      }
   }

   public static void marker(ServerLevel world, Vec3 at, double radius, int leadTicks) {
      paint(world, at, radius, leadTicks, 15864338, 0.0, 0.0, null);
      ModmindsMod.queueServerWork(Math.max(0, leadTicks - 10), () -> {
         sound(world, at, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.4F, 0.7);
         burst(world, at.add(0.0, 1.0, 0.0), ALERT_DOT, 6, 0.8, 0.12);
      });
   }

   public static void warningBeam(ServerLevel world, Entity anchor, Vec3 at, double angle, double length, double width, int leadTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setBeam(length, width, angle, 15864338, leadTicks);
         if (anchor != null) {
            zone.follow(anchor);
         }

         world.addFreshEntity(zone);
      }
   }

   public static List<Vec3> rayRing(ServerLevel world, Vec3 centre, double ringRadius, int circles, double radius, double boltHeight, int leadTicks) {
      List<ZonaPeligroEntity> set = new ArrayList<>();
      List<Vec3> spots = new ArrayList<>();

      for (int i = 0; i < circles; i++) {
         double angle = (Math.PI * 2) * i / circles;
         Vec3 at = groundAt(world, centre.x + Math.cos(angle) * ringRadius, centre.z + Math.sin(angle) * ringRadius);
         ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
         if (zone == null) {
            return spots;
         }

         zone.setPos(at.x, at.y, at.z);
         zone.setRayCircle(radius, 11824127, leadTicks, boltHeight, -1);
         world.addFreshEntity(zone);
         set.add(zone);
         spots.add(at);
      }

      for (int i = 0; i < set.size(); i++) {
         set.get(i).setMirror(set.get((i + 1) % set.size()).getId());
      }

      return spots;
   }

   public static void rayCircle(ServerLevel world, Vec3 at, double radius, double boltHeight, int leadTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setRayCircle(radius, 11824127, leadTicks, boltHeight, -1);
         world.addFreshEntity(zone);
      }
   }

   public static void blackHole(ServerLevel world, Vec3 at, double radius, double depth, int lifeTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setBlackHole(radius, depth, world.getRandom().nextInt(997), 8010728, lifeTicks);
         world.addFreshEntity(zone);
      }
   }

   public static List<Vec3> blockColumns(ServerLevel world, Vec3 at, double radius, int count, double height, int riseTicks, int lifeTicks) {
      List<Vec3> spots = new ArrayList<>();
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      int cantidad = zone == null ? 0 : ZonaPeligroEntity.columnCount(count);
      int seed = world.getRandom().nextInt(997);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setColumns(radius, cantidad, height, seed, 14205066, lifeTicks, riseTicks);
         world.addFreshEntity(zone);
      }

      for (int c = 0; c < cantidad; c++) {
         Vec3 spot = ZonaPeligroEntity.columnSpot(c, cantidad, radius, seed);
         Vec3 base = groundAt(world, at.x + spot.x, at.z + spot.z);
         spots.add(base);
         int arrivesAt = (int)Math.round(ZonaPeligroEntity.columnArrival(c, riseTicks));
         ModmindsMod.queueServerWork(arrivesAt + 3, () -> sandColumn(world, base, (int)Math.round(height), 2, lifeTicks));
      }

      return spots;
   }

   public static void sandColumn(ServerLevel world, Vec3 base, int height, int stepTicks, int holdTicks) {
      int bx = Mth.floor(base.x);
      int bz = Mth.floor(base.z);
      int vy = Mth.floor(world.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz));
      int flag = 50;

      for (int i = 0; i < height; i++) {
         BlockPos pos = new BlockPos(bx, vy + 1 + i, bz);
         if (world.isEmptyBlock(pos)) {
            int crece = i * Math.max(1, stepTicks);
            ModmindsMod.queueServerWork(crece, () -> {
               if (world.isEmptyBlock(pos)) {
                  world.setBlock(pos, Blocks.SAND.defaultBlockState(), flag);
               }
            });
            ModmindsMod.queueServerWork(crece + holdTicks + (height - 1 - i), () -> {
               if (world.getBlockState(pos).is(Blocks.SAND)) {
                  world.setBlock(pos, Blocks.AIR.defaultBlockState(), flag);
               }
            });
         }
      }
   }

   public static void golpeViaja(ServerLevel world, Vec3 desde, Vec3 hasta, int ticks, Runnable alLlegar) {
      double distancia = distanciaPlana(desde, hasta);
      if (distancia < 6.0) {
         alLlegar.run();
      } else {
         int pasos = Math.max(4, Math.min(18, (int)(distancia / 8.0)));

         for (int paso = 1; paso <= pasos; paso++) {
            double t = (double)paso / pasos;
            Vec3 punto = new Vec3(
               desde.x + (hasta.x - desde.x) * t, 0.0, desde.z + (hasta.z - desde.z) * t
            );
            Vec3 enElSuelo = groundAt(world, punto.x, punto.z);
            ModmindsMod.queueServerWork((int)(ticks * t), () -> {
               ring(world, enElSuelo.add(0.0, 0.1, 0.0), 3.2, 14, DUST_PILLAR, 1, 0.4);
               ring(world, enElSuelo.add(0.0, 0.7, 0.0), 2.3, 8, ParticleTypes.GUST_EMITTER_LARGE, 1, 0.3);
               ring(world, enElSuelo.add(0.0, 1.6, 0.0), 1.4, 5, ParticleTypes.END_ROD, 1, 0.2);
               if (t > 0.8) {
                  ring(world, enElSuelo.add(0.0, 0.2, 0.0), 4.4, 12, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.5);
               }

               sound(world, enElSuelo, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.1F, 0.45 + t * 0.25);
            });
         }

         ModmindsMod.queueServerWork(ticks, alLlegar);
      }
   }

   public static void onda(
      ServerLevel world, Vec3 desde, double hasta, double paso, int ticksPorPaso, DoubleConsumer pintura, BiConsumer<Player, Double> alAlcanzar
   ) {
      avanzaOnda(world, desde, hasta, paso, ticksPorPaso, paso, pintura, alAlcanzar);
   }

   private static void avanzaOnda(
      ServerLevel world,
      Vec3 desde,
      double hasta,
      double paso,
      int ticksPorPaso,
      double frente,
      DoubleConsumer pintura,
      BiConsumer<Player, Double> alAlcanzar
   ) {
      pintura.accept(frente);

      for (Player jugador : world.getEntitiesOfClass(
         Player.class, new AABB(desde, desde).inflate(frente + 3.0), p -> p.isAlive() && !p.isSpectator()
      )) {
         double alcance = distanciaPlana(desde, jugador.position());
         if (alcance <= frente && alcance > frente - paso - 0.6) {
            alAlcanzar.accept(jugador, alcance);
         }
      }

      if (!(frente >= hasta)) {
         ModmindsMod.queueServerWork(ticksPorPaso, () -> avanzaOnda(world, desde, hasta, paso, ticksPorPaso, frente + paso, pintura, alAlcanzar));
      }
   }

   public static void sandWallRow(ServerLevel world, Vec3 frente, double angle, double width, int height, int holdTicks) {
      new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
      Vec3 normal = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));
      int flag = 50;
      int lanes = Math.max(1, (int)Math.round(width));

      for (int lane = 0; lane < lanes; lane++) {
         double lado = lane - (lanes - 1) / 2.0;
         double x = frente.x + normal.x * lado;
         double z = frente.z + normal.z * lado;
         int bx = Mth.floor(x);
         int bz = Mth.floor(z);
         int vy = Mth.floor(world.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz));

         for (int i = 0; i < height; i++) {
            BlockPos pos = new BlockPos(bx, vy + 1 + i, bz);
            if (world.isEmptyBlock(pos)) {
               ModmindsMod.queueServerWork(3 + i, () -> {
                  if (world.isEmptyBlock(pos)) {
                     world.setBlock(pos, Blocks.SAND.defaultBlockState(), flag);
                  }
               });
               ModmindsMod.queueServerWork(holdTicks + 3 + (height - 1 - i), () -> {
                  if (world.getBlockState(pos).is(Blocks.SAND)) {
                     world.setBlock(pos, Blocks.AIR.defaultBlockState(), flag);
                  }
               });
            }
         }
      }
   }

   public static void blockAvalanche(ServerLevel world, Vec3 at, double length, double width, double height, double angle, int lifeTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setAvalanche(length, width, height, angle, world.getRandom().nextInt(997), 14205066, lifeTicks);
         world.addFreshEntity(zone);
      }
   }

   public static void redField(ServerLevel world, Vec3 at, double radius, double angle, int lifeTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setField(radius, angle, 0.24, world.getRandom().nextInt(997), 15864338, lifeTicks);
         world.addFreshEntity(zone);
      }
   }

   public static void clawFurrow(ServerLevel world, Entity anchor, Vec3 at, double length, double width, double angle, int travelTicks, int lifeTicks) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setFurrow(length, width, angle, world.getRandom().nextInt(997), 15864338, lifeTicks, travelTicks);
         if (anchor != null) {
            zone.follow(anchor);
         }

         world.addFreshEntity(zone);
      }
   }

   public static void shockwave(ServerLevel world, Vec3 at, double radius, int waveTicks) {
      shockwave(world, at, radius, waveTicks, 15864338);
   }

   public static void shockwave(ServerLevel world, Vec3 at, double radius, int waveTicks, int rgb) {
      paint(world, at, radius, waveTicks + 8, rgb, waveTicks, 0.0, null);
   }

   private static void paint(ServerLevel world, Vec3 at, double radius, int ticks, int rgb, double wave, double depth, @Nullable Entity anchor) {
      ZonaPeligroEntity zone = (ZonaPeligroEntity)ModmindsModEntities.ZONA_PELIGRO.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (zone != null) {
         zone.setPos(at.x, at.y, at.z);
         zone.setShape(radius, rgb, Math.max(2, ticks), wave, depth);
         if (anchor != null) {
            zone.follow(anchor);
         }

         world.addFreshEntity(zone);
      }
   }

   public static void groundSwell(ServerLevel world, Vec3 at, double radius, int ticks) {
      for (int tick = 0; tick < ticks; tick += 3) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double reach = radius * (0.3 + 0.7 * step / Math.max(1, ticks));
            ring(world, at.add(0.0, 0.1, 0.0), reach, 20, DUST_PILLAR, 1, 0.3);
            ring(world, at.add(0.0, 0.4, 0.0), reach * 0.78, 12, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.22);
            ring(world, at.add(0.0, 0.9, 0.0), reach * 0.9, 8, ParticleTypes.GUST_EMITTER_SMALL, 1, 0.3);
            ring(world, at.add(0.0, 1.5 + step * 0.04, 0.0), reach * 0.62, 5, ParticleTypes.END_ROD, 1, 0.25);
         });
      }
   }

   public static double distanceToSegment(Vec3 point, Vec3 from, Vec3 to) {
      Vec3 span = to.subtract(from);
      double length = span.lengthSqr();
      if (length < 1.0E-4) {
         return point.distanceTo(from);
      } else {
         double t = Math.max(0.0, Math.min(1.0, point.subtract(from).dot(span) / length));
         return point.distanceTo(from.add(span.scale(t)));
      }
   }

   public static Vec3 centre(Entity entity) {
      return entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);
   }

   public static Vec3 ground(Entity entity) {
      return new Vec3(entity.getX(), entity.getY() + 0.08, entity.getZ());
   }

   public static Vec3 groundAt(ServerLevel world, double x, double z) {
      int bx = Mth.floor(x);
      int bz = Mth.floor(z);
      double surface = world.getHeight(Heightmap.Types.MOTION_BLOCKING, bx, bz);
      if (surface <= world.getMinY()) {
         surface = world.getHeight(Heightmap.Types.WORLD_SURFACE, bx, bz);
      }

      return new Vec3(bx + 0.5, surface + 0.1, bz + 0.5);
   }

   @Nullable
   public static Player objetivo(ServerLevel world, Entity boss, double range) {
      double mejor = range * range;
      if (boss instanceof Mob mob
         && mob.getTarget() instanceof Player bloqueado
         && bloqueado.isAlive()
         && !bloqueado.isSpectator()
         && boss.position().distanceToSqr(bloqueado.position()) <= mejor) {
         return bloqueado;
      } else {
         Player elegido = null;

         for (Player candidato : world.players()) {
            if (candidato.isAlive() && !candidato.isSpectator()) {
               double distancia = boss.position().distanceToSqr(candidato.position());
               if (distancia < mejor) {
                  mejor = distancia;
                  elegido = candidato;
               }
            }
         }

         return elegido;
      }
   }

   public static boolean hayObjetivo(ServerLevel world, Entity boss, double range) {
      return objetivo(world, boss, range) != null;
   }

   public static Vec3 aim(ServerLevel world, Entity boss, double range) {
      Player objetivo = objetivo(world, boss, range);
      if (objetivo != null) {
         return groundAt(world, objetivo.getX(), objetivo.getZ());
      } else {
         Vec3 mirada = boss.getLookAngle();
         return groundAt(world, boss.getX() + mirada.x * 8.0, boss.getZ() + mirada.z * 8.0);
      }
   }

   public static Vec3 direccionHacia(Vec3 desde, Vec3 hacia) {
      double x = hacia.x - desde.x;
      double z = hacia.z - desde.z;
      double plano = Math.max(1.0E-4, Math.hypot(x, z));
      return new Vec3(x / plano, 0.0, z / plano);
   }

   public static double distanciaPlana(Vec3 a, Vec3 b) {
      return Math.hypot(a.x - b.x, a.z - b.z);
   }

   public static double miraA(Entity boss, Vec3 punto) {
      double dx = punto.x - boss.getX();
      double dz = punto.z - boss.getZ();
      double angle = Math.atan2(dz, dx);
      boss.setYRot((float)Math.toDegrees(Math.atan2(-dx, dz)));
      boss.setYHeadRot(boss.getYRot());
      return angle;
   }

   public static Vec3 mouth(Entity entity, double forwards, double up) {
      Vec3 look = entity.getLookAngle();
      return entity.position().add(look.x * forwards, up, look.z * forwards);
   }

   @Nullable
   public static ServerLevel server(LevelAccessor world) {
      return world instanceof ServerLevel serverLevel ? serverLevel : null;
   }
}
