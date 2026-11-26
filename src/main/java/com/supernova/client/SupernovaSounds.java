package com.supernova.client;

import com.mojang.logging.LogUtils;
import com.supernova.SupernovaMod;
import com.supernova.SupernovaState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;
import org.slf4j.Logger;

public class SupernovaSounds {
   private static final Set<Integer> COLAPSO = new HashSet<>();
   private static final float COLAPSO_TICKS = 200.0F;
   private static final Set<Integer> BLAST = new HashSet<>();
   private static final Set<Integer> WAVE = new HashSet<>();
   private static final Map<Integer, SupernovaSounds.Hum> HUMS = new HashMap<>();
   private static final Logger LOG = LogUtils.getLogger();
   private static final Map<Integer, SupernovaSounds.Musica> MUSICAS = new HashMap<>();

   private static float near(SupernovaState s, double d) {
      return 1.0F - SupernovaState.smooth((float)((d - s.maxRadius * 6.0) / (s.maxRadius * 6.0)));
   }

   private static void play(SoundEvent ev, float vol, float pitch) {
      if (!(vol <= 0.01F)) {
         Minecraft.getInstance()
            .getSoundManager()
            .play(
               new SimpleSoundInstance(
                  ev.getLocation(), SoundSource.MASTER, Math.min(1.0F, vol), pitch, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true
               )
            );
      }
   }

