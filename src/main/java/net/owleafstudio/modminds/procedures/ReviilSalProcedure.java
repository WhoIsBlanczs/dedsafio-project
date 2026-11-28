package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilSalProcedure {
   private static final int MANOS = 3;
   private static final int POR_MANO = 9;
   private static final int LEAD_TICKS = 34;
   private static final int WAVE_TICKS = 26;
   private static final int STAGGER_TICKS = 7;
   private static final double RADIO_MIN = 3.0;
   private static final double RADIO_MAX = 20.0;
   private static final double BLAST = 4.2;
   private static final double DAMAGE = 19.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            reviil.setAnimation("emote.giant.sal");
            BossFx.alert(level, BossFx.centre(reviil), 160.0, "⚠ REVIIL TIENE EL SUELO EN LA MANO");
            ReviilFx.rainCharge(level, reviil);
            BossFx.warningCircle(level, reviil, 9.0, 16);
            Vec3 centro = BossFx.aim(level, reviil, 200.0);
            BossFx.miraA(reviil, centro);
            RandomSource random = level.getRandom();

            for (int mano = 0; mano < 3; mano++) {
               int manoActual = mano;
               ModmindsMod.queueServerWork(
                  mano * 26,
                  () -> {
                     if (reviil.isAlive()) {
                        BossFx.sound(
                           level,
                           BossFx.mouth(reviil, 1.5, reviil.getBbHeight() * 0.7),
                           SoundEvents.SAND_FALL,
                           SoundSource.HOSTILE,
                           2.0F,
                           0.55 + manoActual * 0.1
                        );
                        BossFx.shake(level, BossFx.centre(reviil), 60.0, 6, 0.16);
                     }
                  }
               );

               for (int i = 0; i < 9; i++) {
                  double angle = random.nextDouble() * Math.PI * 2.0;
                  double distance = 3.0 + 17.0 * Math.sqrt(random.nextDouble());
                  Vec3 landing = surface(level, centro.x + Math.cos(angle) * distance, centro.z + Math.sin(angle) * distance);
                  int at = manoActual * 26 + 34 + i * 7 + random.nextInt(4);
                  BossFx.marker(level, landing, 4.2, at);
                  ReviilFx.meteorTelegraph(level, landing, at);
                  ModmindsMod.queueServerWork(at, () -> {
                     if (reviil.isAlive()) {
                        land(level, reviil, landing);
                     }
                  });
               }
            }

            ModmindsMod.queueServerWork(209, () -> reviil.releasePose());
         }
      }
   }

   private static void land(ServerLevel world, ReviilEntity reviil, Vec3 landing) {
      ReviilFx.meteorImpact(world, landing, 4.2);
      BossFx.marker(world, landing, 6.720000000000001, 6);
      BossFx.shake(world, landing, 44.0, 5, 0.14);

      for (Player victim : world.getEntitiesOfClass(
         Player.class, new AABB(landing, landing).inflate(6.2), p -> p.isAlive() && !p.isSpectator() && !p.isCreative()
      )) {
         if (victim.position().distanceTo(landing) <= 4.2 + victim.getBbWidth() * 0.5) {
            victim.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(19.0F));
            reviil.sting(victim);
            victim.setDeltaMovement(victim.getDeltaMovement().add(0.0, 0.5, 0.0));
            victim.needsSync = true;
            victim.igniteForSeconds(3.0F);
         }
      }
   }

   private static Vec3 surface(ServerLevel level, double x, double z) {
      return new Vec3(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z)), z);
   }
}
