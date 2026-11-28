package com.supernova;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.ruleta.Ajustes;

public final class SupernovaAjustes {
   public static final float TAMANO_MIN = 1.0F;
   public static final float TAMANO_MAX = 2000.0F;
   public static final int TIEMPO_MAX_SEGUNDOS = 86400;
   public static float tamano = -1.0F;
   public static int tiempoTicks = 0;

   public static float tamano() {
      return tamano > 0.0F ? tamano : Ajustes.decimal("supernova.radio");
   }

   private SupernovaAjustes() {
   }

   public static float limitarTamano(float t) {
      return Mth.clamp(t, 1.0F, 2000.0F);
   }

   public static int limitarTicks(int ticks) {
      return Mth.clamp(ticks, 0, 1728000);
   }

   public static void alApagar(MinecraftServer servidor) {
      tamano = -1.0F;
      tiempoTicks = 0;
   }
}
