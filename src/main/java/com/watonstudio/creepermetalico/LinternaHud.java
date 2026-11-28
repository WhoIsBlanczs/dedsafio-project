package com.watonstudio.creepermetalico;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class LinternaHud {
   private static final int DURACION_MS = 2000;
   private static final int SEGMENTS = 56;

   private LinternaHud() {
   }

   public static void register() {
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("mod", "hud_83626_645"), (context, tickCounter) -> render(context, tickCounter));
   }

   private static void render(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
      Minecraft client = Minecraft.getInstance();
      if (client.player != null && client.gui.screen() == null && client.player.isUsingItem()) {
         ItemStack active = client.player.getUseItem();
         if (active.getItem() instanceof LinternaItem linterna) {
            int var22 = 40 - client.player.getUseItemRemainingTicks();
            float partialTick = tickCounter.getGameTimeDeltaPartialTick(false);
            float elapsedTicks = var22 + partialTick;
            int elapsedMillis = Mth.clamp(Math.round(elapsedTicks * 50.0F), 0, 2000);
            float progress = elapsedMillis / 2000.0F;
            int completed = Math.round(progress * 56.0F);
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight();
            int centerX = width / 2;
            int centerY = height / 2 + 46;
            byte radius = 25;
            context.fill(centerX - 18, centerY - 18, centerX + 18, centerY + 18, -1610612736);

            for (int label = 0; label < 56; label++) {
               double angle = (-Math.PI / 2) + (Math.PI * 2) * label / 56.0;
               int x = centerX + (int)Math.round(Math.cos(angle) * radius);
               int y = centerY + (int)Math.round(Math.sin(angle) * radius);
               int color = label < completed ? -15539 : -12237499;
               context.fill(x - 2, y - 2, x + 2, y + 2, color);
            }

            String label = linterna.isLit() ? "APAGANDO" : "ENCENDIENDO";
            String timer = String.format(Locale.ROOT, "%d s %03d ms", elapsedMillis / 1000, elapsedMillis % 1000);
            int labelWidth = client.font.width(label);
            int timerWidth = client.font.width(timer);
            context.text(client.font, label, centerX - labelWidth / 2, centerY - 5, -1, true);
            context.text(client.font, timer, centerX - timerWidth / 2, centerY + radius + 9, -11145, true);
         }
      }
   }
}
