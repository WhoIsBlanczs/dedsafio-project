package net.owleafstudio.modminds.compat;

import java.util.UUID;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class LegacyItemTag {
   private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, "modminds");
   public static final RegistryObject<DataComponentType<CompoundTag>> TAG = COMPONENTS.register(
      "legacy_tag",
      () -> DataComponentType.builder()
         .persistent(CompoundTag.CODEC)
         .networkSynchronized(ByteBufCodecs.compoundTagCodec(() -> NbtAccounter.create(2097152L)))
         .build()
   );

   private LegacyItemTag() {
   }

   public static void flush() {
      DeferredRegister.flushAll();
   }

   public static CompoundTag of(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && TAG.isPresent()) {
         CompoundTag stored = (CompoundTag)stack.get(TAG.get());
         return new LegacyItemTag.WriteThrough(stack, stored == null ? new CompoundTag() : stored);
      } else {
         return new CompoundTag();
      }
   }

   private static CompoundTag copyOf(CompoundTag source) {
      CompoundTag copy = new CompoundTag();

      for (String key : source.getAllKeys()) {
         Tag value = source.get(key);
         if (value != null) {
            copy.put(key, value);
         }
      }

      return copy;
   }

   private static final class WriteThrough extends CompoundTag {
      private final ItemStack owner;

      private WriteThrough(ItemStack owner, CompoundTag source) {
         CompoundTag snapshot = LegacyItemTag.copyOf(source);

         for (String key : snapshot.getAllKeys()) {
            super.put(key, snapshot.get(key));
         }

         this.owner = owner;
      }

      private void sync() {
         this.owner.set(LegacyItemTag.TAG.get(), LegacyItemTag.copyOf(this));
      }

      public Tag put(String key, Tag value) {
         super.put(key, value);
         this.sync();
         return value;
      }

      public void putByte(String key, byte value) {
         super.putByte(key, value);
         this.sync();
      }

      public void putShort(String key, short value) {
         super.putShort(key, value);
         this.sync();
      }

      public void putInt(String key, int value) {
         super.putInt(key, value);
         this.sync();
      }

      public void putLong(String key, long value) {
         super.putLong(key, value);
         this.sync();
      }

      public void putUUID(String key, UUID value) {
         super.putUUID(key, value);
         this.sync();
      }

      public void putFloat(String key, float value) {
         super.putFloat(key, value);
         this.sync();
      }

      public void putDouble(String key, double value) {
         super.putDouble(key, value);
         this.sync();
      }

      public void putString(String key, String value) {
         super.putString(key, value);
         this.sync();
      }

      public void putByteArray(String key, byte[] value) {
         super.putByteArray(key, value);
         this.sync();
      }

      public void putIntArray(String key, int[] value) {
         super.putIntArray(key, value);
         this.sync();
      }

      public void putLongArray(String key, long[] value) {
         super.putLongArray(key, value);
         this.sync();
      }

      public void putBoolean(String key, boolean value) {
         super.putBoolean(key, value);
         this.sync();
      }

      public void remove(String key) {
         super.remove(key);
         this.sync();
      }
   }
}
