package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.DinoTravelPayload;
import com.ghostsafio.mitomod.ModNave;
import com.ghostsafio.mitomod.NaveEntity;
import com.ghostsafio.mitomod.NaveInputPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class NaveClient {
   private static final boolean AUTO_THIRD_PERSON = true;
   private static int noLoadingTicks = 0;
   private static int lastFlags = -1;
   private static int resendCooldown = 0;
   private static boolean wasRiding = false;
   private static int notRidingTicks = 0;
   private static CameraType previousCamera = null;

   public static boolean suppressLoadingScreen() {
      return noLoadingTicks > 0;
   }

   public static void suppressLoading(int ticks) {
      noLoadingTicks = Math.max(noLoadingTicks, ticks);
   }

   public static void register() {
      ClientPlayNetworking.registerGlobalReceiver(DinoTravelPayload.TYPE, (payload, context) -> context.client().execute(() -> suppressLoading(100)));
      EntityRendererRegistry.register(ModNave.NAVE, NaveRenderer::new);
      NaveCommands.register();
      ClientTickEvents.END_CLIENT_TICK.register(NaveClient::tick);
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(net.minecraft.resources.Identifier.fromNamespaceAndPath("mod", "hud_20130_2385"), NaveHud::render);
   }

   private static void tick(Minecraft mc) {
      SpaceClient.clientTick(mc);
      boolean riding = mc.player != null && mc.player.getVehicle() instanceof NaveEntity;
      if (riding && ((NaveEntity)mc.player.getVehicle()).getPhase() > 0) {
         noLoadingTicks = 100;
      } else if (noLoadingTicks > 0) {
         noLoadingTicks--;
      }

      if (riding) {
         notRidingTicks = 0;
         if (!wasRiding) {
            previousCamera = mc.options.getCameraType();
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            wasRiding = true;
         }
      } else if (wasRiding && ++notRidingTicks > 10) {
         wasRiding = false;
         if (previousCamera != null) {
            mc.options.setCameraType(previousCamera);
         }
      }

      if (!riding) {
         lastFlags = -1;
      } else {
         int flags = mc.options.keyJump.isDown() ? 1 : 0;
         if (flags != lastFlags || ++resendCooldown >= 10) {
            ClientPlayNetworking.send(new NaveInputPayload(flags));
            lastFlags = flags;
            resendCooldown = 0;
         }
      }
   }
}
