package net.minecraftforge.registries;

import java.util.function.Supplier;
import net.minecraft.resources.Identifier;

public class RegistryObject<T> implements Supplier<T> {
   private final Identifier id;
   private T value;
   private boolean virtual;
   private DeferredRegister<T> owner;
   private Supplier<? extends T> supplier;

   RegistryObject(Identifier id) {
      this.id = id;
   }

   void bind(DeferredRegister<T> owner, Supplier<? extends T> supplier) {
      this.owner = owner;
      this.supplier = supplier;
   }

   void release() {
      this.owner = null;
      this.supplier = null;
   }

   @Override
   public T get() {
      if (this.value == null && this.supplier != null) {
         this.owner.materialise(this);
      }

      return this.value;
   }

   void setValue(T value, boolean virtual) {
      this.value = value;
      this.virtual = virtual;
   }

   Supplier<? extends T> pendingSupplier() {
      return this.supplier;
   }

   public T orElse(T other) {
      return this.value != null ? this.value : other;
   }

   public boolean isPresent() {
      return this.value != null;
   }

   public boolean isVirtual() {
      return this.virtual;
   }

   public Identifier getId() {
      return this.id;
   }

   Identifier registryKey() {
      return this.id;
   }
}