   @SubscribeEvent
   public static void onTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null) {
            float now = (float)mc.level.getGameTime();
            Vec3 eye = mc.player.getEyePosition();
            Set<Integer> seen = new HashSet<>();

            for (SupernovaState s : SupernovaGhosts.active(now)) {
               int id = s.id;
               seen.add(id);
               double d = eye.distanceTo(s.center());
               float near = near(s, d);
               if (!s.isBlasting(now) && d < 400.0) {
                  particulas(mc, s, now);
               }

               if (s.charge(now) <= 0.0F && near > 0.01F) {
                  SupernovaSounds.Hum h = HUMS.get(id);
                  if (h == null || h.isStopped() || now - h.startedAt > 40.0F && !mc.getSoundManager().isActive(h)) {
                     h = new SupernovaSounds.Hum(id, now);
                     HUMS.put(id, h);
                     mc.getSoundManager().play(h);
                  }
               }

               if (s.isDetonating() && !s.isBlasting(now)) {
                  float cc = s.countTicks(now) - (SupernovaState.countdown() - 200.0F);
                  if (cc >= 0.0F && COLAPSO.add(id) && cc < 30.0F) {
                     play(SupernovaMod.SND_COLAPSO.get(), 0.45F + 0.55F * near, 1.0F);
                  }
               }

               if (s.isBlasting(now)) {
                  float bt = s.blastTicks(now);
                  if (BLAST.add(id) && bt < 40.0F) {
                     boolean yaSuena = false;

                     for (SupernovaSounds.Musica otra : MUSICAS.values()) {
                        if (!otra.isStopped() && otra.apagando < 0.0F && mc.getSoundManager().isActive(otra)) {
                           yaSuena = true;
                           break;
                        }
                     }

                     if (!yaSuena) {
                        LOG.info("[supernova] suena music.ogg (supernova {})", id);
                        SupernovaSounds.Musica m = new SupernovaSounds.Musica(id, Math.max(near, 0.7F));
                        MUSICAS.put(id, m);
                        mc.getSoundManager().play(m);
                     }
                  }

                  if (!WAVE.contains(id)) {
                     float r = s.shockRadius(now);
                     float soon = s.shockRadius(now + 60.0F);
                     if (d <= r) {
                        WAVE.add(id);
                     } else if (d <= soon) {
                        WAVE.add(id);
                        play(SupernovaMod.SND_WAVE.get(), 1.0F, 1.0F);
                     }
                  }
               }
            }

            COLAPSO.retainAll(seen);
            BLAST.retainAll(seen);
            WAVE.retainAll(seen);
            HUMS.keySet().retainAll(seen);
            MUSICAS.values().removeIf(AbstractTickableSoundInstance::isStopped);
         } else {
            clear();
         }
      }
   }

   private static void particulas(Minecraft mc, SupernovaState s, float now) {
      RandomSource r = mc.level.getRandom();
      Vec3 c = s.center();
      float R = s.starRadius(now) * s.breath(now);
      float act = s.activation(now);
      int cuantas = act > 0.0F ? 3 + Math.round(6.0F * act) : 3;

      for (int i = 0; i < cuantas; i++) {
         double u = r.nextDouble() * 2.0 - 1.0;
         double a = r.nextDouble() * Math.PI * 2.0;
         double h = Math.sqrt(1.0 - u * u);
         Vec3 dir = new Vec3(h * Math.cos(a), u, h * Math.sin(a));
         double dist = R * (1.3 + r.nextDouble() * 0.9);
         Vec3 p = c.add(dir.scale(dist));
         Vec3 giro = new Vec3(-dir.z, 0.0, dir.x);
         if (giro.lengthSqr() < 1.0E-4) {
            giro = new Vec3(1.0, 0.0, 0.0);
         }

         giro = giro.normalize();
         boolean blanca = act > 0.0F && r.nextFloat() < 0.35F + 0.65F * act;
         double vel = blanca ? 0.25 + 0.35 * act : 0.12;
         Vec3 v = giro.scale(vel).add(dir.scale(blanca ? -0.18 - 0.3 * act : 0.05));
         mc.level.addAlwaysVisibleParticle(blanca ? ParticleTypes.END_ROD : ParticleTypes.FLAME, true, p.x, p.y, p.z, v.x, v.y, v.z);
      }
   }

   private static void clear() {
      for (SupernovaSounds.Musica m : MUSICAS.values()) {
         Minecraft.getInstance().getSoundManager().stop(m);
      }

      MUSICAS.clear();
      COLAPSO.clear();
      BLAST.clear();
      WAVE.clear();
      HUMS.clear();
   }

   private static class Hum extends AbstractTickableSoundInstance {
      private final int id;
      final float startedAt;

      Hum(int id, float startedAt) {
         super(SupernovaMod.SND_IDLE.get(), SoundSource.MASTER, RandomSource.create());
         this.id = id;
         this.startedAt = startedAt;
         this.looping = true;
         this.delay = 0;
         this.attenuation = Attenuation.NONE;
         this.relative = true;
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         this.volume = 0.01F;
         this.pitch = 1.0F;
      }

      public boolean canStartSilent() {
         return true;
      }

      public void tick() {
         Minecraft mc = Minecraft.getInstance();
         SupernovaState s = SupernovaGhosts.get(this.id);
         if (s != null
            && mc.level != null
            && mc.player != null
            && !(s.charge((float)mc.level.getGameTime()) > 0.0F)
            && s.dim.equals(mc.level.dimension().location().toString())) {
            double d = mc.player.getEyePosition().distanceTo(s.center());
            float close = (float)Math.max(0.0, 1.0 - d / (s.maxRadius * 3.0));
            float target = 0.12F * SupernovaSounds.near(s, d) + 0.6F * close * close;
            float fadeIn = SupernovaState.smooth(s.age((float)mc.level.getGameTime()) / 60.0F);
            this.volume = Math.max(0.001F, target * fadeIn);
         } else {
            this.stop();
         }
      }
   }

   private static class Musica extends AbstractTickableSoundInstance {
      private static final float DESVANECE = 60.0F;
      private final int id;
      private float base;
      private float apagando = -1.0F;

      public boolean canStartSilent() {
         return false;
      }

      Musica(int id, float volumen) {
         super(SupernovaMod.SND_MUSIC.get(), SoundSource.MASTER, RandomSource.create());
         this.id = id;
         this.base = Math.min(1.0F, volumen);
         this.looping = false;
         this.delay = 0;
         this.attenuation = Attenuation.NONE;
         this.relative = true;
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
         this.volume = this.base;
         this.pitch = 1.0F;
      }

      public void tick() {
         Minecraft mc = Minecraft.getInstance();
         SupernovaState s = SupernovaGhosts.get(this.id);
         boolean sigue = s != null && mc.level != null && s.dim.equals(mc.level.dimension().location().toString()) && !s.expired((float)mc.level.getGameTime());
         if (sigue) {
            float f = s.fade((float)mc.level.getGameTime());
            if (f <= 0.01F) {
               sigue = false;
            } else if (this.apagando < 0.0F) {
               this.volume = Math.max(0.001F, this.base * f);
            }
         }

         if (!sigue && this.apagando < 0.0F) {
            this.apagando = 0.0F;
            this.base = Math.max(0.001F, this.volume);
         }

         if (this.apagando >= 0.0F) {
            this.apagando++;
            float k = 1.0F - this.apagando / 60.0F;
            if (k <= 0.0F) {
               this.volume = 0.0F;
               this.stop();
               return;
            }

            this.volume = Math.max(0.001F, this.base * k * k);
         }
      }
   }
}
