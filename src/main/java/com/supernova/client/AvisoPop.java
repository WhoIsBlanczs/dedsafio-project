package com.supernova.client;

import com.supernova.SupernovaState;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public final class AvisoPop {
   private static final long DURACION_MS = 4000L;
   private static final long ENTRA_MS = 280L;
   private static final long SALE_MS = 260L;
   private static final double ALCANCE = 3000.0;
   private static final Map<Integer, Long> INICIO = new HashMap<>();

   private AvisoPop() {
   }

   private static float easeOutBack(float t) {
      t = Mth.clamp(t, 0.0F, 1.0F);
      float c1 = 2.2F;
      float c3 = c1 + 1.0F;
      float u = t - 1.0F;
      return 1.0F + c3 * u * u * u + c1 * u * u;
   }

   public static void dibujar(GuiGraphicsExtractor g, int ancho, int alto) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         float now = (float)mc.level.getGameTime();
         long ms = Util.getMillis();
         String texto = null;
         long desde = 0L;

         for (SupernovaState s : SupernovaGhosts.active(now)) {
            if (!s.isDetonating() || s.countTicks(now) > 120.0F) {
               INICIO.remove(s.id);
            } else if (!(mc.player.position().distanceToSqr(s.position()) > 9000000.0)) {
               Long ini = INICIO.get(s.id);
               if (ini == null) {
                  ini = ms - (long)(s.countTicks(now) * 50.0F);
                  INICIO.put(s.id, ini);
               }

               if (ms - ini <= 4000L && (texto == null || ini < desde)) {
                  desde = ini;
                  texto = "La estrella colapsará en " + SupernovaState.countdown() / 20 + " segundos. ¡Aléjate todo lo que puedas!";
               }
            }
         }

         if (texto != null) {
            long t = ms - desde;
            float alfa = 1.0F;
            float escala;
            if (t < 280L) {
               escala = easeOutBack((float)t / 280.0F);
            } else if (t > 3740L) {
               float u = (float)(4000L - t) / 260.0F;
               escala = easeOutBack(u);
               alfa = Mth.clamp(u * 3.0F, 0.0F, 1.0F);
            } else {
               escala = 1.0F;
            }

            if (!(escala <= 0.01F)) {
               Font f = mc.font;
               int cx = ancho / 2;
               int cy = alto - 62;
               int a = Math.max(4, (int)(alfa * 255.0F));
               g.pose().pushPose();
               g.pose().translate(cx, cy, 0.0F);
               g.pose().scale(escala, escala, 1.0F);
               g.text(f, texto, -f.width(texto) / 2, -9 / 2, a << 24 | 16777215, true);
               g.pose().popPose();
            }
         }
      } else {
         INICIO.clear();
      }
   }
}
