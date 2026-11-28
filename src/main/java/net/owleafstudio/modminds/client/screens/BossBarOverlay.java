package net.owleafstudio.modminds.client.screens;

import com.mojang.renderpearl.backend.opengl.GlStateManager.DestFactor;
import com.mojang.renderpearl.backend.opengl.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class BossBarOverlay {
   private static final int MARGIN_X = 10;
   private static final int MARGIN_Y = 8;
   private static final int ROW_H = 56;
   private static final int RANGE = 150;
   private static final float SIZE = 1.6F;
   private static final List<BossBarOverlay.Style> STYLES = List.of(
      new BossBarOverlay.Style(bar("reviil"), "REVIIL", ReviilEntity.class, 200, 42, 1280, 267, 38, 18, 160, 10, 44, -20406),
      new BossBarOverlay.Style(bar("hydra"), "HYDRA", HydraEntity.class, 240, 20, 960, 80, 26, 5, 209, 9, 22, -49957),
      new BossBarOverlay.Style(bar("limbo_titan"), "LIMBO TITAN", LimboTitanEntity.class, 235, 22, 940, 88, 24, 7, 208, 8, 23, -30165),
      new BossBarOverlay.Style(bar("tiki"), "JEFE TIKI", TikiGuardianEntity.class, 240, 22, 960, 88, 26, 7, 207, 9, 24, -8658870)
   );
   private static final HashMap<UUID, Float> DISPLAYED_HEALTH = new HashMap<>();

   private static int px(int drawnUnits) {
      return Math.round(drawnUnits * 1.6F);
   }

   private BossBarOverlay() {
   }

   private static Identifier bar(String name) {
      return Identifier.parse("modminds:textures/screens/bossbar_" + name + ".png");
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void onRenderOverlay(RenderGuiEvent.Pre event) {
      Minecraft mc = Minecraft.getInstance();
      Player player = mc.player;
      if (player != null && player.level() != null && !player.isSpectator()) {
         Level level = player.level();
         AABB range = new AABB(
            player.getX() - 150.0,
            player.getY() - 150.0,
            player.getZ() - 150.0,
            player.getX() + 150.0,
            player.getY() + 150.0,
            player.getZ() + 150.0
         );
         List<BossBarOverlay.Shown> bosses = new ArrayList<>();

         for (BossBarOverlay.Style style : STYLES) {
            List<? extends LivingEntity> nearby = level.getEntitiesOfClass(style.boss(), range);
            if (!nearby.isEmpty() && hasArtwork(mc, style)) {
               for (LivingEntity boss : nearby) {
                  bosses.add(new BossBarOverlay.Shown(boss, style, boss.distanceToSqr(player)));
               }
            }
         }

         bosses.sort(Comparator.comparingDouble(BossBarOverlay.Shown::distanceSq));
         int screenH = mc.getWindow().getGuiScaledHeight();

         for (int row = 0; row < bosses.size(); row++) {
            BossBarOverlay.Shown shown = bosses.get(row);
            int y = 8 + row * px(56);
            if (y + px(shown.style().barH()) > screenH - 4) {
               break;
            }

            draw(event.getGuiGraphics(), mc, shown.boss(), shown.style(), 10, y);
         }
      }
   }

   private static boolean hasArtwork(Minecraft mc, BossBarOverlay.Style style) {
      return mc.getResourceManager().getResource(style.artwork()).isPresent();
   }

   private static void draw(GuiGraphicsExtractor gui, Minecraft mc, LivingEntity boss, BossBarOverlay.Style style, int x, int y) {
      int barW = px(style.barW());
      int barH = px(style.barH());
      int segX = px(style.segX());
      int segY = px(style.segY());
      int segW = px(style.segW());
      int segH = px(style.segH());
      gui.blit(style.artwork(), x, y, barW, barH, 0.0F, 0.0F, style.texW(), style.texH(), style.texW(), style.texH());
      float ratio = smoothedHealth(boss);
      int filled = (int)Math.ceil(segW * Math.max(ratio > 0.0F ? 1.0 / segW : 0.0, (double)ratio));
      if (filled < segW) {
         gui.fill(x + segX + filled, y + segY, x + segX + segW, y + segY + segH, -1728053248);
         gui.fill(x + segX + filled - 2, y + segY, x + segX + filled, y + segY + segH, style.accent());
      }

      String raw = boss.getName().getString();
      String title = raw.isEmpty() ? style.fallbackTitle() : raw;
      int textX = x + (barW - mc.font.width(title)) / 2;
      int textY = y + px(style.titleY());
      gui.drawString(mc.font, Component.literal(title), textX - 1, textY, Integer.MIN_VALUE, false);
      gui.drawString(mc.font, Component.literal(title), textX + 1, textY, Integer.MIN_VALUE, false);
      gui.drawString(mc.font, Component.literal(title), textX, textY - 1, Integer.MIN_VALUE, false);
      gui.drawString(mc.font, Component.literal(title), textX, textY + 1, Integer.MIN_VALUE, false);
      gui.drawString(mc.font, Component.literal(title), textX, textY, style.accent());
   }

   private static float smoothedHealth(LivingEntity boss) {
      float current = Math.max(0.0F, boss.getHealth() / boss.getMaxHealth());
      float previous = DISPLAYED_HEALTH.getOrDefault(boss.getUUID(), current);
      float shown = previous + (current - previous) * 0.05F;
      DISPLAYED_HEALTH.put(boss.getUUID(), Math.abs(shown - current) < 0.001F ? current : shown);
      return shown;
   }

   private record Shown(LivingEntity boss, BossBarOverlay.Style style, double distanceSq) {
   }

   private record Style(
      Identifier artwork,
      String fallbackTitle,
      Class<? extends LivingEntity> boss,
      int barW,
      int barH,
      int texW,
      int texH,
      int segX,
      int segY,
      int segW,
      int segH,
      int titleY,
      int accent
   ) {
   }
}
