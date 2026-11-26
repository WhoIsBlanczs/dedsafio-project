package net.ruleta.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class UiBoton extends AbstractButton {
   private final Runnable alPulsar;
   private UiBoton.Estilo estilo;
   private String texto;
   private final Suave hover = new Suave();
   private final Suave pulso = new Suave();
   private boolean marcado;
   private String[] icono;

   public UiBoton(int x, int y, int w, int h, String texto, UiBoton.Estilo estilo, Runnable alPulsar) {
      super(x, y, w, h, Component.literal(texto));
      this.texto = texto;
      this.estilo = estilo;
      this.alPulsar = alPulsar;
   }

   public UiBoton(int x, int y, int w, int h, String texto, Runnable alPulsar) {
      this(x, y, w, h, texto, UiBoton.Estilo.NORMAL, alPulsar);
   }

   public UiBoton ayuda(String ayuda) {
      this.setTooltip(Tooltip.create(Component.literal(ayuda)));
      return this;
   }

   public UiBoton icono(String[] i) {
      this.icono = i;
      return this;
   }

   public void setTexto(String t) {
      this.texto = t;
      this.setMessage(Component.literal(t));
   }

   public String texto() {
      return this.texto;
   }

   public void setEstilo(UiBoton.Estilo e) {
      this.estilo = e;
   }

   public void setMarcado(boolean m) {
      this.marcado = m;
   }

   public void onPress() {
      this.pulso.fijar(1.0F);
      this.alPulsar.run();
   }

   public void playDownSound(SoundManager sm) {
      sm.play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.25F, 0.55F));
   }

   protected void renderWidget(GuiGraphics g, int mx, int my, float pt) {
      Font f = Minecraft.getInstance().font;
      float h = this.hover.hacia(this.isHoveredOrFocused() && this.active ? 1.0F : 0.0F, 16.0F);
      float p = this.pulso.hacia(0.0F, 7.0F);
      int x = this.getX();
      int y = this.getY();
      int w = this.getWidth();
      int hh = this.getHeight();
      int hundido = p > 0.5F ? 1 : 0;
      int fondoBase;
      int fondoHover;
      int textoRgb;
      int acento;
      switch (this.estilo) {
         case PRIMARIO:
            fondoBase = Ui.mezcla(7113983, 1119259, 0.25F);
            fondoHover = 7113983;
            textoRgb = 16777215;
            acento = 16777215;
            break;
         case NORMAL:
         default:
            fondoBase = 1711656;
            fondoHover = 2369846;
            textoRgb = 15330549;
            acento = 7113983;
            break;
         case PELIGRO:
            fondoBase = Ui.mezcla(16735340, 1119259, 0.78F);
            fondoHover = 16735340;
            textoRgb = Ui.mezcla(16735340, 16777215, 0.35F);
            acento = 16735340;
            break;
         case FANTASMA:
            fondoBase = 1119259;
            fondoHover = 2369846;
            textoRgb = 9344683;
            acento = 7113983;
      }

      if (this.marcado) {
         fondoBase = Ui.mezcla(7113983, 1711656, 0.55F);
         textoRgb = 16777215;
      }

      float a = this.active ? 1.0F : 0.4F;
      int fondo = Ui.mezcla(fondoBase, fondoHover, h);
      if (this.estilo == UiBoton.Estilo.FANTASMA && !this.marcado) {
         Ui.caja(g, x, y + hundido, w, hh, 4, Ui.c(fondo, h * a));
      } else {
         Ui.cajaBorde(g, x, y + hundido, w, hh, 4, Ui.c(fondo, a), Ui.c(Ui.mezcla(3028037, acento, h * 0.8F), a));
      }

      if (this.estilo != UiBoton.Estilo.FANTASMA || this.marcado || h > 0.05F) {
         g.fillGradient(x + 2, y + 2 + hundido, x + w - 2, y + hh / 2 + hundido, Ui.c(16777215, (0.07F + 0.07F * h) * a), Ui.c(16777215, 0.0F));
         g.fillGradient(x + 2, y + hh / 2 + hundido, x + w - 2, y + hh - 2 + hundido, Ui.c(0, 0.0F), Ui.c(0, 0.16F * a));
      }

      int barra = Math.round((w - 12) * h);
      if (barra > 1 && this.estilo != UiBoton.Estilo.PRIMARIO) {
         Ui.caja(g, x + (w - barra) / 2, y + hh - 2 + hundido, barra, 1, 0, Ui.c(acento, a));
      }

      if (p > 0.02F) {
         Ui.caja(g, x, y, w, hh, 4, Ui.c(16777215, p * 0.22F));
      }

      int iw = this.icono != null ? Iconos.ancho(this.icono) + 5 : 0;
      String t = Ui.recortar(f, this.texto, w - 10 - iw);
      int tw = Ui.anchoN(f, t);
      int x0 = x + (w - tw - iw) / 2;
      if (this.icono != null) {
         Iconos.dibujar(g, this.icono, x0, y + (hh - Iconos.alto(this.icono)) / 2 + hundido, Ui.mezcla(textoRgb, acento, 0.35F * (1.0F - h)), a);
      }

      Ui.textoN(g, f, t, x0 + iw, y + (hh - 8) / 2 + hundido, textoRgb, a);
   }

   protected void updateWidgetNarration(NarrationElementOutput out) {
      this.defaultButtonNarrationText(out);
   }

   public static enum Estilo {
      PRIMARIO,
      NORMAL,
      PELIGRO,
      FANTASMA;
   }
}
