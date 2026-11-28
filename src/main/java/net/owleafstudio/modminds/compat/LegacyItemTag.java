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
      () -> DataComponentType.<CompoundTag>builder()
         .persistent(CompoundTag.CODEC)
         .networkSynchronized(ByteBufCodecs.compoundTagCodec(() -> NbtAccounter.create(2097152L)))
         .build()
   );

   private LegacyItemTag() {
   }

   public static void flush() {
      DeferredRegister.flushAll();
   }

   /**
    * CompoundTag es final en 26.3: se devuelve una copia propia del stack, ya guardada en el componente.
    * Los cambios sobre ella quedan en ese stack y se sincronizan porque el componente es un objeto nuevo.
    */
   public static CompoundTag of(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && TAG.isPresent()) {
         CompoundTag stored = stack.get(TAG.get());
         CompoundTag own = stored == null ? new CompoundTag() : stored.copy();
         stack.set(TAG.get(), own);
         return own;
      } else {
         return new CompoundTag();
      }
   }
}
