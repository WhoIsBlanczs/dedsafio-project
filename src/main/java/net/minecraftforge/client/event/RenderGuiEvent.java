package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.eventbus.api.Event;

public class RenderGuiEvent extends Event {
   private final GuiGraphicsExtractor guiGraphics;

   protected RenderGuiEvent(GuiGraphicsExtractor guiGraphics) {
      this.guiGraphics = guiGraphics;
   }

   public GuiGraphicsExtractor getGuiGraphics() {
      return this.guiGraphics;
   }

   public Window getWindow() {
      return Minecraft.getInstance().getWindow();
   }

   public org.joml.Matrix3x2fStack getPose() {
      return this.guiGraphics.pose();
   }

   public static class Post extends RenderGuiEvent {
      public Post(GuiGraphicsExtractor guiGraphics) {
         super(guiGraphics);
      }
   }

   public static class Pre extends RenderGuiEvent {
      public Pre(GuiGraphicsExtractor guiGraphics) {
         super(guiGraphics);
      }
   }
}
