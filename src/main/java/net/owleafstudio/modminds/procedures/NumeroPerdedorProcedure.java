package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.owleafstudio.modminds.entity.CasinoEntity;

public class NumeroPerdedorProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof CasinoEntity) {
         ((CasinoEntity)entity).setAnimation("aleatoria" + Mth.nextInt(RandomSource.create(), 1, 60));
      }
   }
}
