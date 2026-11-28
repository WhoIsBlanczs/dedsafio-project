package net.ruleta;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class Efectos {
   public static final Reg<Holder<MobEffect>> TEMBLOR = Reg.efecto("ruletafanproject", "temblor", () -> new Efectos.Efecto(9071947));
   public static final Reg<Holder<MobEffect>> RULETA_ROJA = Reg.efecto("ruletafanproject", "ruleta_roja", () -> new Efectos.Efecto(12453900));
   public static final Reg<Holder<MobEffect>> MIOPIA = Reg.efecto("ruletafanproject", "miopia", () -> new Efectos.Efecto(10526880));
   public static final Reg<Holder<MobEffect>> DALTONISMO = Reg.efecto("ruletafanproject", "daltonismo", () -> new Efectos.Efecto(8421504));
   public static final int DURACION_PANTALLA_AZUL = 400;
   public static final Reg<Holder<MobEffect>> PANTALLA_AZUL = Reg.efecto("ruletafanproject", "pantalla_azul", () -> new Efectos.Efecto(30935));
   public static final Reg<Holder<MobEffect>> ZONA_SEGURA = Reg.efecto(
      "ruletafanproject", "zona_segura", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 5230955) {}
   );

   private Efectos() {
   }

   public static void init() {
   }

   public static boolean esDelMod(MobEffect efecto) {
      return efecto instanceof Efectos.Efecto;
   }

   private static final class Efecto extends MobEffect {
      Efecto(int color) {
         super(MobEffectCategory.HARMFUL, color);
      }
   }
}
