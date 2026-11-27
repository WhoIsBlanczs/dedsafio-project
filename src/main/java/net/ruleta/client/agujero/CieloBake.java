package net.ruleta.client.agujero;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CieloBake {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final int RES_NEBULOSA = 768;
   private static final int RES_CONSTELACIONES = 1024;
   private static final int CARAS = 12;
   private static Object shader;
   private static RenderTarget nebulosa;
   private static RenderTarget constelaciones;
   private static int hechas;
   private static boolean fallo;

   private CieloBake() {
   }

   public static void shader(Object s) {
      shader = s;
      fallo = false;
      liberar();
   }

   public static boolean listo() {
      return hechas >= 12 && nebulosa != null && constelaciones != null;
   }

   public static boolean fallo() {
      return fallo;
   }

   public static RenderTarget nebulosa() {
      return nebulosa;
   }

   public static RenderTarget constelaciones() {
      return constelaciones;
   }

   public static boolean avanzar() {
      if (listo()) {
         return true;
      }

      if (shader != null && !fallo) {
         try {
            if (nebulosa == null) {
               nebulosa = crear(2304, 1536);
               constelaciones = crear(3072, 2048);
               hechas = 0;
            }

            boolean esNebulosa = hechas < 6;
            int cara = hechas % 6;
            int res = esNebulosa ? 768 : 1024;
            dibujarCara(esNebulosa ? nebulosa : constelaciones, res, cara, esNebulosa ? 0.0F : 1.0F);
            hechas++;
         } catch (RuntimeException ex) {
            LOG.warn("No se pudo hornear el cielo del agujero negro", ex);
            fallo = true;
            liberar();
         }

         return listo();
      } else {
         return false;
      }
   }

   public static void liberar() {
      if (nebulosa != null) {
         nebulosa.destroyBuffers();
         nebulosa = null;
      }

      if (constelaciones != null) {
         constelaciones.destroyBuffers();
         constelaciones = null;
      }

      hechas = 0;
   }

   private static RenderTarget crear(int ancho, int alto) { return null; }

   private static void dibujarCara(RenderTarget destino, int res, int cara, float modo) { }
}
