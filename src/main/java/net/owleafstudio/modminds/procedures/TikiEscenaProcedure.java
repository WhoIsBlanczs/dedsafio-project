package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.SceneRunner;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import org.joml.Vector3f;

public class TikiEscenaProcedure {
   private static final String DANCE = "animation.TikiGuardian.scene.dance.";
   private static final String DRUM = "animation.TikiGuardian.scene.drum.";
   private static final String GAZE = "animation.TikiGuardian.scene.gaze";
   private static final String POINT = "animation.TikiGuardian.scene.point.";
   private static final String TWIRL = "animation.TikiGuardian.scene.twirl";
   private static final String AGONY = "animation.TikiGuardian.scene.agony";
   private static final String HEAVE = "animation.TikiGuardian.scene.heave";
   private static final int DRUM_TICKS = 32;
   private static final int DRUM_IMPACT_TICKS = 15;
   private static final int FLOURISH_TICKS = 32;
   private static final int GAZE_TICKS = 64;
   private static final int GESTURE_TICKS = 48;
   private static final int HEAVE_TICKS = 32;
   private static final int HEAVE_IMPACT_TICKS = 8;
   private static final int AGONY_TICKS = 774;
   private static final int BLEED_GAP_TICKS = 45;
   private static final int FINAL_CRACK_TICKS = 100;
   private static final double BELLY_REACH = 0.41;
   private static final double BELLY_SIDE = 0.87;
   private static final double BELLY_HEIGHT = 2.29;
   private static final int[][] SPEECH_1 = new int[][]{
      {0, 31},
      {47, 81},
      {98, 142},
      {158, 182},
      {189, 219},
      {236, 282},
      {289, 314},
      {329, 367},
      {376, 402},
      {408, 440},
      {456, 491},
      {498, 524},
      {538, 583},
      {591, 633},
      {646, 673},
      {678, 722},
      {732, 793},
      {802, 852},
      {863, 890},
      {898, 917},
      {933, 983},
      {997, 1014},
      {1025, 1046},
      {1057, 1083},
      {1096, 1118},
      {1124, 1151},
      {1156, 1186},
      {1199, 1255},
      {1265, 1305},
      {1311, 1342}
   };
   private static final int[][] SPEECH_2 = new int[][]{
      {0, 28},
      {38, 72},
      {81, 100},
      {106, 140},
      {152, 204},
      {212, 246},
      {256, 316},
      {327, 377},
      {387, 406},
      {414, 448},
      {461, 502},
      {514, 547},
      {552, 568},
      {580, 610},
      {621, 669},
      {681, 714},
      {724, 765}
   };

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel level && tiki.isAlive()) {
         tiki.releasePose();
         tiki.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         int end = SceneRunner.run(level, tiki, script(), () -> {
            BossFx.alert(level, BossFx.centre(tiki), 96.0, "⚠ TIKI: EL TAMBOR LLAMA");
            BossFx.shake(level, BossFx.centre(tiki), 40.0, 12, 0.16);
         }, () -> {
            tiki.escenaTicks = 0;
            tiki.releasePose();
            BossFx.shake(level, BossFx.centre(tiki), 40.0, 20, 0.35);
            BossFx.groundCrack(level, BossFx.groundAt(level, tiki.getX(), tiki.getZ()), 16.0, 100);
            BossFx.sound(level, BossFx.centre(tiki), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.6F, 0.6F);
            BossFx.alert(level, BossFx.centre(tiki), 96.0, "⚠ TIKI: RESPONDE EL MONTE");
         }, TikiEscenaProcedure::bellyThump);
         tiki.escenaTicks = end + 5;
      }
   }

   private static void bellyThump(ServerLevel level, Entity boss) {
      Vec3 belly = bellyOf(boss);
      BossFx.shake(level, BossFx.centre(boss), 40.0, 7, 0.18);
      BossFx.sound(level, belly, ModmindsModSounds.HAMMER_2.get(), SoundSource.HOSTILE, 1.7F, 0.7, 0.18);
      BossFx.burst(level, belly, BossFx.CRUSHED_STONE, 14, 0.3, 0.35);
      BossFx.ring(level, belly, 1.9, 18, BossFx.CRUSHED_STONE, 2, 0.12);
   }

   private static Vec3 bellyOf(Entity boss) {
      double yaw = Math.toRadians(boss.getYRot());
      Vec3 forwards = new Vec3(-Math.sin(yaw), 0.0, Math.cos(yaw));
      Vec3 armSide = new Vec3(-Math.cos(yaw), 0.0, -Math.sin(yaw));
      return boss.position()
         .add(forwards.x * 0.41 + armSide.x * 0.87, 2.29, forwards.z * 0.41 + armSide.z * 0.87);
   }

   private static SceneRunner.Part[] script() {
      return new SceneRunner.Part[]{
         new SceneRunner.Part(
               ModmindsModSounds.TIKI_LINE_1.get(),
               "animation.TikiGuardian.scene.dance.a",
               1350,
               new String[]{
                  "...Silencio.",
                  "Tierra temblar. Tiki sentir pasos. Pequeños. Extraños.",
                  "Ustedes oler a hierro. A miedo. Todos venir igual: querer tomar, nunca pedir.",
                  "Yo ser Tiki. Guardián. Cuando mundo ser joven, Tiki ya estar aquí.",
                  "Bajo arena, ancestros escuchar... Ellos oír su respiración.",
                  "Tiki dar una oportunidad. Una sola. Correr.",
                  "...Tiki contar hasta tres.",
                  "Uno...",
                  "Dos...",
                  "...¿No irse?",
                  "¡GRRR! ¡ANCESTROS, DESPERTAR! ¡NADIE TOCAR LO SAGRADO!",
                  "¡TIKI SER MURO! ¡RAAAAAH!"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(1, 0, "animation.TikiGuardian.scene.gaze", 64),
                  new SceneRunner.Cue(2, 3, "animation.TikiGuardian.scene.point.a", 48),
                  new SceneRunner.Cue(3, 0, "animation.TikiGuardian.scene.drum.a", 32, 15),
                  new SceneRunner.Cue(4, 4, "animation.TikiGuardian.scene.twirl", 48),
                  new SceneRunner.Cue(5, 5, "animation.TikiGuardian.scene.point.b", 48),
                  new SceneRunner.Cue(6, 0, "animation.TikiGuardian.scene.dance.b", 32),
                  new SceneRunner.Cue(7, 0, "animation.TikiGuardian.scene.drum.b", 32, 15),
                  new SceneRunner.Cue(8, 0, "animation.TikiGuardian.scene.drum.c", 32, 15),
                  new SceneRunner.Cue(9, 0, "animation.TikiGuardian.scene.dance.c", 32),
                  new SceneRunner.Cue(10, 0, "animation.TikiGuardian.scene.drum.a", 32, 15),
                  new SceneRunner.Cue(11, 0, "animation.TikiGuardian.scene.twirl", 48)
               }
            )
            .withSpeech(SPEECH_1)
      };
   }

   public static void executeAgonia(LevelAccessor world, Entity entity) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel level && tiki.isAlive()) {
         tiki.releasePose();
         tiki.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         int end = SceneRunner.run(level, tiki, agoniaScript(), () -> {
            BossFx.alert(level, BossFx.centre(tiki), 96.0, "⚠ TIKI: LA SAVIA CAE");
            BossFx.shake(level, BossFx.centre(tiki), 40.0, 10, 0.12);
         }, () -> {
            tiki.escenaTicks = 0;
            tiki.releasePose();
            BossFx.shake(level, BossFx.centre(tiki), 40.0, 16, 0.28);
            BossFx.groundCrack(level, BossFx.groundAt(level, tiki.getX(), tiki.getZ()), 12.0, 100);
            BossFx.sound(level, BossFx.centre(tiki), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 0.5);
            BossFx.alert(level, BossFx.centre(tiki), 96.0, "⚠ TIKI: SE LEVANTA");
         }, TikiEscenaProcedure::sangreVerde);
         tiki.escenaTicks = end + 5;
         bleed(level, tiki, end);
      }
   }

   private static void sangreVerde(ServerLevel level, Entity boss) {
      Vec3 chest = BossFx.centre(boss).add(0.0, 0.6, 0.0);
      BossFx.colored(level, chest, new Vector3f(0.12F, 0.72F, 0.2F), 1.3F, 16, 0.45);
      BossFx.burst(level, chest, BossFx.CRUSHED_STONE, 10, 0.35, 0.3);
      BossFx.sound(level, chest, ModmindsModSounds.HAMMER_3.get(), SoundSource.HOSTILE, 1.4F, 0.85, 0.1);
      BossFx.shake(level, BossFx.centre(boss), 40.0, 6, 0.12);
   }

   private static void bleed(ServerLevel level, TikiGuardianEntity tiki, int untilTick) {
      if (untilTick > 0 && tiki.isAlive()) {
         ModmindsMod.queueServerWork(45, () -> {
            if (tiki.isAlive() && tiki.escenaTicks > 0) {
               Vec3 wound = BossFx.mouth(tiki, 0.5, 0.4);
               BossFx.colored(level, wound, new Vector3f(0.1F, 0.65F, 0.18F), 1.0F, 6, 0.3);
               bleed(level, tiki, untilTick - 45);
            }
         });
      }
   }

   private static SceneRunner.Part[] agoniaScript() {
      return new SceneRunner.Part[]{
         new SceneRunner.Part(
               ModmindsModSounds.TIKI_LINE_2.get(),
               "animation.TikiGuardian.scene.agony",
               774,
               new String[]{
                  "...Tiki sangrar. Piedra romperse. Brazos... muy pesados.",
                  "Tiki recordar fuego de tribu. Risas de niños. Canto de ancestros bajo la luna.",
                  "Tiki prometer cuidar todo. Siempre... siempre cuidar.",
                  "¿Por qué ustedes venir? Tiki solo querer... paz.",
                  "...Tiki cansado. Pero Tiki no poder caer. Si Tiki caer... ¿quién recordar tribu?"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 0, "animation.TikiGuardian.scene.heave", 32, 8),
                  new SceneRunner.Cue(1, 0, "animation.TikiGuardian.scene.heave", 32, 8),
                  new SceneRunner.Cue(3, 0, "animation.TikiGuardian.scene.heave", 32, 8),
                  new SceneRunner.Cue(4, 8, "animation.TikiGuardian.scene.heave", 32, 8)
               }
            )
            .withSpeech(SPEECH_2)
      };
   }
}
