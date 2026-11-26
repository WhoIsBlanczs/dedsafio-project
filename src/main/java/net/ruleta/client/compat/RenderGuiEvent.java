package net.ruleta.client.compat;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class RenderGuiEvent extends Evento {
   private final GuiGraphicsExtractor grafico;
   private final float parcial;

   protected RenderGuiEvent(GuiGraphicsExtractor grafico, float parcial) {
      this.grafico = grafico;
      this.parcial = parcial;
   }

   public GuiGraphicsExtractor getGuiGraphics() {
      return this.grafico;
   }

   public float getPartialTick() {
      return this.parcial;
   }

   public Window getWindow() {
      return Minecraft.getInstance().getWindow();
   }

   public static final class Post extends RenderGuiEvent {
      public Post(GuiGraphicsExtractor grafico, float parcial) {
         super(grafico, parcial);
      }
   }

   public static final class Pre extends RenderGuiEvent {
      public Pre(GuiGraphicsExtractor grafico, float parcial) {
         super(grafico, parcial);
      }
   }
}
