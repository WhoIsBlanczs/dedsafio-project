package net.ruleta.client.ui;

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

   public boolean mouseClicked(double mx, double my, int boton) { return false; }

   public void renderWidget(GuiGraphicsExtractor g, int mx, int my, float pt) { }
}
