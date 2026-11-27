package net.ruleta.client.ui;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class UiInterruptor extends AbstractWidget {
   private final BooleanSupplier leer;
   private final Consumer<Boolean> escribir;
   private final Suave pos = new Suave();
   private final Suave hover = new Suave();
   private boolean primera = true;

   public UiInterruptor(int x, int y, BooleanSupplier leer, Consumer<Boolean> escribir) {
      super(x, y, 30, 16, Component.empty());
      this.leer = leer;
      this.escribir = escribir;
   }

   public void onClick(double mx, double my) {
      this.escribir.accept(!this.leer.getAsBoolean());
      Minecraft.getInstance()
         .getSoundManager()
         .play(SimpleSoundInstance.forUI((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), this.leer.getAsBoolean() ? 1.5F : 1.0F, 0.5F));
   }

   protected void extractWidgetRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
      boolean on = this.leer.getAsBoolean();
      if (this.primera) {
         this.pos.fijar(on ? 1.0F : 0.0F);
         this.primera = false;
      }

      float p = this.pos.hacia(on ? 1.0F : 0.0F, 14.0F);
      float h = this.hover.hacia(this.isHoveredOrFocused() ? 1.0F : 0.0F, 14.0F);
      int x = this.getX();
      int y = this.getY();
      int fondo = Ui.mezcla(2369846, 7113983, p);
      Ui.cajaBorde(g, x, y, 30, 16, 8, Ui.c(fondo), Ui.c(Ui.mezcla(3028037, 7113983, Math.max(p * 0.6F, h * 0.7F))));
      int bx = x + 3 + Math.round(14.0F * p);
      Ui.caja(g, bx, y + 3, 10, 10, 5, Ui.c(Ui.mezcla(9344683, 16777215, p)));
   }

   protected void updateWidgetNarration(NarrationElementOutput out) {
      this.defaultButtonNarrationText(out);
   }
}
