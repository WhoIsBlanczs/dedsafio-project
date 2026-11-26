package net.ruleta.client.compat;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class RenderGuiEvent extends Evento {
   private final GuiGraphics grafico;
   private final float parcial;

   protected RenderGuiEvent(GuiGraphics grafico, float parcial) {
      this.grafico = grafico;
      this.parcial = parcial;
   }

   public GuiGraphics getGuiGraphics() {
      return this.grafico;
   }

   public float getPartialTick() {
      return this.parcial;
   }

   public Window getWindow() {
      return Minecraft.getInstance().getWindow();
   }

   public static final class Post extends RenderGuiEvent {
      public Post(GuiGraphics grafico, float parcial) {
         super(grafico, parcial);
      }
   }

   public static final class Pre extends RenderGuiEvent {
      public Pre(GuiGraphics grafico, float parcial) {
         super(grafico, parcial);
      }
   }
}
