package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilRuletaGiganteProcedure {
   private static final int[] PONDERADA = new int[]{1, 3, 4, 5, 6, 13, 17, 19, 20, 21, 22, 23, 24, 25, 26};
   private static final String[] NOMBRE = new String[]{
      "EXPLOSIÓN",
      "ÓRBITA",
      "RAYO DEL VACÍO",
      "AULLIDO",
      "LLUVIA",
      "GOLPE DE SUELO",
      "PUÑO MARCADO",
      "MANO ARRASTRADA",
      "APLAUSO",
      "SAL DE METEOROS",
      "AGUJERO NEGRO",
      "CÍRCULOS DE RAYOS",
      "BLOQUES ALZADOS",
      "AVALANCHA",
      "CAMPO ROJO"
   };
   private static final int TURNOS = 3;
   private static final int GIRO_TICKS = 34;
   private static final int DESCANSO_TICKS = 22;
   private static final int PASO_TICKS = 4;
   private static final int TOPE_TICKS = 420;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            reviil.ruletaHasta = level.getGameTime() + 34L + 420L;
            reviil.setAnimation("attack_emote_2");
            BossFx.alert(level, BossFx.centre(reviil), 170.0, "⚠ REVIIL: RULETA GIGANTE");
            BossFx.warningCircle(level, reviil, reviil.phaseBlastRadius(13.0), 34);
            BossFx.sound(level, BossFx.centre(reviil), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.9F, 1.15);
            ModmindsMod.queueServerWork(34, () -> gira(level, reviil, 0, -1));
         }
      }
   }

   private static void gira(ServerLevel world, ReviilEntity reviil, int turno, int anterior) {
      if (reviil.isAlive() && world.hasChunkAt(reviil.blockPosition())) {
         int elegido = tira(world, anterior);
         BossFx.alert(world, BossFx.centre(reviil), 180.0, "⚠ LA RULETA DICE " + nombre(elegido));
         BossFx.sound(world, BossFx.mouth(reviil, 1.5, 1.5), (SoundEvent)SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.HOSTILE, 2.2F, 1.45);
         BossFx.shake(world, BossFx.centre(reviil), 70.0, 8, 0.2);
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = elegido;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         ModmindsMod.queueServerWork(12, () -> esperaLibre(world, reviil, turno, elegido, 0));
      } else {
         reviil.ruletaHasta = 0L;
      }
   }

   private static void esperaLibre(ServerLevel world, ReviilEntity reviil, int turno, int anterior, int transcurrido) {
      if (reviil.isAlive() && reviil.escenaTicks <= 0) {
         String clip = reviil.getSyncedAnimation();
         boolean libre = (clip.equals("empty") || clip.equals("undefined"))
            && reviil.grabBeat() <= 0
            && ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 0.0;
         if (!libre && transcurrido < 420) {
            ModmindsMod.queueServerWork(4, () -> esperaLibre(world, reviil, turno, anterior, transcurrido + 4));
         } else if (turno + 1 >= 3) {
            reviil.ruletaHasta = 0L;
            BossFx.alert(world, BossFx.centre(reviil), 180.0, "⚠ LA RULETA SE GUARDA");
         } else {
            reviil.ruletaHasta = world.getGameTime() + 22L + 420L;
            ModmindsMod.queueServerWork(22, () -> gira(world, reviil, turno + 1, anterior));
         }
      } else {
         reviil.ruletaHasta = 0L;
      }
   }

   private static int tira(ServerLevel world, int anterior) {
      for (int intento = 0; intento < PONDERADA.length * 3; intento++) {
         int id = PONDERADA[world.getRandom().nextInt(PONDERADA.length)];
         if (id != anterior) {
            return id;
         }
      }

      return PONDERADA[0];
   }

   private static String nombre(int id) {
      for (int i = 0; i < PONDERADA.length; i++) {
         if (PONDERADA[i] == id) {
            return NOMBRE[i];
         }
      }

      return "OTRA COSA";
   }
}
