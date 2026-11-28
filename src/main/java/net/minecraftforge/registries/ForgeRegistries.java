package net.minecraftforge.registries;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.fluids.FluidType;

public final class ForgeRegistries {
   public static final ForgeRegistryHandle<Block> BLOCKS = new ForgeRegistryHandle<>(BuiltInRegistries.BLOCK);
   public static final ForgeRegistryHandle<Item> ITEMS = new ForgeRegistryHandle<>(BuiltInRegistries.ITEM);
   public static final ForgeRegistryHandle<SoundEvent> SOUND_EVENTS = new ForgeRegistryHandle<>(BuiltInRegistries.SOUND_EVENT);
   public static final ForgeRegistryHandle<MobEffect> MOB_EFFECTS = new ForgeRegistryHandle<>(BuiltInRegistries.MOB_EFFECT);
   public static final ForgeRegistryHandle<Potion> POTIONS = new ForgeRegistryHandle<>(BuiltInRegistries.POTION);
   public static final ForgeRegistryHandle<CreativeModeTab> CREATIVE_MODE_TABS = new ForgeRegistryHandle<>(BuiltInRegistries.CREATIVE_MODE_TAB);
   public static final ForgeRegistryHandle<PoiType> POI_TYPES = new ForgeRegistryHandle<>(BuiltInRegistries.POINT_OF_INTEREST_TYPE);
   public static final ForgeRegistryHandle<EntityType<?>> ENTITY_TYPES = new ForgeRegistryHandle<>(BuiltInRegistries.ENTITY_TYPE);
   public static final ForgeRegistryHandle<Fluid> FLUIDS = new ForgeRegistryHandle<>(BuiltInRegistries.FLUID);
   public static final ForgeRegistryHandle<MenuType<?>> MENU_TYPES = new ForgeRegistryHandle<>(BuiltInRegistries.MENU);
   public static final ForgeRegistryHandle<ParticleType<?>> PARTICLE_TYPES = new ForgeRegistryHandle<>(BuiltInRegistries.PARTICLE_TYPE);

   private ForgeRegistries() {
   }

   static <T> Registry<T> resolve(ResourceKey<? extends Registry<T>> key) {
      Registry<?> vanilla = (Registry<?>)BuiltInRegistries.REGISTRY.getValue(key.location());
      return (Registry<T>)(vanilla == null ? null : vanilla);
   }

   public static final class Keys {
      public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("forge", "fluid_type"));
      public static final ResourceKey<Registry<PoiType>> POI_TYPES = Registries.POINT_OF_INTEREST_TYPE;

      private Keys() {
      }
   }
}
