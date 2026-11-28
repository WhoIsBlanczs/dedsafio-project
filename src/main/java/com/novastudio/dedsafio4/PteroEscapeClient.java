package com.novastudio.dedsafio4;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.sounds.SoundEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public final class PteroEscapeClient {
   private static final int NEEDED = 6;
   private static final int MOVE_TICKS = 30;
   private static final int[] KEYS = new int[]{87, 65, 83, 68};
   private static final Identifier[] TEXTURES = new Identifier[]{tex("w"), tex("a"), tex("s"), tex("d")};
   private static final int SIZE_W = 220;
   private static final int[] SIZE_H = new int[]{100, 104, 103, 101};
   private static final RandomSource RANDOM = RandomSource.create();
   private static final boolean[] down = new boolean[4];
   private static boolean active;
   private static boolean sent;
   private static int hits;
   private static int key;
   private static int age;
   private static int shownAt;
   private static int startedAt;
   private static float fx;
   private static float fy;

   private PteroEscapeClient() {
   }

   private static Identifier tex(String var0) {
      return Identifier.fromNamespaceAndPath("nexo", "textures/gui/teclas/" + var0 + ".png");
   }

   static void register() {
      ClientTickEvents.END_CLIENT_TICK.register(PteroEscapeClient::tick);
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("nexo", "hud_1569_1766"), (var0, var1) -> render(var0));
   }

   private static void tick(Minecraft var0) {
      boolean var1 = var0.player != null && var0.player.getVehicle() instanceof PterodactiloEntity;
      if (!var1) {
         active = false;
      } else {
         if (!active) {
            active = true;
            sent = false;
            hits = 0;
            startedAt = age;

            for (int var4 = 0; var4 < 4; var4++) {
               down[var4] = InputConstants.isKeyDown(KEYS[var4]);
            }

            next();
         }

         age++;
         if (!sent) {
            if (age - shownAt >= 30) {
               move();
            }

            for (int var6 = 0; var6 < 4; var6++) {
               boolean var5 = var0.gui.screen() == null && InputConstants.isKeyDown(KEYS[var6]);
               if (var5 && !down[var6]) {
                  press(var0, var6);
               }

               down[var6] = var5;
            }
         }
      }
   }

   private static void press(Minecraft var0, int var1) {
      if (var1 != key) {
         hits = 0;
         var0.player.playSound(SoundEvents.VILLAGER_NO, 0.8F, 1.2F);
         next();
      } else {
         hits++;
         var0.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6F, 0.8F + hits * 0.15F);
         if (hits >= 6) {
            sent = true;
            ClientPlayNetworking.send(new PteroEscape.Escaped());
         } else {
            next();
         }
      }
   }

   private static void next() {
      int var0 = key;

      do {
         key = RANDOM.nextInt(4);
      } while (key == var0 && RANDOM.nextBoolean());

      move();
   }

   private static void move() {
      fx = 0.15F + RANDOM.nextFloat() * 0.7F;
      fy = 0.15F + RANDOM.nextFloat() * 0.6F;
      shownAt = age;
   }

   private static void render(GuiGraphicsExtractor var0) {
      if (active && !sent) {
         int var1 = var0.guiWidth();
         int var2 = var0.guiHeight();
         float var3 = Math.min(1.0F, (age - shownAt + 1) / 4.0F);
         float var4 = 0.4F * (0.6F + 0.4F * var3);
         int var5 = (int)(var1 * fx);
         int var6 = (int)(var2 * fy);
         var0.pose().pushMatrix();
         var0.pose().translate((float)(var5), (float)(var6));
         var0.pose().scale((float)(var4), (float)(var4));
         var0.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURES[key], (int)(-110), (int)(-SIZE_H[key] / 2), (float)(0.0F), (float)(0.0F), 220, SIZE_H[key], 220, SIZE_H[key]);
         var0.pose().popMatrix();
         TimerBar.draw(var0, var1 / 2, var2 - 72, 1.0F - (age - startedAt) / 200.0F);
      }
   }
}
