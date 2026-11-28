package net.minecraftforge.common.util;

import java.util.function.Supplier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;

public class ForgeSoundType extends SoundType {
   private final Supplier<SoundEvent> breakSound;
   private final Supplier<SoundEvent> stepSound;
   private final Supplier<SoundEvent> placeSound;
   private final Supplier<SoundEvent> fallSound;
   private final Supplier<SoundEvent> hitSound;

   public ForgeSoundType(
      float volume,
      float pitch,
      Supplier<SoundEvent> breakSound,
      Supplier<SoundEvent> stepSound,
      Supplier<SoundEvent> placeSound,
      Supplier<SoundEvent> fallSound,
      Supplier<SoundEvent> hitSound
   ) {
      super(volume, pitch, null, null, null, null, null);
      this.breakSound = breakSound;
      this.stepSound = stepSound;
      this.placeSound = placeSound;
      this.fallSound = fallSound;
      this.hitSound = hitSound;
   }

   public SoundEvent getBreakSound() {
      return this.breakSound.get();
   }

   public SoundEvent getStepSound() {
      return this.stepSound.get();
   }

   public SoundEvent getPlaceSound() {
      return this.placeSound.get();
   }

   public SoundEvent getFallSound() {
      return this.fallSound.get();
   }

   public SoundEvent getHitSound() {
      return this.hitSound.get();
   }
}
