package net.owleafstudio.modminds.boss;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.ReviilEntity;

public final class SceneRunner {
   public static final int INTRO_TICKS = 30;
   public static final int PART_GAP_TICKS = 25;
   public static final int OUTRO_TICKS = 30;
   private static final int REPEAT_TICKS = 40;
   private static final int SIN_CORTE = -1;
   private static final int MAX_CUE_CHARS = 58;
   private static final int MIN_CUE_TICKS = 55;
   private static final double LISTENING_RADIUS = 256.0;
   private static final int BREATH_WINDOW_TICKS = 100;

   public static int run(ServerLevel level, SceneRunner.Actor actor, SceneRunner.Part[] parts, Runnable opening, Runnable ending, SceneRunner.Impact impact) {
      Entity boss = actor.sceneEntity();
      beat(level, actor, 0, opening);
      int tick = 30;

      for (SceneRunner.Part part : parts) {
         int start = tick;
         int[] bounds = bounds(part);
         beat(level, actor, tick, () -> {
            actor.setAnimation(part.pose());
            BossFx.sound(level, BossFx.centre(boss), part.voice(), SoundSource.RECORDS, 4.0F, 1.0);
         });

         for (int i = 0; i < part.beats().length; i++) {
            say(level, boss, part.beats()[i], start + bounds[i], start + bounds[i + 1]);
         }

         for (SceneRunner.Cue cue : part.cues()) {
            int at = start + cueTick(part, bounds, cue);
            beat(level, actor, at, () -> actor.setAnimation(cue.pose()));
            if (cue.impactTicks() >= 0) {
               beat(level, actor, at + cue.impactTicks(), () -> impact.hit(level, boss));
            }

            if (cue.holdTicks() > 0) {
               beat(level, actor, at + cue.holdTicks(), () -> actor.setAnimation(part.pose()));
            }
         }

         tick = start + part.ticks() + 25;
      }

      int end = tick + 30;
      beat(level, actor, end, ending);
      return end;
   }

   private static int[] bounds(SceneRunner.Part part) {
      int total = 0;

      for (String beat : part.beats()) {
         total += words(beat);
      }

      int[] bounds = new int[part.beats().length + 1];
      int spoken = 0;

      for (int i = 0; i < part.beats().length; i++) {
         spoken += words(part.beats()[i]);
         if (i + 1 == part.beats().length) {
            bounds[i + 1] = part.ticks();
         } else if (part.speech().length == 0) {
            bounds[i + 1] = (int)Math.round((double)part.ticks() * spoken / total);
         } else {
            int wall = wallAt(part.speech(), speechTicks(part.speech(), spoken, total));
            int breath = nextBreath(part.speech(), wall);
            bounds[i + 1] = breath > 0 && breath - wall <= 100 ? breath : wall;
         }

         bounds[i + 1] = Math.max(bounds[i + 1], bounds[i] + 55);
      }

      return bounds;
   }

   private static int speechTicks(int[][] runs, int spoken, int total) {
      int voice = 0;

      for (int[] run : runs) {
         voice += run[1] - run[0];
      }

      return (int)Math.round((double)voice * spoken / total);
   }

   private static int wallAt(int[][] runs, int target) {
      int spent = 0;

      for (int[] run : runs) {
         if (spent + run[1] - run[0] >= target) {
            return run[0] + target - spent;
         }

         spent += run[1] - run[0];
      }

      return runs[runs.length - 1][1];
   }

   private static int nextBreath(int[][] runs, int wall) {
      for (int[] run : runs) {
         if (run[0] > wall) {
            return run[0];
         }
      }

      return -1;
   }

   private static int cueTick(SceneRunner.Part part, int[] bounds, SceneRunner.Cue cue) {
      int line = bounds[cue.atBeat() + 1] - bounds[cue.atBeat()];
      return bounds[cue.atBeat()] + (int)Math.round((double)line * cue.wordsIn() / words(part.beats()[cue.atBeat()]));
   }

   private static int words(String text) {
      int count = 0;
      boolean inWord = false;

      for (int i = 0; i < text.length(); i++) {
         boolean space = text.charAt(i) == ' ';
         if (!space && !inWord) {
            count++;
         }

         inWord = !space;
      }

      return Math.max(1, count);
   }

