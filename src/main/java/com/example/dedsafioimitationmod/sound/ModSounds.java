package com.example.dedsafioimitationmod.sound;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModSounds {
   public static final SoundEvent NAUTILUS_HURT = registerSoundEvent("entity.nautilus_oseo_g.hurt");
   public static final SoundEvent NAUTILUS_DEATH = registerSoundEvent("entity.nautilus_oseo_g.death");
   public static final SoundEvent NAUTILUS_ATTACK = registerSoundEvent("entity.nautilus_oseo_g.attack");
   public static final SoundEvent CEREBRO_ATAQUE = registerSoundEvent("cerebro_ataque");
   public static final SoundEvent CEREBRO_MUERTE = registerSoundEvent("cerebro_muerte");
   public static final SoundEvent NAUTILUS_P_ATTACK = registerSoundEvent("nautilus_oseo_p_attack");

   private static SoundEvent registerSoundEvent(String name) {
      Identifier id = Identifier.fromNamespaceAndPath("dedsafioimitationmod", name);
      return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
   }

   public static void registerSounds() {
   }
}
