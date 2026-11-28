package net.owleafstudio.modminds.compat;

import java.util.IdentityHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.ITeleporter;

public final class CompatAccess {
   private static final AttachmentType<CompoundTag> PERSISTENT_DATA = AttachmentRegistry.create(
      Identifier.fromNamespaceAndPath("modminds", "persistent_data"), builder -> builder.initializer(CompoundTag::new).copyOnDeath().persistent(CompoundTag.CODEC)
   );
   private static final Map<MobEffect, Holder<MobEffect>> EFFECT_HOLDERS = new IdentityHashMap<>();
   private static volatile HolderLookup.Provider serverRegistries;
   private static volatile HolderLookup.Provider clientRegistries;
   private static volatile Thread serverRegistriesThread;
   private static volatile Thread clientRegistriesThread;

   private CompatAccess() {
   }

   public static MinecraftServer server(Entity entity) {
      return entity != null && entity.level() != null ? entity.level().getServer() : null;
   }

   public static MinecraftServer server(Level level) {
      return level == null ? null : level.getServer();
   }

   public static CompoundTag persistentData(Entity entity) {
      return entity == null ? new CompoundTag() : (CompoundTag)entity.getAttachedOrCreate(PERSISTENT_DATA);
   }

   public static CompoundTag itemTag(ItemStack stack) {
      return LegacyItemTag.of(stack);
   }

   public static synchronized Holder<MobEffect> effect(MobEffect effect) {
      return EFFECT_HOLDERS.computeIfAbsent(effect, BuiltInRegistries.MOB_EFFECT::wrapAsHolder);
   }

   public static Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
      return registered(Registries.ENCHANTMENT, key);
   }

   public static Holder<Potion> potion(ResourceKey<Potion> key) {
      return registered(Registries.POTION, key);
   }

   public static <T> Holder<T> registered(ResourceKey<? extends Registry<T>> registryKey, ResourceKey<T> key) {
      HolderLookup.Provider provider = providerForCurrentThread();
      if (provider == null) {
         throw new IllegalStateException("Entry " + key + " requested before the registry provider was bound");
      } else {
         return provider.lookupOrThrow(registryKey).getOrThrow(key);
      }
   }

   private static HolderLookup.Provider providerForCurrentThread() {
      Thread thread = Thread.currentThread();
      if (thread == serverRegistriesThread) {
         return serverRegistries;
      } else if (thread == clientRegistriesThread) {
         return clientRegistries;
      } else {
         return serverRegistries != null ? serverRegistries : clientRegistries;
      }
   }

   public static int enchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key) {
      ItemEnchantments enchantments = stack.getEnchantments();
      return enchantments.isEmpty() ? 0 : enchantments.getLevel(enchantment(key));
   }

   public static void bindRegistries(HolderLookup.Provider provider, boolean serverSide) {
      if (serverSide) {
         serverRegistriesThread = provider == null ? null : Thread.currentThread();
         serverRegistries = provider;
      } else {
         clientRegistriesThread = provider == null ? null : Thread.currentThread();
         clientRegistries = provider;
      }
   }

   public static void enchant(ItemStack stack, ResourceKey<Enchantment> key, int level) {
      ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(
         stack.has(DataComponents.ENCHANTMENTS) ? (ItemEnchantments)stack.get(DataComponents.ENCHANTMENTS) : ItemEnchantments.EMPTY
      );
      mutable.set(enchantment(key), level);
      stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
   }

   public static Entity changeDimension(Entity entity, ServerLevel dest, ITeleporter teleporter) {
      return entity != null && dest != null && entity.level() instanceof ServerLevel current
         ? teleporter.placeEntity(entity, current, dest, entity.getYRot(), reposition -> entity)
         : entity;
   }

   public static boolean canChangeDimensions(Entity entity) {
      return entity != null && entity.canChangeDimensions(entity.level(), entity.level());
   }

   public static EquipmentSlot slot(InteractionHand hand) {
      return hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
   }
}