   public static void speak(ServerLevel level, Entity boss, String paragraph, int ticks) {
      say(level, boss, paragraph, 0, ticks, corte(boss));
   }

   private static int corte(Entity boss) {
      return boss instanceof ReviilEntity reviil ? reviil.hablaCorte() : -1;
   }

   private static void say(ServerLevel level, Entity boss, String paragraph, int from, int until) {
      say(level, boss, paragraph, from, until, -1);
   }

   private static void say(ServerLevel level, Entity boss, String paragraph, int from, int until, int corte) {
      List<String> cues = cues(paragraph, Math.max(1, (until - from) / 55));
      int span = Math.max(1, (until - from) / cues.size());

      for (int i = 0; i < cues.size(); i++) {
         int start = from + i * span;
         show(level, boss, cues.get(i), start, i + 1 == cues.size() ? until : start + span, corte);
      }
   }

   private static List<String> cues(String paragraph, int maxCues) {
      List<String> cues = new ArrayList<>();
      StringBuilder cue = new StringBuilder();

      for (String word : paragraph.split(" ")) {
         if (cue.length() > 0 && cue.length() + 1 + word.length() > 58) {
            cues.add(cue.toString());
            cue.setLength(0);
         }

         if (cue.length() > 0) {
            cue.append(' ');
         }

         cue.append(word);
      }

      if (cue.length() > 0) {
         cues.add(cue.toString());
      }

      while (cues.size() > maxCues) {
         int at = 0;

         for (int i = 1; i + 1 < cues.size(); i++) {
            if (cues.get(i).length() + cues.get(i + 1).length() < cues.get(at).length() + cues.get(at + 1).length()) {
               at = i;
            }
         }

         cues.set(at, cues.get(at) + " " + cues.get(at + 1));
         cues.remove(at + 1);
      }

      return cues;
   }

   private static void show(ServerLevel level, Entity boss, String text, int from, int until, int corte) {
      for (int repeat = from; repeat < until; repeat += 40) {
         int shows = Math.max(40, until - repeat);
         beat(level, boss, repeat, corte, () -> BossFx.subtitle(level, BossFx.centre(boss), 256.0, text, shows));
      }
   }

   private static void beat(ServerLevel level, SceneRunner.Actor actor, int tick, Runnable action) {
      beat(level, actor.sceneEntity(), tick, -1, action);
   }

   private static void beat(ServerLevel level, Entity boss, int tick, int corte, Runnable action) {
      if (action != null) {
         ModmindsMod.queueServerWork(tick, () -> {
            if (boss.isAlive() && !level.isClientSide() && (corte == -1 || corte == corte(boss))) {
               action.run();
            }
         });
      }
   }

   private SceneRunner() {
   }

   public interface Actor {
      Entity sceneEntity();

      void setAnimation(String var1);

      void releasePose();
   }

   public record Cue(int atBeat, int wordsIn, String pose, int holdTicks, int impactTicks) {
      public Cue(int atBeat, int wordsIn, String pose, int holdTicks) {
         this(atBeat, wordsIn, pose, holdTicks, -1);
      }

      public Cue(String pose) {
         this(0, 0, pose, 0, -1);
      }
   }

   public interface Impact {
      void hit(ServerLevel var1, Entity var2);
   }

   public record Part(SoundEvent voice, String pose, int ticks, String[] beats, SceneRunner.Cue[] cues, int[][] speech) {
      public Part(SoundEvent voice, String pose, int ticks, String[] beats) {
         this(voice, pose, ticks, beats, new SceneRunner.Cue[0], new int[0][]);
      }

      public Part(SoundEvent voice, String pose, int ticks, String[] beats, SceneRunner.Cue[] cues) {
         this(voice, pose, ticks, beats, cues, new int[0][]);
      }

      public SceneRunner.Part withSpeech(int[][] speech) {
         return new SceneRunner.Part(this.voice, this.pose, this.ticks, this.beats, this.cues, speech);
      }
   }
}
