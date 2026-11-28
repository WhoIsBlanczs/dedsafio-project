package net.minecraftforge.registries;

import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Holder;

public class ForgeRegistryHandle<T> {
   private final Registry<T> vanilla;

   public ForgeRegistryHandle(Registry<T> vanilla) {
      this.vanilla = vanilla;
   }

   public Registry<T> vanilla() {
      return this.vanilla;
   }

   public T getValue(Identifier name) {
      if (name == null) {
         return null;
      }
      Optional<T> value = this.vanilla.getOptional(name);
      if (value.isEmpty()) {
         value = this.vanilla.getOptional(vanillaDottedName(name));
      }
      return value.orElseGet(() -> this.vanilla.getValue(name));
   }

   static Identifier vanillaDottedName(Identifier name) {
      return Identifier.fromNamespaceAndPath("minecraft", name.getNamespace() + "." + name.getPath());
   }

   public Optional<T> getOptional(Identifier name) {
      return this.vanilla.getOptional(name);
   }

   public Optional<Holder.Reference<T>> getHolder(Identifier name) {
      return this.vanilla.get(name);
   }

   public Optional<Holder.Reference<T>> getHolder(T value) {
      return RegisterEvent.holderOf(this.vanilla, value);
   }

   public Holder.Reference<T> getHolderOrThrow(T value) {
      return this.getHolder(value).orElseThrow(() -> new IllegalStateException("Value is not registered: " + value));
   }

   public Identifier getKey(T value) {
      return this.vanilla.getKey(value);
   }

   public Set<Identifier> getKeys() {
      return this.vanilla.keySet();
   }

   public boolean containsKey(Identifier name) {
      return this.vanilla.containsKey(name);
   }
}
