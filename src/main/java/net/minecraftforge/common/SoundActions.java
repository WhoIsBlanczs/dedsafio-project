package net.minecraftforge.common;

public enum SoundActions {
   BUCKET_FILL("bucket_fill"),
   BUCKET_EMPTY("bucket_empty"),
   FLUID_VAPORIZE("fluid_vaporize");

   private final String name;

   private SoundActions(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
