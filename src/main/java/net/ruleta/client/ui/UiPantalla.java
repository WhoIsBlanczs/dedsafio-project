package net.ruleta.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.ruleta.client.Fondo;

public abstract class UiPantalla extends Screen {
   protected final Screen padre;
   protected final Suave aparece = new Suave();
   protected int px;
   protected int py;
   protected int pw;
   protected int ph;
   private final String titulo;
   private String subtitulo = "";
   private UiBoton cerrar;
   private String[] icono;

   protected UiPantalla(String titulo, Screen padre) {
      super(Component.literal(titulo));
      this.titulo = titulo;
      this.padre = padre;

      this.icono = switch (titulo) {
         case "Ajustes" -> Iconos.ENGRANAJE;
         case "Eclipse" -> Iconos.ECLIPSE;
         case "Elegir jugador" -> Iconos.JUGADORES;
         case "Momentos Reviil" -> Iconos.ESTRELLA;
         case "Temporizador" -> Iconos.RELOJ;
         case "Elegir bunker", "Bunkers" -> Iconos.BUNKER;
         case "HUD" -> Iconos.PANTALLA;
         default -> null;
      };
   }

   protected void subtitulo(String s) {
      this.subtitulo = s == null ? "" : s;
   }

   protected void panel(int anchoMax, int altoMax) {
      EscalaGui.ajustar(this, anchoMax + 28, altoMax + 24);
      this.pw = Math.min(anchoMax, this.width - 28);
      this.ph = Math.min(altoMax, this.height - 24);
      this.px = (this.width - this.pw) / 2;
      this.py = (this.height - this.ph) / 2;
   }

   protected void cabecera(boolean conCerrar) {
      Fondo.poner();
      if (conCerrar) {
         this.cerrar = (UiBoton)this.addRenderableWidget(new UiBoton(this.px + this.pw - 30, this.py + 10, 20, 20, "×", UiBoton.Estilo.FANTASMA, this::onClose));
      }
   }

   protected int yContenido() {
      return this.py + 46;
   }

   public void removed() {
      EscalaGui.restaurar();
      Fondo.quitar();
      Ui.alfa = 1.0F;
   }

   public void renderBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float parcialFondo) {
   }

   protected void fondoUi(GuiGraphicsExtractor g) {
      float a = this.aparece.v;
      g.fillGradient(0, 0, this.width, this.height, Ui.c(329484, 0.55F * a), Ui.c(329484, 0.78F * a));
   }

   public void render(GuiGraphicsExtractor g, int mx, int my, float pt) {
      float a = this.aparece.hacia(1.0F, 11.0F);
      Ui.alfa = a;
      this.fondoUi(g);
      float e = Ui.ease(a);
      int m = Math.round((1.0F - e) * 8.0F);
      Ui.sombra(g, this.px + m, this.py + m, this.pw - m * 2, this.ph - m * 2, 8, 1.0F);
      Ui.cajaBorde(g, this.px + m, this.py + m, this.pw - m * 2, this.ph - m * 2, 8, Ui.c(1119259, 0.97F), Ui.c(3028037));
      int ix = this.px + m + 2;
      int iw = this.pw - m * 2 - 4;
      g.fillGradient(ix, this.py + m + 8, ix + iw, this.py + this.ph - m - 8, Ui.c(16777215, 0.035F), Ui.c(0, 0.1F));
      g.fillGradient(ix + 4, this.py + m + 1, ix + iw - 4, this.py + 40, Ui.c(7113983, 0.16F), Ui.c(7113983, 0.0F));
      int tramos = 24;

      for (int i = 0; i < tramos; i++) {
         float k = 1.0F - Math.abs(i - (tramos - 1) / 2.0F) / (tramos / 2.0F);
         int x0 = this.px + 14 + (this.pw - 28) * i / tramos;
         int x1 = this.px + 14 + (this.pw - 28) * (i + 1) / tramos;
         g.fill(x0, this.py + m + 1, x1, this.py + m + 2, Ui.c(7113983, 0.15F + 0.75F * k));
      }

      Font f = Minecraft.getInstance().font;
      int tx = this.px + 18;
      if (this.icono != null) {
         Ui.cajaBorde(g, this.px + 14, this.py + 9, 24, 24, 6, Ui.c(Ui.mezcla(1711656, 7113983, 0.28F)), Ui.c(7113983, 0.55F));
         g.fillGradient(this.px + 16, this.py + 11, this.px + 36, this.py + 21, Ui.c(16777215, 0.1F), Ui.c(16777215, 0.0F));
         Iconos.dibujar(g, this.icono, this.px + 26 - Iconos.ancho(this.icono) / 2, this.py + 21 - Iconos.alto(this.icono) / 2, 16777215, 0.95F);
         tx = this.px + 46;
      }

      Ui.textoN(g, f, this.titulo, tx, this.py + 12, 15330549, 1.0F);
      if (!this.subtitulo.isEmpty()) {
         Ui.texto(g, f, Ui.recortar(f, this.subtitulo, this.pw - 100), tx, this.py + 25, 9344683, 1.0F);
      }

      Ui.linea(g, this.px + 12, this.py + 40, this.pw - 24, Ui.c(3028037, 0.8F));
      this.contenido(g, mx, my, pt);
      super.render(g, mx, my, pt);
      this.encima(g, mx, my, pt);
      Ui.alfa = 1.0F;
   }

   protected abstract void contenido(GuiGraphicsExtractor var1, int var2, int var3, float var4);

   protected void encima(GuiGraphicsExtractor g, int mx, int my, float pt) {
   }

   public void onClose() {
      Minecraft.getInstance().setScreen(this.padre);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
