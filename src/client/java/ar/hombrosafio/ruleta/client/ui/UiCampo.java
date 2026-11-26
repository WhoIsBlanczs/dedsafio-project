package ar.hombrosafio.ruleta.client.ui;

import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class UiCampo extends EditBox {
   private final int ox;
   private final int oy;
   private final int ow;
   private final int oh;
   private final Suave foco = new Suave();
   private BooleanSupplier malo = () -> false;

   public UiCampo(Font font, int x, int y, int w, int h, Component mensaje) {
      super(font, x + 6, y + (h - 8) / 2, w - 12, 10, mensaje);
      this.setBordered(false);
      this.ox = x;
      this.oy = y;
      this.ow = w;
      this.oh = h;
   }

   public UiCampo malo(BooleanSupplier malo) {
      this.malo = malo;
      return this;
   }

   public int xExterior() {
      return this.ox;
   }

   public int yExterior() {
      return this.oy;
   }

   public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent ev, boolean dbl) {
      if (this.active && this.visible && Ui.dentro(ev.x(), ev.y(), this.ox, this.oy, this.ow, this.oh)) {
         this.setFocused(true);
         return true;
      }
      return super.mouseClicked(ev, dbl);
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
      float f = this.foco.hacia(this.isFocused() ? 1.0F : 0.0F, 14.0F);
      boolean rojo = this.malo.getAsBoolean() && !this.getValue().isEmpty();
      int borde = rojo ? 16735340 : Ui.mezcla(3028037, 7113983, f);
      Ui.cajaBorde(g, this.ox, this.oy, this.ow, this.oh, 4, Ui.c(1711656), Ui.c(borde));
      if (f > 0.02F) {
         Ui.caja(g, this.ox + 4, this.oy + this.oh - 2, Math.round((this.ow - 8) * f), 1, 0, Ui.c(rojo ? 16735340 : 7113983));
      }

      super.extractWidgetRenderState(g, mx, my, pt);
   }
}
