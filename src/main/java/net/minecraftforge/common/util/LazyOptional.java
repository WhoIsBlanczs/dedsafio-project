package net.minecraftforge.common.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class LazyOptional<T> {
   private static final LazyOptional<?> EMPTY = new LazyOptional(null);
   @Nullable
   private Supplier<? extends T> supplier;
   @Nullable
   private T value;
   private boolean resolved;

   private LazyOptional(@Nullable T value) {
      this.value = value;
      this.resolved = true;
   }

   private LazyOptional(Supplier<? extends T> supplier) {
      this.supplier = supplier;
   }

   public static <T> LazyOptional<T> of(Supplier<? extends T> supplier) {
      return new LazyOptional<>(supplier);
   }

   public static <T> LazyOptional<T> empty() {
      return (LazyOptional<T>)EMPTY;
   }

   public static <T> LazyOptional<T> ofNullable(@Nullable T value) {
      return value == null ? empty() : new LazyOptional<>(value);
   }

   @Nullable
   private T resolve() {
      if (!this.resolved) {
         this.value = (T)(this.supplier == null ? null : this.supplier.get());
         this.resolved = true;
      }

      return this.value;
   }

   public <X> LazyOptional<X> cast() {
      return (LazyOptional<X>)this;
   }

   public void ifPresent(Consumer<? super T> consumer) {
      T v = this.resolve();
      if (v != null) {
         consumer.accept(v);
      }
   }

   public T orElse(T other) {
      T v = this.resolve();
      return v == null ? other : v;
   }

   public <X extends Throwable> T orElseThrow(Supplier<? extends X> exception) throws X {
      T v = this.resolve();
      if (v == null) {
         throw exception.get();
      } else {
         return v;
      }
   }

   public <U> LazyOptional<U> map(Function<? super T, ? extends U> fn) {
      T v = this.resolve();
      return v == null ? empty() : new LazyOptional<>(fn.apply(v));
   }

   public boolean isPresent() {
      return this.resolve() != null;
   }

   public void invalidate() {
   }
}
