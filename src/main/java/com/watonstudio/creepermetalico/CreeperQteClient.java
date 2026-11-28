package com.watonstudio.creepermetalico;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Locale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundEvents;

@Environment(EnvType.CLIENT)
public class CreeperQteClient implements ClientModInitializer {
   private static boolean active;
   private static int currentKey = -1;
   private static int currentStep;
   private static int totalSteps = 6;
   private static int durationTicks = 60;
   private static long startTime;
   private static long greenFeedbackUntil;
   private static long errorFeedbackUntil;
   private static long pressedUntil;
   private static long lastWrongSoundAt;
   private static boolean previousW;
   private static boolean previousA;
   private static boolean previousS;
   private static boolean previousD;

   public void onInitializeClient() {
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> FloralFirstPersonWorldRoots.clearVisuals());
      FloralFirstPersonWorldRoots.register();
      net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback.EVENT
         .register((entityType, renderer, helper, context) -> {
            if (renderer instanceof net.minecraft.client.renderer.entity.player.AvatarRenderer<?> playerRenderer) {
               helper.register(new FloralRootsFeatureRenderer((net.minecraft.client.renderer.entity.RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, net.minecraft.client.model.player.PlayerModel>)(Object)playerRenderer));
            }
         });
      ClientPlayNetworking.registerGlobalReceiver(
         CreeperQteVisualPayload.ID,
         (payload, context) -> context.client()
            .execute(() -> FloralFirstPersonWorldRoots.setVisualState(payload.playerId(), payload.stage(), payload.active()))
      );
      ClientPlayNetworking.registerGlobalReceiver(CreeperQteStatePayload.ID, (payload, context) -> context.client().execute(() -> {
         long now = System.currentTimeMillis();
         if (payload.step() >= 0 && payload.key() >= 0) {
            boolean timedOut = active && now - startTime >= durationTicks * 50L;
            if (active && payload.step() > currentStep) {
               greenFeedbackUntil = now + 400L;
               errorFeedbackUntil = 0L;
               playSuccessSound(context.client());
            } else if (active && payload.step() == 0 && (currentStep > 0 || timedOut)) {
               errorFeedbackUntil = now + 400L;
               greenFeedbackUntil = 0L;
               playWrongSound(context.client());
            }

            active = true;
            currentKey = payload.key();
            currentStep = payload.step();
            totalSteps = Math.max(1, payload.total());
            durationTicks = Math.max(1, payload.durationTicks());
            startTime = now;
         } else {
            if (active && currentStep >= totalSteps - 1) {
               greenFeedbackUntil = now + 350L;
               playSuccessSound(context.client());
            }

            active = false;
            currentKey = -1;
            currentStep = 0;
            greenFeedbackUntil = 0L;
            errorFeedbackUntil = 0L;
            pressedUntil = 0L;
         }
      }));
      ClientTickEvents.START_CLIENT_TICK.register((StartTick)client -> {
         if (active && client.player != null) {
            blockMovement(client);
         }
      });
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         boolean w = com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_W);
         boolean a = com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_A);
         boolean s = com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_S);
         boolean d = com.mojang.blaze3d.platform.InputConstants.isKeyDown(com.mojang.blaze3d.platform.InputConstants.KEY_D);
         if (active && client.player != null) {
            blockMovement(client);
            if (client.gui.screen() == null) {
               int pressed = -1;
               if (w && !previousW) {
                  pressed = 0;
               } else if (a && !previousA) {
                  pressed = 1;
               } else if (s && !previousS) {
                  pressed = 2;
               } else if (d && !previousD) {
                  pressed = 3;
               }

               if (pressed != -1) {
                  pressedUntil = System.currentTimeMillis() + 150L;
                  if (pressed != currentKey) {
                     errorFeedbackUntil = System.currentTimeMillis() + 400L;
                     greenFeedbackUntil = 0L;
                     playWrongSound(client);
                  }

                  ClientPlayNetworking.send(new CreeperQteInputPayload(pressed, currentStep));
               }
            }
         }

         previousW = w;
         previousA = a;
         previousS = s;
         previousD = d;
      });
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("mod", "hud_71829_5667"), (context, tickCounter) -> renderQte(Minecraft.getInstance(), context));
   }

   private static void blockMovement(Minecraft client) {
      client.options.keyUp.setDown(false);
      client.options.keyDown.setDown(false);
      client.options.keyLeft.setDown(false);
      client.options.keyRight.setDown(false);
   }

   private static void playSuccessSound(Minecraft client) {
      if (client.player != null) {
         client.player.playSound(SoundEvents.AZALEA_LEAVES_PLACE, 0.85F, 1.35F);
      }
   }

   private static void playWrongSound(Minecraft client) {
      long now = System.currentTimeMillis();
      if (client.player != null && now - lastWrongSoundAt > 300L) {
         client.player.playSound(SoundEvents.AZALEA_LEAVES_BREAK, 0.8F, 0.75F);
         lastWrongSoundAt = now;
      }
   }

   public static boolean shouldRenderRoots(AbstractClientPlayer player) {
      Minecraft client = Minecraft.getInstance();
      return player != null && client.gui.screen() == null && (active && client.player == player || FloralFirstPersonWorldRoots.hasVisual(player.getId()));
   }

   public static int getRootStage() {
      return Math.max(0, Math.min(currentStep, totalSteps));
   }

   private static void draw3DText(GuiGraphicsExtractor context, Minecraft client, String text, int x, int y, int faceColor, int sideColor) {
      org.joml.Matrix3x2fStack matrices = context.pose();
      matrices.pushMatrix();
      matrices.translate((float)(x), (float)(y));
      matrices.scale((float)(0.82F), (float)(0.82F));
      context.text(client.font, text, -client.font.width(text) / 2, 0, faceColor, false);
      matrices.popMatrix();
   }

   private static void renderFirstPersonRoots(GuiGraphicsExtractor context, int width, int height) {
      int left = Math.max(9, width / 90);
      int nearLeft = Math.max(32, width / 20);
      int farLeft = Math.max(58, width / 12);
      int right = width - left;
      int nearRight = width - nearLeft;
      int farRight = width - farLeft;
      int y1 = height / 10;
      int y2 = height / 5;
      int y3 = height * 3 / 10;
      int y4 = height * 2 / 5;
      int y5 = height / 2;
      int y6 = height * 3 / 5;
      int y7 = height * 3 / 4;
      int y8 = height * 9 / 10;
      int stage = getRootStage();
      if (stage <= 0) {
         drawRootLine(context, left, -12, farLeft, y1);
         drawRootLine(context, farLeft, y1, nearLeft, y2);
         drawRootLine(context, right, -12, farRight, y1);
         drawRootLine(context, farRight, y1, nearRight, y2);
         drawRootLine(context, left, y1, width / 7, y2);
         drawRootLine(context, right, y1, width * 6 / 7, y2);
         drawPinkFlower(context, farLeft, y1);
         drawPinkFlower(context, nearLeft, y2);
         drawPinkFlower(context, farRight, y1);
         drawPinkFlower(context, nearRight, y2);
      }

      if (stage <= 1) {
         drawRootLine(context, left, y2, farLeft, y3);
         drawRootLine(context, farLeft, y3, nearLeft, y4);
         drawRootLine(context, right, y2, farRight, y3);
         drawRootLine(context, farRight, y3, nearRight, y4);
         drawRootLine(context, nearLeft, y3, width / 6, y3 + height / 14);
         drawRootLine(context, nearRight, y3, width * 5 / 6, y3 + height / 14);
         drawPinkFlower(context, farLeft, y3);
         drawPinkFlower(context, nearLeft, y4);
         drawPinkFlower(context, farRight, y3);
         drawPinkFlower(context, nearRight, y4);
      }

      if (stage <= 2) {
         drawRootLine(context, left, y3, nearLeft, y4);
         drawRootLine(context, nearLeft, y4, left + 7, y5);
         drawRootLine(context, right, y3, nearRight, y4);
         drawRootLine(context, nearRight, y4, right - 7, y5);
         drawRootLine(context, nearLeft, y4, width / 8, y5);
         drawRootLine(context, nearRight, y4, width * 7 / 8, y5);
         drawRootLine(context, left + 5, y4, farLeft, y5);
         drawRootLine(context, right - 5, y4, farRight, y5);
         drawPinkFlower(context, left + 15, y4);
         drawPinkFlower(context, farLeft, y5);
         drawPinkFlower(context, right - 15, y4);
         drawPinkFlower(context, farRight, y5);
      }

      if (stage <= 3) {
         drawRootLine(context, left + 5, y5, farLeft, y6);
         drawRootLine(context, farLeft, y6, left + 10, y7);
         drawRootLine(context, right - 5, y5, farRight, y6);
         drawRootLine(context, farRight, y6, right - 10, y7);
         drawRootLine(context, left, y6, width / 7, y7);
         drawRootLine(context, right, y6, width * 6 / 7, y7);
         drawRootLine(context, farLeft, y6, width / 5, y6 + height / 12);
         drawRootLine(context, farRight, y6, width * 4 / 5, y6 + height / 12);
         drawPinkFlower(context, farLeft, y6);
         drawPinkFlower(context, left + 11, y7);
         drawPinkFlower(context, farRight, y6);
         drawPinkFlower(context, right - 11, y7);
         drawPinkFlower(context, width / 5, y6 + height / 12);
         drawPinkFlower(context, width * 4 / 5, y6 + height / 12);
      }

      if (stage <= 4) {
         drawRootLine(context, left, y7, width / 6, y8);
         drawRootLine(context, width / 6, y8, width / 4, height - 5);
         drawRootLine(context, right, y7, width * 5 / 6, y8);
         drawRootLine(context, width * 5 / 6, y8, width * 3 / 4, height - 5);
         drawRootLine(context, width / 10, height - 5, width / 4, y8);
         drawRootLine(context, width * 9 / 10, height - 5, width * 3 / 4, y8);
         drawPinkFlower(context, width / 6, y8);
         drawPinkFlower(context, width / 4, height - 8);
         drawPinkFlower(context, width * 5 / 6, y8);
         drawPinkFlower(context, width * 3 / 4, height - 8);
      }

      if (stage <= 5) {
         drawRootLine(context, width / 12, height - 6, width / 5, y7);
         drawRootLine(context, width / 5, y7, width / 3, y8);
         drawRootLine(context, width * 11 / 12, height - 6, width * 4 / 5, y7);
         drawRootLine(context, width * 4 / 5, y7, width * 2 / 3, y8);
         drawPinkFlower(context, width / 5, y7);
         drawPinkFlower(context, width / 3, y8);
         drawPinkFlower(context, width * 4 / 5, y7);
         drawPinkFlower(context, width * 2 / 3, y8);
      }
   }

   private static void drawRootLine(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2) {
      double dx = x2 - x1;
      double dy = y2 - y1;
      float length = (float)Math.sqrt(dx * dx + dy * dy);
      float angle = (float)Math.toDegrees(Math.atan2(dy, dx));
      if (!(length < 2.0F)) {
         org.joml.Matrix3x2fStack matrices = context.pose();
         matrices.pushMatrix();
         matrices.translate((float)((x1 + x2) / 2.0), (float)((y1 + y2) / 2.0));
         matrices.rotate((float)Math.toRadians(angle));
         int half = Math.max(1, (int)(length / 2.0F));
         context.fill(-half - 3, -15, half + 8, 18, -369098752);
         context.fill(-half + 2, -11, half + 8, 15, -14609137);
         context.fill(-half, -11, half + 3, 12, -13097702);
         context.fill(-half + 1, -9, half + 1, 10, -8957898);
         context.fill(-half + 3, -7, half - 2, 8, -6788793);
         context.fill(-half + 4, -8, half - 4, -5, -3038353);
         context.fill(-half + 6, -5, half - 6, -4, -1852278);
         context.fill(-half + 3, -2, half - 3, 3, -13145032);
         context.fill(-half + 5, -1, half - 5, 1, -8931757);
         context.fill(-half + 3, 6, half - 2, 9, -12506853);

         for (int p = -half + 13; p < half - 10; p += 22) {
            context.fill(p, -9, p + 5, -12, -11192798);
            context.fill(p + 2, -10, p + 5, -11, -2578312);
            context.fill(p + 6, 6, p + 11, 10, -13295337);
            context.fill(p + 8, 5, p + 10, 7, -9463740);
         }

         for (int p = -half + 20; p < half - 12; p += 35) {
            context.fill(p, -10, p + 3, -16, -9943506);
            context.fill(p + 2, -14, p + 4, -16, -3958169);
            context.fill(p + 7, 8, p + 10, 13, -11521247);
         }

         matrices.popMatrix();
      }
   }

   private static void drawPinkFlower(GuiGraphicsExtractor context, int x, int y) {
      context.fill(x - 6 + 4, y - 6 + 5, x + 7 + 4, y + 7 + 5, -738197504);
      context.fill(x - 4, y - 7, x + 4, y + 3, -6477723);
      context.fill(x - 7, y - 4, x + 3, y + 4, -6477723);
      context.fill(x + 2, y - 4, x + 7, y + 4, -6477723);
      context.fill(x - 4, y + 2, x + 4, y + 8, -6477723);
      context.fill(x - 3, y - 8, x + 3, y - 2, -22576);
      context.fill(x - 8, y - 4, x - 2, y + 3, -35913);
      context.fill(x + 2, y - 4, x + 8, y + 3, -35913);
      context.fill(x - 4, y + 2, x + 4, y + 8, -28220);
      context.fill(x - 2, y - 7, x + 2, y - 5, -7440);
      context.fill(x - 7, y - 3, x - 5, y, -19240);
      context.fill(x + 5, y - 3, x + 7, y, -19240);
      context.fill(x - 3, y - 3, x + 4, y + 4, -1460145);
      context.fill(x - 1, y - 2, x + 2, y + 2, -4448);
      context.fill(x - 1, y - 1, x + 1, y + 1, -1);
      context.fill(x - 9, y + 3, x - 3, y + 6, -13735115);
      context.fill(x + 3, y + 4, x + 9, y + 7, -13735115);
      context.fill(x - 7, y + 3, x - 4, y + 4, -8341414);
      context.fill(x + 4, y + 4, x + 7, y + 5, -8341414);
   }

   public static int getRootStage(AbstractClientPlayer player) {
      return player != null && FloralFirstPersonWorldRoots.hasVisual(player.getId())
         ? FloralFirstPersonWorldRoots.getVisualStage(player.getId())
         : getRootStage();
   }

   private static void renderQte(Minecraft client, GuiGraphicsExtractor context) {
      if (active && client.player != null && client.level != null && client.gui.screen() == null) {
         int width = client.getWindow().getGuiScaledWidth();
         int height = client.getWindow().getGuiScaledHeight();
         int centerX = width / 2;
         int centerY = height / 2;
         long now = System.currentTimeMillis();
         boolean successFlash = now < greenFeedbackUntil;
         boolean errorFlash = now < errorFeedbackUntil;
         boolean keyPressed = now < pressedUntil;
         int tileSize = Math.max(48, Math.min(60, height / 10));
         int tileX = centerX - tileSize / 2;
         int tileY = centerY + Math.max(38, height / 17);
         int borderColor = successFlash ? -9112166 : (errorFlash ? -3839411 : -14189493);
         context.fill(tileX - 12, tileY - 11, tileX + tileSize + 12, tileY + tileSize + 13, -1728053248);
         context.fill(tileX - 6, tileY - 6, tileX + tileSize + 6, tileY + tileSize + 6, -16312049);
         context.fill(tileX - 3, tileY - 3, tileX + tileSize + 3, tileY + tileSize + 3, borderColor);
         context.fill(tileX, tileY, tileX + tileSize, tileY + tileSize, -15718629);
         context.fill(tileX + 4, tileY + 4, tileX + tileSize - 4, tileY + tileSize - 4, -14664405);
         context.fill(tileX + 5, tileY + 5, tileX + tileSize - 5, tileY + 8, -10253715);
         draw3DText(context, client, "PULSA LA TECLA", centerX, tileY - 21, -1, -16115440);
         int capWidth = (int)(tileSize * 0.7F);
         int capHeight = (int)(tileSize * 0.7F);
         int capX = centerX - capWidth / 2;
         int capBaseY = tileY + (tileSize - capHeight) / 2;
         int pressOffset = keyPressed ? 3 : 0;
         int capY = capBaseY + pressOffset;
         context.fill(capX - 4, capBaseY + capHeight - 2, capX + capWidth + 4, capBaseY + capHeight + 8, -16644348);
         context.fill(capX - 2, capY + 2, capX + capWidth + 2, capY + capHeight + 4, keyPressed ? -9142922 : -7760243);
         context.fill(capX + capWidth - 3, capY + 4, capX + capWidth + 7, capY + capHeight + 8, keyPressed ? -13286856 : -11376042);
         context.fill(capX - 2, capY + capHeight - 3, capX + capWidth + 7, capY + capHeight + 8, keyPressed ? -12957380 : -10257563);
         context.fill(capX, capY, capX + capWidth, capY + capHeight, keyPressed ? -4800328 : -1);
         context.fill(capX + 3, capY + 3, capX + capWidth - 3, capY + capHeight - 5, keyPressed ? -2038048 : -722956);
         context.fill(capX + 5, capY + 5, capX + capWidth - 5, capY + 8, -1);
         context.fill(capX + 5, capY + capHeight - 8, capX + capWidth - 5, capY + capHeight - 5, -6575971);

         String letter = switch (currentKey) {
            case 0 -> "W";
            case 1 -> "A";
            case 2 -> "S";
            case 3 -> "D";
            default -> "?";
         };
         float textScale = 2.1F;
         float textY = capY + (capHeight - 9.0F * textScale) / 2.0F - 1.0F;
         org.joml.Matrix3x2fStack matrices = context.pose();
         matrices.pushMatrix();
         matrices.translate((float)(centerX), (float)(textY));
         matrices.scale((float)(textScale), (float)(textScale));
         context.text(client.font, letter, -client.font.width(letter) / 2, 0, successFlash ? -15242440 : -15261158, false);
         matrices.popMatrix();
         int barWidth = Math.max(125, Math.min(205, width / 5));
         int barHeight = 10;
         int barX = centerX - barWidth / 2;
         int barY = tileY + tileSize + 34;
         long maxTime = Math.max(1L, durationTicks * 50L);
         long remainingMs = Math.max(0L, maxTime - Math.max(0L, now - startTime));
         String timer = String.format(Locale.ROOT, "%.1f s", remainingMs / 1000.0);
         int shownStep = Math.min(currentStep + 1, totalSteps);
         String label = "TECLA " + shownStep + " / " + totalSteps + "   -   " + timer;
         draw3DText(context, client, label, centerX, barY - 19, remainingMs < 1000L ? -19554 : -1, -16181489);
         context.fill(barX - 5, barY - 5, barX + barWidth + 5, barY + barHeight + 5, -16578299);
         context.fill(barX - 3, barY - 3, barX + barWidth + 3, barY + barHeight + 3, -13475772);
         context.fill(barX, barY, barX + barWidth, barY + barHeight, -15718117);
         float progress = Math.max(0.0F, Math.min(1.0F, (float)currentStep / totalSteps));
         int filledWidth = Math.round(barWidth * progress);
         if (filledWidth > 0) {
            int darkGreen = successFlash ? -14316981 : -15243206;
            int brightGreen = successFlash ? -8192086 : -12527499;
            context.fill(barX, barY, barX + filledWidth, barY + barHeight, darkGreen);
            context.fill(barX + 1, barY + 1, Math.max(barX + 1, barX + filledWidth - 1), barY + 4, brightGreen);
            context.fill(barX + 1, barY + 5, Math.max(barX + 1, barX + filledWidth - 1), barY + barHeight - 1, darkGreen);
         }

         for (int i = 1; i < totalSteps; i++) {
            int dividerX = barX + barWidth * i / totalSteps;
            context.fill(dividerX, barY, dividerX + 1, barY + barHeight, -16247539);
         }

         if (successFlash) {
            draw3DText(context, client, "CORRECTO!", centerX, barY + barHeight + 11, -8192086, -16107234);
         } else if (errorFlash) {
            draw3DText(context, client, "OTRA VEZ!", centerX, barY + barHeight + 11, -19554, -12444396);
         }
      }
   }
}
