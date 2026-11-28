package com.novastudio.dedsafio4;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
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
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public final class MemeTrapClient {
   private static final int NEEDED = 15;
   private static final int KEY_SPACE = 32;
   private static final int FLOOD = 110;
   private static final Identifier SPACE = tex("espacio");
   private static final Identifier[] FLOOD_TEX = new Identifier[]{tex("kick_logo"), tex("kick_canal_1"), tex("kick_canal_2"), tex("duko")};
   private static final int[][] FLOOD_SIZE = new int[][]{{128, 128}, {376, 116}, {296, 105}, {256, 319}};
   private static final float[] FLOOD_SCALE = new float[]{0.4F, 0.4F, 0.45F, 0.25F};
   private static final RandomSource RANDOM = RandomSource.create();
   private static final List<MemeTrapClient.Sticker> stickers = new ArrayList<>();
   private static boolean active;
   private static boolean flood;
   private static boolean sent;
   private static boolean wasDown;
   private static int presses;
   private static int age;
   private static int pressedAt = -10;
   private static boolean registered;

   private MemeTrapClient() {
   }

   private static Identifier tex(String var0) {
      return Identifier.fromNamespaceAndPath("nexo", "textures/gui/kick/" + var0 + ".png");
   }

   static void register() {
      if (!registered) {
         registered = true;
         ClientPlayNetworking.registerGlobalReceiver(MemeTrap.Trapped.ID, (var0, var1) -> {
            active = var0.mode() != 0;
            flood = var0.mode() == 1;
            sent = false;
            presses = 0;
            age = 0;
            stickers.clear();
            wasDown = true;
         });
         ClientTickEvents.END_CLIENT_TICK.register(MemeTrapClient::tick);
         net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("nexo", "hud_93821_2382"), (var0, var1) -> render(var0));
      }
   }

   private static void tick(Minecraft var0) {
      if (active) {
         if (var0.player != null && ++age <= 285) {
            int var1 = (int)(110.0 * Math.pow(Math.min(1.0, age / 200.0), 1.3));

            while (flood && stickers.size() < var1) {
               stickers.add(
                  new MemeTrapClient.Sticker(
                     RANDOM.nextInt(FLOOD_TEX.length),
                     -0.05F + RANDOM.nextFloat() * 1.1F,
                     -0.05F + RANDOM.nextFloat() * 1.1F,
                     (RANDOM.nextFloat() - 0.5F) * 40.0F,
                     0.8F + RANDOM.nextFloat() * 0.5F
                  )
               );
            }

            if (!sent) {
               boolean var2 = var0.gui.screen() == null && InputConstants.isKeyDown(32);
               if (var2 && !wasDown) {
                  presses++;
                  pressedAt = age;
                  var0.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5F, 0.7F + presses * 0.06F);
                  if (presses >= 15) {
                     sent = true;
                     ClientPlayNetworking.send(new MemeTrap.Freed());
                  }
               }

               wasDown = var2;
            }
         } else {
            active = false;
         }
      }
   }

   private static void render(GuiGraphicsExtractor var0) {
      if (active && !sent) {
         int var1 = var0.guiWidth();
         int var2 = var0.guiHeight();
         for (MemeTrapClient.Sticker var4 : stickers) {
            int[] var5 = FLOOD_SIZE[var4.kind()];
            float var6 = FLOOD_SCALE[var4.kind()] * var4.scale();
            var0.pose().pushMatrix();
            var0.pose().translate((float)(var1 * var4.fx()), (float)(var2 * var4.fy()));
            var0.pose().rotate((float)Math.toRadians(var4.rot()));
            var0.pose().scale((float)(var6), (float)(var6));
            var0.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, FLOOD_TEX[var4.kind()], (int)(-var5[0] / 2), (int)(-var5[1] / 2), (float)(0.0F), (float)(0.0F), var5[0], var5[1], var5[0], var5[1]);
            var0.pose().popMatrix();
         }

         float var7 = 0.42F * (age - pressedAt < 3 ? 0.92F : 1.0F);
         var0.pose().pushMatrix();
         var0.pose().translate((float)(var1 / 2.0F), (float)(var2 * 0.62F));
         var0.pose().scale((float)(var7), (float)(var7));
         var0.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, SPACE, (int)(-256), (int)(-56), (float)(0.0F), (float)(0.0F), 512, 113, 512, 113);
         var0.pose().popMatrix();
         var0.pose().pushMatrix();
         var0.pose().translate((float)(0.0F), (float)(0.0F));
         TimerBar.draw(var0, var1 / 2, (int)(var2 * 0.62F) + 30, 1.0F - age / 200.0F);
         var0.pose().popMatrix();
      }
   }

   @Environment(EnvType.CLIENT)
   private record Sticker(int kind, float fx, float fy, float rot, float scale) {
   }
}
