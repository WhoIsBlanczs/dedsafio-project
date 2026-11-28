package com.watonstudio.creepermetalico.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@Environment(EnvType.CLIENT)
public final class EclipseAudio {
   public static final float START_VOLUME = 0.2F;
   public static final float POST_VOLUME = 0.1F;
   public static final float AMBIENT_VOLUME = 0.05F;
   public static final float DARK_VOLUME = 0.05F;
   public static final float RING_VOLUME = 0.4F;
   private static final int START_DURATION_TICKS = 920;
   private static final int POST_DELAY_TICKS = 40;
   private static final int DARK_PLAYS = 2;
   private static final ResourceLocation START_ID = ResourceLocation.fromNamespaceAndPath("watonstudio", "start");
   private static final ResourceLocation POST_ID = ResourceLocation.fromNamespaceAndPath("watonstudio", "post_eclipse");
   private static final ResourceLocation AMBIENT_ID = ResourceLocation.fromNamespaceAndPath("watonstudio", "ambient");
   private static final ResourceLocation DARK_ID = ResourceLocation.fromNamespaceAndPath("watonstudio", "dark");
   private static final ResourceLocation RING_ID = ResourceLocation.fromNamespaceAndPath("watonstudio", "ring");
   private static final SoundEvent START = SoundEvent.createVariableRangeEvent(START_ID);
   private static final SoundEvent POST = SoundEvent.createVariableRangeEvent(POST_ID);
   private static final SoundEvent AMBIENT = SoundEvent.createVariableRangeEvent(AMBIENT_ID);
   private static final SoundEvent DARK = SoundEvent.createVariableRangeEvent(DARK_ID);
   private static final SoundEvent RING = SoundEvent.createVariableRangeEvent(RING_ID);
   private static SoundInstance startInstance;
   private static SoundInstance postInstance;
   private static SoundInstance ambientInstance;
   private static SoundInstance darkInstance;
   private static SoundInstance ringInstance;
   private static int ticks;
   private static int startStartedAt = -1;
   private static int postDelayTicks = -1;
   private static int darkPlays;
   private static int darkStartedAt = -1000;
   private static boolean postSequence;
   private static boolean postPlayed;
   private static boolean flareWasHigh;
   private static boolean warned;

   private EclipseAudio() {
   }

   private static SoundManager sm() {
      return Minecraft.getInstance().getSoundManager();
   }

   private static void checkLoaded() {
      if (!warned) {
         SoundManager manager = sm();
         if (manager.getSoundEvent(START_ID) == null
            || manager.getSoundEvent(POST_ID) == null
            || manager.getSoundEvent(AMBIENT_ID) == null
            || manager.getSoundEvent(DARK_ID) == null
            || manager.getSoundEvent(RING_ID) == null) {
            warned = true;
            Minecraft.getInstance().gui.getChat().addMessage(Component.literal("[Eclipse] Falta cargar uno de los sonidos. Revisa sounds.json y los OGG."));
         }
      }
   }

   public static void playStart() {
      stopAll();
      checkLoaded();
      startInstance = SimpleSoundInstance.forUI(START, 1.0F, 0.2F);
      sm().play(startInstance);
      startStartedAt = ticks;
   }

   public static void tick(boolean running, boolean dark, float flare, float night) {
      ticks++;
      SoundManager manager = sm();
      boolean scheduledThisTick = false;
      if (startStartedAt >= 0 && ticks - startStartedAt >= 920) {
         if (startInstance != null) {
            manager.stop(startInstance);
            startInstance = null;
         }

         startStartedAt = -1;
         postSequence = true;
         postDelayTicks = 40;
         postPlayed = false;
         postInstance = null;
         scheduledThisTick = true;
      }

      if (postSequence && !scheduledThisTick && postDelayTicks > 0) {
         postDelayTicks--;
      }

      if (postSequence && postDelayTicks <= 0 && !postPlayed) {
         if (startInstance != null) {
            manager.stop(startInstance);
            startInstance = null;
         }

         if (ambientInstance != null) {
            manager.stop(ambientInstance);
            ambientInstance = null;
         }

         if (darkInstance != null) {
            manager.stop(darkInstance);
            darkInstance = null;
         }

         if (ringInstance != null) {
            manager.stop(ringInstance);
            ringInstance = null;
         }

         postInstance = SimpleSoundInstance.forUI(POST, 1.0F, 0.1F);
         manager.play(postInstance);
         postPlayed = true;
      } else if (postSequence && postPlayed && postInstance != null && !manager.isActive(postInstance)) {
         postSequence = false;
         postDelayTicks = -1;
         postInstance = null;
      }

      if (running && !postPlayed) {
         if (ambientInstance == null || !manager.isActive(ambientInstance)) {
            ambientInstance = SimpleSoundInstance.forUI(AMBIENT, 1.0F, 0.05F);
            manager.play(ambientInstance);
         }
      } else if (ambientInstance != null) {
         manager.stop(ambientInstance);
         ambientInstance = null;
      }

      if (!postPlayed && dark && darkPlays < 2 && (darkInstance == null || ticks - darkStartedAt >= 20 && !manager.isActive(darkInstance))) {
         darkInstance = SimpleSoundInstance.forUI(DARK, 1.0F, 0.05F);
         manager.play(darkInstance);
         darkStartedAt = ticks;
         darkPlays++;
      }

      if (!postPlayed && flare > 0.5F && !flareWasHigh) {
         ringInstance = SimpleSoundInstance.forUI(RING, 1.0F, 0.4F);
         manager.play(ringInstance);
         flareWasHigh = true;
      } else if (flare < 0.2F) {
         flareWasHigh = false;
      }
   }

   public static void stopAll() {
      SoundManager manager = sm();
      if (startInstance != null) {
         manager.stop(startInstance);
      }

      if (postInstance != null) {
         manager.stop(postInstance);
      }

      if (ambientInstance != null) {
         manager.stop(ambientInstance);
      }

      if (darkInstance != null) {
         manager.stop(darkInstance);
      }

      if (ringInstance != null) {
         manager.stop(ringInstance);
      }

      startInstance = null;
      postInstance = null;
      ambientInstance = null;
      darkInstance = null;
      ringInstance = null;
      ticks = 0;
      startStartedAt = -1;
      postDelayTicks = -1;
      darkPlays = 0;
      darkStartedAt = -1000;
      postSequence = false;
      postPlayed = false;
      flareWasHigh = false;
   }
}
