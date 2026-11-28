package net.owleafstudio.modminds.client.screens;

import com.mojang.renderpearl.backend.opengl.GlStateManager.DestFactor;
import com.mojang.renderpearl.backend.opengl.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.network.SubtitleMessage;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class SubtitleOverlay {
   private static final int SIDE_MARGIN = 24;
   private static final int BOTTOM_OFFSET = 88;
   private static final int LINE_H = 11;
   private static final int PADDING_X = 6;
   private static final int PADDING_Y = 4;
   private static final int BAND = -1610612736;
   private static final int OUTLINE = -16777216;
   private static final int TEXT = -1;

   private SubtitleOverlay() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onRenderOverlay(RenderGuiEvent.Pre event) {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      if (player != null && mc.font != null) {
         String text = SubtitleMessage.visibleText();
         if (text != null) {
            GuiGraphicsExtractor gui = event.getGuiGraphics();
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            int maxWidth = Math.max(60, w - 48);
            List<FormattedCharSequence> lines = mc.font.split(Component.literal(text), maxWidth);
            if (!lines.isEmpty()) {
               int blockW = 0;

               for (FormattedCharSequence line : lines) {
                  blockW = Math.max(blockW, mc.font.width(line));
               }

               int bandX0 = (w - blockW) / 2 - 6;
               int bandY1 = h - 88;
               int bandY0 = bandY1 - lines.size() * 11 - 4;
               int bandX1 = bandX0 + blockW + 12;
               gui.fill(bandX0, bandY0, bandX1, bandY1, -1610612736);
               int y = bandY0 + 4;

               for (FormattedCharSequence line : lines) {
                  int x = (w - mc.font.width(line)) / 2;

                  for (int dx = -1; dx <= 1; dx++) {
                     for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                           gui.drawString(mc.font, line, x + dx, y + dy, -16777216, false);
                        }
                     }
                  }

                  gui.drawString(mc.font, line, x, y, -1, false);
                  y += 11;
               }
            }
         }
      }
   }
}
