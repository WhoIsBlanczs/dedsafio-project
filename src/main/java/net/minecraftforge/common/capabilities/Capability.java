package net.minecraftforge.common.capabilities;

public class Capability<T> {
   private final String name;

   public Capability(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean shouldAutoRegister() {
      return true;
   }

   @Override
   public boolean equals(Object other) {
      return other instanceof Capability<?> cap && this.name.equals(cap.name);
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }
}
