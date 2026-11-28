package com.ghostsafio.mitomod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
   public static final SoundEvent SUPERNOVA = register("supernova");
   public static final SoundEvent SKIFF_APARECE = register("skiff_phora_aparece");
   public static final SoundEvent SKIFF_IDLE = register("skiff_phora_idle");
   public static final SoundEvent POD_APARECE = register("pod_phora_aparece");
   public static final SoundEvent POD_IDLE = register("pod_phora_idle");
   public static final SoundEvent POD_LASER = register("pod_phora_laser");

   private static SoundEvent register(String name) {
      ResourceLocation id = Ghostsafio.id(name);
      return (SoundEvent)Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
   }

   public static void initialize() {
   }
}
