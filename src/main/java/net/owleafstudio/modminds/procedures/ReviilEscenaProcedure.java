package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.SceneRunner;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilEscenaProcedure {
   private static final int CRACK_TICKS = 90;
   private static final int PUNCH_TICKS = 48;
   private static final int PUNCH_IMPACT_TICKS = 10;
   private static final int SHOUT_IMPACT_TICKS = 8;
   private static final int PUNO_TICKS = 70;
   private static final int[][] SPEECH_1 = new int[][]{{0, 23}, {36, 69}, {83, 113}, {121, 149}, {162, 222}, {236, 347}, {358, 415}, {428, 447}, {459, 562}};
   private static final int[][] SPEECH_2 = new int[][]{
      {0, 50}, {65, 123}, {132, 234}, {250, 264}, {269, 323}, {334, 347}, {358, 375}, {383, 400}, {408, 434}, {443, 457}, {463, 527}, {536, 590}
   };
   private static final int[][] SPEECH_3 = new int[][]{{0, 44}, {60, 85}, {95, 113}, {126, 171}, {179, 270}, {287, 316}, {321, 337}, {346, 386}, {404, 506}};
   private static final int[][] SPEECH_4 = new int[][]{{0, 107}, {118, 155}, {163, 171}, {178, 239}, {253, 352}, {365, 480}};
   private static final int[][] SPEECH_5 = new int[][]{{0, 55}, {68, 107}, {119, 140}, {147, 332}, {345, 428}, {437, 492}};
   private static final int[][] SPEECH_6 = new int[][]{{0, 46}, {56, 101}, {116, 223}, {227, 282}, {298, 333}, {345, 404}, {417, 482}};
   private static final int[][] SPEECH_7 = new int[][]{
      {0, 23}, {38, 87}, {105, 154}, {172, 203}, {215, 237}, {247, 283}, {293, 340}, {349, 404}, {414, 491}, {500, 554}, {569, 608}
   };
   private static final int[][] SPEECH_8 = new int[][]{{0, 15}, {31, 130}, {137, 162}, {174, 267}, {274, 347}, {361, 389}, {397, 797}, {807, 904}};
   private static final int[][] SPEECH_9 = new int[][]{
      {0, 153}, {165, 282}, {291, 319}, {327, 403}, {425, 454}, {464, 522}, {540, 566}, {581, 646}, {652, 729}, {750, 780}
   };

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && reviil.escenaStage < 1) {
         reviil.escenaStage = 1;
      }

      run(world, entity, "⚠ REVIIL: LA RULETA HABLA", intro());
   }

   public static void executeHalf(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         reviil.escenaStage = Math.max(reviil.escenaStage, 2);
      }

      run(world, entity, "⚠ REVIIL: SANGRA", halfHealth());
   }

   public static void executeFinal(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil) {
         reviil.escenaStage = Math.max(reviil.escenaStage, 3);
      }

      run(world, entity, "⚠ REVIIL: SE NEGA A CAER", nearDeath());
   }

   private static void run(LevelAccessor world, Entity _entity, String banner, SceneRunner.Part[] parts) {
      if (_entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.releasePose();
         reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         int end = SceneRunner.run(level, reviil, parts, () -> {
            BossFx.alert(level, BossFx.centre(reviil), 96.0, banner);
            BossFx.shake(level, BossFx.centre(reviil), 40.0, 14, 0.22);
         }, () -> {
            reviil.escenaTicks = 0;
            reviil.releasePose();
         }, ReviilEscenaProcedure::floorPunch);
         reviil.escenaTicks = end + 5;
      }
   }

   private static void floorPunch(ServerLevel level, Entity boss) {
      if (boss instanceof ReviilEntity reviil) {
         Vec3 look = reviil.getLookAngle();
         Vec3 at = BossFx.groundAt(level, reviil.getX() + look.x * 2.2, reviil.getZ() + look.z * 2.2);
         BossFx.shake(level, BossFx.centre(reviil), 96.0, 11, 0.55);
         BossFx.sound(level, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.4F, 0.45F);
         BossFx.smashSmall(level, at, 6.0, 0.85, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
         BossFx.groundCrack(level, at, 9.0, 90);
      }
   }

   private static SceneRunner.Part[] intro() {
      return new SceneRunner.Part[]{
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_1.get(),
               "emote.talk.calm",
               572,
               new String[]{
                  "Agh... ¿Ustedes otra vez? Ya ni me enojo. Solo me cansan.",
                  "Los conozco mejor que ustedes mismos. Cada paso que dan lo dan por ego. Quieren ser el héroe que derrote al demonio, el nombre que repitan por generaciones.",
                  "Díganme, ¿cuándo fue la última vez que hicieron algo sin pensar en cómo los verían?"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 1, "emote.esc1.atras", 0),
                  new SceneRunner.Cue(0, 4, "emote.esc1.cadera", 0),
                  new SceneRunner.Cue(1, 6, "emote.esc1.niega", 0),
                  new SceneRunner.Cue(2, 0, "emote.esc1.encoge", 0)
               }
            )
            .withSpeech(SPEECH_1),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_2.get(),
               "emote.talk.calm",
               600,
               new String[]{
                  "Yo también tuve un hogar. Gente que me esperaba al caer la tarde. Sus ejércitos lo quemaron y ustedes se reían mientras mi pueblo caía.",
                  "¿Saben qué vi en sus ojos ese día? Nada. Ni culpa, ni miedo. Solo orgullo.",
                  "Ahí entendí que no destruyen por necesidad. Destruyen para sentirse grandes."
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 0, "emote.esc1.pecho", 0),
                  new SceneRunner.Cue(0, 18, "emote.esc1.rie", 0),
                  new SceneRunner.Cue(1, 0, "emote.esc1.horizon", 0),
                  new SceneRunner.Cue(2, 7, "emote.esc1.medida", 0)
               }
            )
            .withSpeech(SPEECH_2),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_3.get(),
               "emote.talk",
               516,
               new String[]{
                  "Entonces tomé la Ruleta. No me la dieron: la ROBÉ. Ya no tenía nada que perder.",
                  "Hice girar su destino y vi sus ciudades convertirse en polvo. ¿Y saben qué sentí? NADA. Eso fue lo que más me dolió.",
                  "Porque en ese momento me di cuenta de que ya me parecía a ustedes."
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 1, "emote.esc1.ruleta", 0),
                  new SceneRunner.Cue(1, 6, "emote.esc1.arraza", 0),
                  new SceneRunner.Cue(1, 15, "emote.esc1.encoge", 0),
                  new SceneRunner.Cue(2, 0, "emote.esc1.pecho", 0)
               }
            )
            .withSpeech(SPEECH_3),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_4.get(),
               "emote.talk.calm",
               490,
               new String[]{
                  "Me alejé a mi propio Egipto, lejos de sus coronas y sus aplausos. Solo quería silencio.",
                  "Pero el ego herido siempre vuelve a cobrar. No soportan que alguien los olvide.",
                  "No es valentía lo que los trae. Es no poder aceptar que un demonio les dio la espalda y siguió viviendo en paz."
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 1, "emote.esc1.horizon", 0),
                  new SceneRunner.Cue(0, 13, "emote.esc1.cadera", 0),
                  new SceneRunner.Cue(1, 8, "emote.esc1.niega", 0),
                  new SceneRunner.Cue(2, 15, "emote.esc1.atras", 0)
               }
            )
            .withSpeech(SPEECH_4),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_5.get(),
               "emote.talk",
               502,
               new String[]{
                  "¿Quieren saber cuál es su mayor mentira? Que lo hacen por los demás. Por su gente, por su reino, por el futuro.",
                  "¡MENTIRA! Lo hacen por el reflejo. Necesitan que alguien los mire y diga: él fue valiente.",
                  "Yo no los odio por ser débiles. Los odio porque NUNCA lo admiten."
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 0, "emote.esc1.encoge", 0),
                  new SceneRunner.Cue(1, 0, "emote.esc1.niega", 0),
                  new SceneRunner.Cue(2, 0, "emote.esc1.rie", 0)
               }
            )
            .withSpeech(SPEECH_5),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_6.get(),
               "emote.talk.calm",
               492,
               new String[]{
                  "Su ego es una cadena, y ustedes la llaman propósito. Yo solo tiro de ella y ustedes caminan hasta mi puerta, sonriendo.",
                  "¿Y si les dijera que ya perdieron? Que cada vez que vuelven, me dan la razón.",
                  "Díganme... ¿quién es el verdadero monstruo aquí?"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 3, "emote.esc1.cadena", 0),
                  new SceneRunner.Cue(1, 0, "emote.esc1.rie", 0),
                  new SceneRunner.Cue(2, 1, "emote.esc1.horizon", 0)
               }
            )
            .withSpeech(SPEECH_6),
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_7.get(),
               "emote.talk",
               618,
               new String[]{
                  "¡NECIOS! Les di la oportunidad de olvidarme. Era lo más cercano a piedad que me quedaba.",
                  "Los destruí. Volvieron. Los destruí otra vez. ¿Y aun así regresan?",
                  "Agh... Hay algo mucho peor que yo allá afuera, y ustedes ni lo imaginan...",
                  "¡PERO YA QUE INSISTEN! La Ruleta girará por última vez... ¡Y ACABARÉ CON USTEDES!!"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(0, 1, "emote.esc1.niega", 0),
                  new SceneRunner.Cue(1, 0, "emote.esc1.puno", 70),
                  new SceneRunner.Cue(2, 2, "emote.esc1.medida", 0),
                  new SceneRunner.Cue(2, 7, "emote.esc1.horizon", 0),
                  new SceneRunner.Cue(3, 0, "emote.shout", 0, 8)
               }
            )
            .withSpeech(SPEECH_7)
      };
   }

   private static SceneRunner.Part[] halfHealth() {
      return new SceneRunner.Part[]{
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_8.get(),
               "emote.scene2.grab",
               904,
               new String[]{
                  "¿Qué...? ¿Qué es esto? ¡NO! ¡NO OTRA VEZ!",
                  "Sangro... Yo, que giré la Ruleta con mis propias manos. ¿Cómo es que ustedes me hacen sangrar?",
                  "Ya lo viví... esta misma sensación. Ustedes avanzando, y yo cayendo de rodillas mientras todo arde.",
                  "¡ES SU EGO! ¡ES SU MALDITO EGO LO QUE LOS HACE VOLVER! ¿Por qué no se rinden?",
                  "Pero esta vez no. ¡ESTA VEZ NO!"
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(1, 0, "emote.scene2.look", 0),
                  new SceneRunner.Cue(2, 0, "emote.esc1.atras", 0),
                  new SceneRunner.Cue(3, 0, "emote.scene2.point", 0),
                  new SceneRunner.Cue(4, 4, "emote.shout", 0)
               }
            )
            .withSpeech(SPEECH_8)
      };
   }

   private static SceneRunner.Part[] nearDeath() {
      return new SceneRunner.Part[]{
         new SceneRunner.Part(
               ModmindsModSounds.REVIIL_LINE_9.get(),
               "emote.scene3.crouch",
               793,
               new String[]{
                  "No... NO... ¡NO PUEDE SER! ¡YO TENÍA LA RULETA! ¡LA TENÍA EN MIS MANOS!",
                  "¡PERDER CONTRA USTEDES!",
                  "¡OTRA VEZ USTEDES!",
                  "¡NO ES JUSTO!",
                  "Mi pueblo... las puertas abiertas al caer la tarde... Perdónenme... no pude protegerlos...",
                  "Pero escuchen... hay algo PEOR allá afuera. Algo que ni mi Ruleta pudo detener... Prepárense..."
               },
               new SceneRunner.Cue[]{
                  new SceneRunner.Cue(1, 0, "emote.scene3.punch.a", 48, 10),
                  new SceneRunner.Cue(2, 0, "emote.scene3.punch.b", 48, 10),
                  new SceneRunner.Cue(3, 0, "emote.scene3.punch.c", 48, 10),
                  new SceneRunner.Cue(4, 9, "emote.scene3.rise", 0),
                  new SceneRunner.Cue(5, 14, "emote.scene3.punch.a", 48, 10)
               }
            )
            .withSpeech(SPEECH_9)
      };
   }
}
