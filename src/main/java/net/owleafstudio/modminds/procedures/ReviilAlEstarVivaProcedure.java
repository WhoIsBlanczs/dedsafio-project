package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (entity instanceof ReviilEntity reviil && reviil.escenaTicks > 0) {
            return;
         }

         if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            entity.lookAt(
               EntityAnchorArgument.Anchor.EYES,
               new Vec3(
                  (entity instanceof Mob _mobEntxxx ? _mobEntxxx.getTarget() : null).getX(),
                  (entity instanceof Mob _mobEntxx ? _mobEntxx.getTarget() : null).getY(),
                  (entity instanceof Mob _mobEntx ? _mobEntx.getTarget() : null).getZ()
               )
            );
         }

         if (entity instanceof ReviilEntity reviil) {
            double life = reviil.getHealth() / reviil.getMaxHealth();
            if (life <= 0.16 && reviil.escenaStage < 3) {
               ReviilEscenaProcedure.executeFinal(world, entity);
               return;
            }

            if (life <= 0.5 && reviil.escenaStage < 2) {
               ReviilEscenaProcedure.executeHalf(world, entity);
               return;
            }

            if (life <= 0.34 && reviil.phase() == 0) {
               ReviilFuriaProcedure.execute(world, entity);
               return;
            }
         }

         if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 1.0) {
            ReviilExplosionProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 2.0) {
            ReviilDisparoProcedure.execute(world, x, y, z, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 3.0) {
            OrbitaReviilProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 4.0) {
            RayoDeVacioReviilProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 5.0) {
            ReviilAullidoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 6.0) {
            ReviilLluviaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 7.0) {
            ReviilRuletaLetalProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 8.0) {
            ReviilCroupierProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 9.0) {
            ReviilTormentaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 10.0) {
            ReviilCoronaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 11.0) {
            ReviilAgarreProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 12.0) {
            ReviilEscenaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 13.0) {
            ReviilGolpeSueloProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 14.0) {
            ReviilFuriaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 15.0) {
            ReviilGiganteProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 16.0) {
            ReviilInvocarSoldadosProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 17.0) {
            ReviilPunoMarcadoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 18.0) {
            ReviilRuletaGiganteProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 19.0) {
            ReviilArrastrarManoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 20.0) {
            ReviilAplausoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 21.0) {
            ReviilSalProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 22.0) {
            ReviilAgujeroNegroProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 23.0) {
            ReviilCirculosRayosProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 24.0) {
            ReviilBloquesAlzandoseProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 25.0) {
            ReviilAvalanchaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 26.0) {
            ReviilCampoRojoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 27.0) {
            ReviilEmbestidaProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 28.0) {
            ReviilEncogerseProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).ReviilAtaques == 29.0) {
            ReviilZarpazoProcedure.execute(world, entity);
         }
      }
   }
}
