package net.ruleta.estatua.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.ruleta.client.ui.Ui;
import net.ruleta.client.ui.UiFila;

abstract class FilaZonasPedestal extends UiFila {
   private final List<FilaZonasPedestal.Zona> zonas = new ArrayList<>();
   private int ox;
   private int oy;

   protected final void empezar(int x, int y) {
      this.zonas.clear();
      this.ox = x;
      this.oy = y;
   }

   protected final void zona(int rx, int ry, int w, int h, IntConsumer accion) {
      this.zonas.add(new FilaZonasPedestal.Zona(rx, ry, w, h, accion));
   }

   protected final void boton(GuiGraphicsExtractor g, Font f, int rx, int ry, int w, int h, String texto, int mx, int my, int color, boolean marcado, IntConsumer accion) {
      boolean hov = Ui.dentro(mx, my, this.ox + rx, this.oy + ry, w, h);
      int borde = marcado ? color : (hov ? color : 3028037);
      int relleno = marcado ? Ui.mezcla(1711656, color, 0.3F) : (hov ? 2369846 : 1119259);
      Ui.cajaBorde(g, this.ox + rx, this.oy + ry, w, h, 4, Ui.c(relleno), Ui.c(borde));
      Ui.textoCentro(g, f, texto, this.ox + rx + w / 2, this.oy + ry + (h - 8) / 2, 15330549, 1.0F, false);
      this.zona(rx, ry, w, h, accion);
   }

   protected final void cuadroItem(GuiGraphicsExtractor g, int rx, int ry, int w, int h, ItemStack pila, int mx, int my, int color, IntConsumer accion) {
      boolean hov = Ui.dentro(mx, my, this.ox + rx, this.oy + ry, w, h);
      Ui.cajaBorde(g, this.ox + rx, this.oy + ry, w, h, 4, Ui.c(hov ? 2369846 : 1119259), Ui.c(hov ? color : 3028037));
      if (!pila.isEmpty()) {
         g.renderItem(pila, this.ox + rx + (w - 16) / 2, this.oy + ry + (h - 16) / 2);
      }

      if (accion != null) {
         this.zona(rx, ry, w, h, accion);
      }
   }

   protected final void texto(GuiGraphicsExtractor g, Font f, String s, int rx, int ry, int color) {
      Ui.texto(g, f, s, this.ox + rx, this.oy + ry, color, 1.0F);
   }

   protected final void paso(GuiGraphicsExtractor g, Font f, int rx, int ry, String valor, int mx, int my, IntConsumer cambio) {
      this.boton(g, f, rx, ry, 20, 18, "−", mx, my, 7113983, false, b -> cambio.accept(-pasoActual()));
      Ui.textoCentro(g, f, valor, this.ox + rx + 20 + 4 + 22, this.oy + ry + 5, 15330549, 1.0F, false);
      this.boton(g, f, rx + 20 + 4 + 44 + 4, ry, 20, 18, "+", mx, my, 7113983, false, b -> cambio.accept(pasoActual()));
   }

   private static int pasoActual() {
      return Screen.hasShiftDown() ? 10 : 1;
   }

   @Override
   public boolean clic(double mx, double my, int boton, int x, int y, int w) {
      for (FilaZonasPedestal.Zona z : this.zonas) {
         if (Ui.dentro(mx, my, x + z.rx(), y + z.ry(), z.w(), z.h())) {
            z.accion().accept(boton);
            return true;
         }
      }

      return false;
   }

   private record Zona(int rx, int ry, int w, int h, IntConsumer accion) {
   }
}
