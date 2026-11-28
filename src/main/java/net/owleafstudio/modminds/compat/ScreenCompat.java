package net.owleafstudio.modminds.compat;

import java.lang.reflect.Field;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

/** Ayudas para pantallas: en 26.3 imageWidth/imageHeight son final y renderScrollingString cambió. */
@Environment(EnvType.CLIENT)
public final class ScreenCompat {
   private static Field width;
   private static Field height;

   private ScreenCompat() {
   }

   public static void setImageSize(AbstractContainerScreen<?> screen, int w, int h) {
      try {
         if (width == null) {
            width = AbstractContainerScreen.class.getDeclaredField("imageWidth");
            height = AbstractContainerScreen.class.getDeclaredField("imageHeight");
            width.setAccessible(true);
            height.setAccessible(true);
         }
         width.setInt(screen, w);
         height.setInt(screen, h);
      } catch (ReflectiveOperationException e) {
         throw new IllegalStateException("No se pudo cambiar el tamaño del panel", e);
      }
   }

   /** Igual que el renderScrollingString de 1.21.1: si el texto no entra, se desplaza de lado a lado. */
   public static void scrollingText(GuiGraphicsExtractor gui, Font font, Component text, int minX, int minY, int maxX, int maxY, int color) {
      int textWidth = font.width(text);
      int y = (minY + maxY - 9) / 2 + 1;
      int available = maxX - minX;
      if (textWidth > available) {
         int over = textWidth - available;
         double seconds = Util.getMillis() / 1000.0;
         double period = Math.max(over * 0.5, 3.0);
         double t = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * seconds / period)) / 2.0 + 0.5;
         double offset = Mth.lerp(t, 0.0, over);
         gui.enableScissor(minX, minY, maxX, maxY);
         gui.text(font, text, minX - (int)offset, y, color);
         gui.disableScissor();
      } else {
         int x = Mth.clamp((minX + maxX) / 2 - textWidth / 2, minX, maxX);
         gui.text(font, text, x, y, color);
      }
   }
}
