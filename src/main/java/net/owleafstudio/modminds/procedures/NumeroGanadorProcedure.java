package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.CasinoEntity;

public class NumeroGanadorProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         double NumeroPerdedor = 0.0;
         NumeroPerdedor = Mth.nextInt(RandomSource.create(), 1, 8);
         if (NumeroPerdedor == 1.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador1");
            }

            ModmindsMod.queueServerWork(80, () -> TresPlumasProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 2.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador2");
            }

            ModmindsMod.queueServerWork(80, () -> TresCosasRarasProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 3.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador3");
            }

            ModmindsMod.queueServerWork(80, () -> TresBrocolisProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 4.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador4");
            }

            ModmindsMod.queueServerWork(80, () -> TresDeditasProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 5.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador5");
            }

            ModmindsMod.queueServerWork(80, () -> TresNutriasProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 6.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador6");
            }

            ModmindsMod.queueServerWork(80, () -> TresCorazonesGanadoresProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 7.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador7");
            }

            ModmindsMod.queueServerWork(80, () -> TresOroProcedure.execute(sourceentity));
         } else if (NumeroPerdedor == 8.0) {
            if (entity instanceof CasinoEntity) {
               ((CasinoEntity)entity).setAnimation("ganador8");
            }

            ModmindsMod.queueServerWork(80, () -> TotemGanadoresProcedure.execute(sourceentity));
         }
      }
   }
}
