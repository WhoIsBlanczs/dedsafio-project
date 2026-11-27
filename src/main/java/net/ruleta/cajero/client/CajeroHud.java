package net.ruleta.cajero.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.RenderGuiEvent;
import net.ruleta.client.compat.SubscribeEvent;

@Environment(EnvType.CLIENT)
public final class CajeroHud {
   private static final int MARGEN = 4;
   private static final int ALTO = 17;
   private static final int RELLENO = 5;
   private static final int ENTRE_TEXTO_Y_FICHA = 3;
   private static final int ENTRE_PARTES = 6;

   private CajeroHud() {
   }

   @SubscribeEvent
   public static void dibujar(RenderGuiEvent.Post e) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && !mc.options.hideGui) {
         GuiGraphicsExtractor g = e.getGuiGraphics();
         Font f = mc.font;
         long[] p = CajeroCliente.partes(CajeroCliente.saldo);
         boolean[] ver = new boolean[]{p[0] > 0L, p[1] > 0L, p[2] > 0L};
         if (!ver[0] && !ver[1] && !ver[2]) {
            ver[2] = true;
         }

         int contenido = 0;
         int n = 0;

         for (int i = 0; i < 3; i++) {
            if (ver[i]) {
               contenido += f.width(Long.toString(p[i])) + 3 + 12;
               n++;
            }
         }

         contenido += (n - 1) * 6;
         int w = contenido + 10 + 2;
         int x = g.guiWidth() - w - 4;
         int y = 4;
         caja(g, x, y, w, 17);
         int cx = x + 1 + 5;

         for (int i = 0; i < 3; i++) {
            if (ver[i]) {
               String t = Long.toString(p[i]);
               g.text(f, t, cx, y + 5, -1, true);
               cx += f.width(t) + 3;
               g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, CajeroCliente.icono(i), cx, y + 2, 0, 0, 12, 13, 12, 13);
               cx += 18;
            }
         }
      }
   }

   private static void caja(GuiGraphicsExtractor g, int x, int y, int w, int h) {
      g.fill(x + 1, y, x + w - 1, y + h, -16777216);
      g.fill(x, y + 1, x + w, y + h - 1, -16777216);
      g.fill(x + 2, y + 1, x + w - 2, y + h - 1, -7697782);
      g.fill(x + 1, y + 2, x + w - 1, y + h - 2, -7697782);
      g.fill(x + 2, y + 2, x + w - 2, y + h - 2, -266724581);
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      CajeroCliente.saldo = 0L;
   }
}
