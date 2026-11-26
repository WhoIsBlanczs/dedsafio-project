package ar.hombrosafio.ruleta.client;

import ar.hombrosafio.ruleta.RouletteColor;
import ar.hombrosafio.ruleta.RoulettePayload;
import ar.hombrosafio.ruleta.RuletaMod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class RouletteHud {
   private static RoulettePayload state = RoulettePayload.NONE;
   private static double lastElapsed;
   private static long originNanos;
   private static float needleAngle;
   private static float needleVelocity;
   private static float needleTime = -1.0F;

   private static Identifier asset(String name) {
      return RuletaMod.id("textures/gui/roulette/" + name + ".png");
   }

   public static void register() {
      RouletteSounds.register();
      ClientPlayNetworking.registerGlobalReceiver(RoulettePayload.TYPE, (payload, context) -> context.client().execute(() -> {
         long now = System.nanoTime();
         if (!state.active() || payload.sequence() != state.sequence()) {
            originNanos = now - payload.elapsed() * 50000000L;
            lastElapsed = payload.elapsed();
            needleTime = -1.0F;
            needleVelocity = 0.0F;
            needleAngle = 0.0F;
         } else if (payload.active()) {
            originNanos = Math.min(originNanos, now - payload.elapsed() * 50000000L);
         }

         RouletteSounds.receive(payload, context.client());
         state = payload;
      }));
      ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
         state = RoulettePayload.NONE;
         RouletteSounds.reset(client);
      });
      HudElementRegistry.addLast(RuletaMod.id("roulette"), (g, delta) -> {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.player != null && active()) {
            lastElapsed = Math.max(lastElapsed, elapsed());
            render(g, state.chosen(), (float)lastElapsed, RouletteColor.SPIN, RouletteColor.HOLD);
         }
      });
   }

   public static boolean active() {
      return state.active();
   }

   static double elapsed() {
      return state.active() ? Math.max(lastElapsed, (System.nanoTime() - originNanos) / 5.0E7) : 0.0;
   }

   private static void texture(GuiGraphicsExtractor g, String name, int x, int y, int w, int h, float alpha) {
      if (!(alpha <= 0.0F)) {
         g.blit(RenderPipelines.GUI_TEXTURED, asset(name), x, y, 0.0F, 0.0F, w, h, w, h, (int)(Math.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | 16777215);
      }
   }

   private static float smooth(float t) {
      t = Math.clamp(t, 0.0F, 1.0F);
      return t * t * (3.0F - 2.0F * t);
   }

   private static void updateNeedle(RouletteColor color, float elapsed, int spin) {
      if (needleTime < 0.0F || elapsed < needleTime) {
         needleTime = Math.max(0.0F, elapsed - 2.0F);
         needleVelocity = 0.0F;
         needleAngle = 0.0F;
      }

      float remaining = Math.min(10.0F, elapsed - needleTime);
      needleTime = elapsed - remaining;

      while (remaining > 0.0F) {
         float ticks = Math.min(0.15F, remaining);
         float dt = ticks / 20.0F;
         double before = color.angle(needleTime, spin);
         double after = color.angle(needleTime + ticks, spin);
         if (Math.floor((before + (Math.PI / 8)) / (Math.PI / 4)) != Math.floor((after + (Math.PI / 8)) / (Math.PI / 4))) {
            needleVelocity = Math.max(-5.8F, needleVelocity - 3.0F);
         }

         needleVelocity = needleVelocity + (-120.0F * needleAngle - 13.0F * needleVelocity) * dt;
         needleAngle = needleAngle + needleVelocity * dt;
         if (needleAngle < -0.3F) {
            needleAngle = -0.3F;
            needleVelocity = Math.max(0.0F, needleVelocity * 0.12F);
         }

         needleTime += ticks;
         remaining -= ticks;
      }
   }

   private static void render(GuiGraphicsExtractor g, RouletteColor color, float elapsed, int spin, int hold) {
      float end = spin + hold;
      if (!(elapsed >= end)) {
         float entry = Math.clamp(elapsed / 28.0F, 0.0F, 1.0F);
         float exit = Math.clamp((elapsed - (end - 26.0F)) / 26.0F, 0.0F, 1.0F);
         float back = entry - 1.0F;
         float entered = 1.0F + 2.70158F * back * back * back + 1.70158F * back * back;
         float fade = smooth(elapsed / 8.0F) * (1.0F - smooth((exit - 0.15F) / 0.85F));
         float age = elapsed - spin;
         float result = smooth(age / 14.0F);
         float press = 1.0F;
         if (age >= 0.0F && age < 8.0F) {
            press = 1.0F - 0.18F * smooth(age / 8.0F);
         } else if (age >= 8.0F && age < 28.0F) {
            float t = (age - 8.0F) / 20.0F - 1.0F;
            press = 0.82F + 0.18F * (1.0F + 2.70158F * t * t * t + 1.70158F * t * t);
         }

         float exitScale = 1.0F + 0.1F * (float)Math.sin(exit * Math.PI) - 0.94F * smooth(exit);
         float size = Math.min(g.guiHeight() * 0.64F, g.guiWidth() * 0.55F) * entered * press * exitScale;
         float cx = g.guiWidth() / 2.0F + 2.0F * (float)Math.sin(elapsed * 0.037) * smooth(entry);
         float cy = g.guiHeight() / 2.0F + 65.0F * (1.0F - smooth(entry)) + 55.0F * smooth(exit) + 3.0F * (float)Math.sin(elapsed * 0.043) * smooth(entry);
         float roll = -0.24F * (1.0F - smooth(entry)) + 0.2F * smooth(exit) + 0.012F * (float)Math.sin(elapsed * 0.027);
         double angle = color.angle(elapsed, spin);
         double speed = Math.abs(color.angle(elapsed + 0.2F, spin) - color.angle(elapsed - 0.2F, spin)) / 0.4;
         float blur = (float)Math.clamp(speed / 0.34 * 5.0, 0.0, 5.0);
         int low = (int)blur;
         updateNeedle(color, elapsed, spin);
         g.pose().pushMatrix();
         g.pose().translate(cx, cy);
         g.pose().rotate(roll);
         g.pose().scale(size / 512.0F, size / 512.0F);
         g.pose().pushMatrix();
         g.pose().rotate((float)angle);
         texture(g, low == 0 ? "wheel" : "wheel_blur_" + low, -256, -256, 512, 512, fade);
         if (low < 5 && blur > low) {
            texture(g, "wheel_blur_" + (low + 1), -256, -256, 512, 512, (blur - low) * fade);
         }

         texture(g, "result_" + color.command, -256, -256, 512, 512, result * fade);
         texture(g, "glitter_a", -256, -256, 512, 512, (0.4F + 0.3F * (float)Math.sin(elapsed * 0.13)) * fade);
         texture(g, "glitter_b", -256, -256, 512, 512, (0.35F + 0.3F * (float)Math.sin(elapsed * 0.11 + 2.0)) * fade);
         g.pose().popMatrix();
         g.pose().pushMatrix();
         g.pose().rotate(0.06F * (float)Math.sin(elapsed * 0.019));
         texture(g, "rim", -256, -256, 512, 512, fade);
         float flash = age >= 0.0F ? 0.5F * (float)Math.exp(-age / 15.0F) : 0.3F * (float)Math.sin(entry * Math.PI);
         texture(g, "rim_glitter", -256, -256, 512, 512, Math.min(1.0F, 0.5F + 0.3F * (float)Math.sin(elapsed * 0.09) + flash) * fade);
         g.pose().popMatrix();
         texture(g, "hub", -256, -256, 512, 512, fade);
         g.pose().pushMatrix();
         g.pose().rotate(0.15F * (float)Math.sin(elapsed * 0.024));
         texture(g, "shine", -256, -256, 512, 512, fade);
         g.pose().popMatrix();
         int picked = Math.floorMod((int)Math.round(-angle / (Math.PI / 4)), 8);
         String pointer = elapsed >= spin ? color.command : RouletteColor.values()[picked].command;
         g.pose().pushMatrix();
         g.pose().translate(0.0F, -238.0F);
         g.pose().rotate(needleAngle);
         texture(g, "pointer_" + pointer, -40, -31, 80, 108, fade);
         g.pose().popMatrix();
         g.pose().popMatrix();
      }
   }

   private RouletteHud() {
   }
}
