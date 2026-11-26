package net.ruleta.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.ruleta.Efectos;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.reviil.AnimacionReviil;
import net.ruleta.client.reviil.CieloReviil;

public final class EfectosCliente {
   private static final float TEMBLOR = 0.28F;
   private static final int TINTE_ALFA = 64;
   private static final int TINTE_COLOR = 14231592;
   private static final float TINTE_ANCHO = 0.34F;
   private static final float ENTRA = 10.0F;
   private static final float SALE = 40.0F;
   private static final Map<Holder<MobEffect>, Long> VISTO = new HashMap<>();
   private static long golpeDesde = -1L;
   private static float golpeFuerza;
   private static final float GOLPE_APAGA = 0.45F;
   private static final float TINTE_ENTRA = 0.5F;
   private static final float TINTE_APAGA = 3.0F;
   private static long tinteDesde = -1L;
   private static long tinteApaga = -1L;

   private EfectosCliente() {
   }

   private static float fuerza(Holder<MobEffect> efecto, float parcial) {
      LocalPlayer p = Minecraft.getInstance().player;
      if (p == null) {
         return 0.0F;
      } else {
         MobEffectInstance e = p.getEffect(efecto);
         if (e == null) {
            VISTO.remove(efecto);
            return 0.0F;
         } else {
            long ahora = System.nanoTime();
            long desde = VISTO.computeIfAbsent(efecto, k -> ahora);
            float queda = e.getDuration() - parcial;
            float pasado = (float)(ahora - desde) / 1.0E9F * 20.0F;
            return Math.min(1.0F, Math.max(0.0F, pasado / 10.0F)) * Math.min(1.0F, Math.max(0.0F, queda / 40.0F));
         }
      }
   }

   public static void golpe(float grados) {
      long ahora = System.nanoTime();
      if (golpeDesde < 0L || grados >= golpeAhora(ahora)) {
         golpeDesde = ahora;
         golpeFuerza = grados;
      }
   }

   private static float golpeAhora(long ahora) {
      if (golpeDesde < 0L) {
         return 0.0F;
      } else {
         float dt = (float)(ahora - golpeDesde) / 1.0E9F;
         if (dt > 1.8F) {
            golpeDesde = -1L;
            return 0.0F;
         } else {
            return golpeFuerza * (float)Math.exp(-dt / 0.45F);
         }
      }
   }

   public static void camara(ViewportEvent.ComputeCameraAngles e) {
      long ahora = System.nanoTime();
      float g = golpeAhora(ahora);
      if (g > 0.0F) {
         double tg = (ahora - golpeDesde) / 1.0E9;
         e.setYaw(e.getYaw() + (float)(Math.sin(tg * 31.0) * 0.7 + Math.sin(tg * 47.0 + 1.1) * 0.3) * g);
         e.setPitch(e.getPitch() + (float)(Math.sin(tg * 37.0 + 0.6) * 0.7 + Math.sin(tg * 53.0 + 2.0) * 0.3) * g);
         e.setRoll(e.getRoll() + (float)Math.sin(tg * 23.0 + 0.3) * g * 0.6F);
      }

      float f = fuerza(Efectos.TEMBLOR.get(), e.getPartialTick());
      if (!(f <= 0.0F)) {
         double t = System.nanoTime() / 1.0E9;
         float a = 0.28F * f;
         float yaw = (float)(Math.sin(t * 9.0) * 0.6 + Math.sin(t * 14.0 + 1.3) * 0.4) * a;
         float pitch = (float)(Math.sin(t * 11.0 + 2.1) * 0.6 + Math.sin(t * 17.0 + 0.4) * 0.4) * a;
         float roll = (float)(Math.sin(t * 7.0 + 0.7) * 0.5 + Math.sin(t * 12.0 + 2.6) * 0.5) * a * 0.6F;
         e.setYaw(e.getYaw() + yaw);
         e.setPitch(e.getPitch() + pitch);
         e.setRoll(e.getRoll() + roll);
      }
   }

   public static void tinte(GuiGraphicsExtractor ctx, float parcial) {
      long ahora = System.nanoTime();
      boolean efecto = fuerza(Efectos.RULETA_ROJA.get(), parcial) > 0.0F;
      if (tinteDesde < 0L) {
         if (!efecto) {
            return;
         }

         tinteDesde = ahora;
         tinteApaga = -1L;
      }

      boolean sigue = efecto || AnimacionReviil.musicaSonando() || CieloReviil.musicaMomentoSonando();
      if (sigue) {
         tinteApaga = -1L;
      } else if (tinteApaga < 0L) {
         tinteApaga = ahora;
      }

      float f = Math.min(1.0F, (float)(ahora - tinteDesde) / 1.0E9F / 0.5F);
      if (tinteApaga >= 0L) {
         float x = Math.min(1.0F, (float)(ahora - tinteApaga) / 1.0E9F / 3.0F);
         if (x >= 1.0F) {
            tinteDesde = -1L;
            tinteApaga = -1L;
            return;
         }

         f *= 1.0F - x * x * (3.0F - 2.0F * x);
      }

      int a = Math.round(64.0F * f);
      if (a > 0) {
         int w = ctx.guiWidth();
         int h = ctx.guiHeight();
         ctx.fill(0, 0, w, h, Math.round(a * 0.22F) << 24 | 14231592);
         int fuerte = a << 24 | 14231592;
         int nada = 14231592;
         int gx = Math.round(w * 0.34F);
         int gy = Math.round(h * 0.34F);
         ctx.fillGradient(0, 0, w, gy, fuerte, nada);
         ctx.fillGradient(0, h - gy, w, h, nada, fuerte);
         int tiras = 24;

         for (int i = 0; i < tiras; i++) {
            float k = 1.0F - (i + 0.5F) / tiras;
            int al = Math.round(a * k * k);
            int c = al << 24 | 14231592;
            int x0 = Math.round((float)gx * i / tiras);
            int x1 = Math.round((float)gx * (i + 1) / tiras);
            ctx.fill(x0, 0, x1, h, c);
            ctx.fill(w - x1, 0, w - x0, h, c);
         }
      }
   }

   public static void limpiarTinte() {
      tinteDesde = -1L;
      tinteApaga = -1L;
   }
}
