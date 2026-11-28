package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.CerebroEntity;
import com.ghostsafio.mitomod.ModMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class MobsClient {
   public static void register() {
      EntityRendererRegistry.register(ModMobs.NAUTILUS, context -> new BbMobRenderer(context, "nautilus", 1.0F, 0.205F, -0.656F, 0.0F));
      EntityRendererRegistry.register(ModMobs.CEREBRO, context -> new BbMobRenderer(context, "cerebro", 0.8F, 0.143F, 0.0F, 0.0F));
      EntityRendererRegistry.register(ModMobs.SKIFF_PHORA, context -> new BbMobRenderer(context, "skiff_phora", 1.0F, 0.0F, 0.0F, 0.0F));
      EntityRendererRegistry.register(ModMobs.POD_PHORA, context -> new BbMobRenderer(context, "pod_phora", 1.15F, 0.0F, 0.0F, 0.0F));
      EntityRendererRegistry.register(ModMobs.POD_LASER, PodLaserRenderer::new);
      HudRenderCallback.EVENT.register(MobsClient::renderParasiteWarning);
      ClientCommandRegistrationCallback.EVENT
         .register(
            (ClientCommandRegistrationCallback)(dispatcher, registryAccess) -> dispatcher.register(
               (LiteralArgumentBuilder)ClientCommandManager.literal("mob_yaw")
                  .then(ClientCommandManager.argument("grados", FloatArgumentType.floatArg(-360.0F, 360.0F)).executes(ctx -> {
                     BbMobRenderer.yawOffset = FloatArgumentType.getFloat(ctx, "grados");
                     ((FabricClientCommandSource)ctx.getSource()).sendFeedback(Component.literal("mob_yaw = " + BbMobRenderer.yawOffset));
                     return 1;
                  }))
            )
         );
   }

   private static boolean hasParasite(Minecraft mc) {
      if (mc.level != null && mc.player != null) {
         for (CerebroEntity c : mc.level.getEntitiesOfClass(CerebroEntity.class, mc.player.getBoundingBox().inflate(4.0))) {
            if (c.getHostId() == mc.player.getId()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static void renderParasiteWarning(GuiGraphics g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && !mc.options.hideGui && hasParasite(mc)) {
         int w = g.guiWidth();
         int h = g.guiHeight();
         float pulse = 0.55F + 0.45F * (float)Math.sin(System.nanoTime() / 1.0E9 * 6.0);

         for (int i = 0; i < 12; i++) {
            float f = 1.0F - i / 12.0F;
            int a = (int)(pulse * 80.0F * f * f);
            if (a >= 3) {
               int col = a << 24 | 14268190;
               int b = i * 5;
               g.fill(0, b, w, b + 5, col);
               g.fill(0, h - b - 5, w, h - b, col);
               g.fill(b, 0, b + 5, h, col);
               g.fill(w - b - 5, 0, w - b, h, col);
            }
         }

         Font font = mc.font;
         String title = "¡PARÁSITO EN LA CABEZA!";
         String sub = "No puedes golpearlo: pide ayuda a alguien";
         text(g, font, title, w / 2.0F, h * 0.18F, -1455798, 1.6F);
         text(g, font, sub, w / 2.0F, h * 0.18F + 18.0F, -3408, 0.9F);
      }
   }

   private static void text(GuiGraphics g, Font font, String s, float cx, float y, int color, float scale) {
      PoseStack ps = g.pose();
      ps.pushPose();
      ps.translate(cx - font.width(s) * scale / 2.0F, y, 0.0F);
      ps.scale(scale, scale, 1.0F);
      g.drawString(font, s, 0, 0, color, false);
      ps.popPose();
   }
}
