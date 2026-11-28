package com.watonstudio.creepermetalico;

import com.watonstudio.creepermetalico.net.OpenPhoraS2C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class AldeanoPhoraEntity extends Villager implements GeoEntity {
   private static final String NBT_ACTIVATED = "WatonPhoraActivated";
   private static final String NBT_PICKUP = "WatonPhoraPickup";
   private static final String NBT_HOVER = "WatonPhoraHoverY";
   private static final String NBT_HOVER_INIT = "WatonPhoraHoverInit";
   private static final String NBT_TRADE_VERSION = "WatonPhoraTradeVersion";
   private static final int TRADE_VERSION = 4;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.aldeano_phora.idle");
   private static final RawAnimation DORMANT = RawAnimation.begin().thenLoop("animation.aldeano_phora.dormido");
   private static final RawAnimation DESPERTAR = RawAnimation.begin().thenPlay("animation.aldeano_phora.despertar");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean activated = false;
   private int phoraAmbientTicks = 0;
   private static final EntityDataAccessor<Boolean> PHORA_ACTIVATED_DATA = SynchedEntityData.defineId(AldeanoPhoraEntity.class, EntityDataSerializers.BOOLEAN);
   private boolean pickupReady = false;
   private boolean hoverInitialized = false;
   private double hoverBaseY;
   private static final List<AldeanoPhoraEntity.TradeMaterial> TRADE_MATERIALS = List.of(
      new AldeanoPhoraEntity.TradeMaterial(Items.STICK, 52, 52, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.BAMBOO, 16, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.COBBLESTONE, 32, 48, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.DIRT, 32, 48, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.SAND, 16, 32, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.GRAVEL, 16, 32, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.OAK_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.SPRUCE_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.BIRCH_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.JUNGLE_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.ACACIA_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.DARK_OAK_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.MANGROVE_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.CHERRY_LOG, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.SUGAR_CANE, 16, 32, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.COAL, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WHEAT, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.CARROT, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.POTATO, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.PUMPKIN, 4, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.MELON_SLICE, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.CLAY_BALL, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.TORCH, 16, 32, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.PAPER, 12, 24, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.GLASS, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.BROWN_MUSHROOM, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.RED_MUSHROOM, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.SWEET_BERRIES, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.GLOW_BERRIES, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.DRIED_KELP, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.HAY_BLOCK, 4, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.MOSS_BLOCK, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.POINTED_DRIPSTONE, 8, 16, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.WHITE), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.ORANGE), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.MAGENTA), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.LIGHT_BLUE), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.YELLOW), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.LIME), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.PINK), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.GRAY), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.LIGHT_GRAY), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.CYAN), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.PURPLE), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.BLUE), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.BROWN), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.GREEN), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.RED), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.WOOL.pick(net.minecraft.world.item.DyeColor.BLACK), 8, 8, 1, 1, 3),
      new AldeanoPhoraEntity.TradeMaterial(Items.ICE, 52, 52, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.AMETHYST_SHARD, 29, 29, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.JACK_O_LANTERN, 4, 4, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.COPPER_INGOT, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.REDSTONE, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.LAPIS_LAZULI, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.IRON_INGOT, 6, 12, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.HONEYCOMB, 4, 8, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.GLOWSTONE_DUST, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.PRISMARINE_SHARD, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.PRISMARINE_CRYSTALS, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.QUARTZ, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.STRING, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.LEATHER, 4, 8, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.SLIME_BALL, 4, 8, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.CACTUS, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.KELP, 16, 32, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.BONE, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.FEATHER, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.FLINT, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.TERRACOTTA, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.TUFF, 16, 32, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.CALCITE, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.DRIPSTONE_BLOCK, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.COPPER_BLOCK.weathering().pick(net.minecraft.world.level.block.WeatheringCopper.WeatherState.UNAFFECTED), 4, 8, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.BRICK, 8, 16, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.HONEY_BOTTLE, 2, 4, 2, 1, 4),
      new AldeanoPhoraEntity.TradeMaterial(Items.GOLD_INGOT, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.OBSIDIAN, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.PACKED_ICE, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.ENDER_PEARL, 2, 4, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.BLAZE_ROD, 2, 4, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.NETHER_WART, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.MAGMA_CREAM, 2, 4, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.SEA_PICKLE, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.BLACKSTONE, 16, 32, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.NETHERRACK, 24, 48, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.CHORUS_FRUIT, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.PURPUR_BLOCK, 4, 8, 3, 1, 5),
      new AldeanoPhoraEntity.TradeMaterial(Items.NAUTILUS_SHELL, 1, 2, 3, 1, 6),
      new AldeanoPhoraEntity.TradeMaterial(Items.SEA_LANTERN, 2, 4, 4, 2, 7),
      new AldeanoPhoraEntity.TradeMaterial(Items.CAKE, 1, 1, 4, 1, 6),
      new AldeanoPhoraEntity.TradeMaterial(Items.GOLDEN_APPLE, 2, 3, 4, 2, 7),
      new AldeanoPhoraEntity.TradeMaterial(Items.ENDER_EYE, 2, 4, 4, 2, 7),
      new AldeanoPhoraEntity.TradeMaterial(Items.DIAMOND, 1, 1, 5, 2, 9),
      new AldeanoPhoraEntity.TradeMaterial(Items.SHULKER_SHELL, 1, 1, 5, 2, 9),
      new AldeanoPhoraEntity.TradeMaterial(Items.NETHERITE_SCRAP, 1, 1, 5, 3, 10),
      new AldeanoPhoraEntity.TradeMaterial(Items.TOTEM_OF_UNDYING, 1, 1, 5, 3, 10)
   );

   public AldeanoPhoraEntity(EntityType<? extends Villager> type, Level world) {
      super(type, world);
      this.setNoGravity(true);
      this.setNoAi(true);
      this.setSilent(true);
      this.setPersistenceRequired();
      this.setVillagerData(this.getVillagerData().withProfession(this.level().registryAccess(), VillagerProfession.FARMER).withLevel(1));
      this.setCustomName(null);
      this.setCustomNameVisible(false);
      this.setOffers(new MerchantOffers());
   }

   public static Builder createAttributes() {
      return Villager.createAttributes();
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PHORA_ACTIVATED_DATA, false);
   }

   private boolean isPhoraActivatedSynced() {
      return (Boolean)this.entityData.get(PHORA_ACTIVATED_DATA);
   }

   public void tick() {
      super.tick();
      this.setNoGravity(true);
      this.setNoAi(true);
      this.setSilent(!this.isPhoraActivatedSynced());
      if (!this.level().isClientSide()) {
         if (this.isPhoraActivatedSynced()) {
            this.phoraAmbientTicks++;
            if (this.phoraAmbientTicks >= 50) {
               this.phoraAmbientTicks = 0;
               this.level().playSound(null, this.blockPosition(), AldeanoPhoraRegistration.PHORA_AMBIENT_SOUND, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
         } else {
            this.phoraAmbientTicks = 0;
         }
      }

      this.setDeltaMovement(Vec3.ZERO);
      if (!this.hoverInitialized) {
         this.hoverBaseY = this.getY();
         this.hoverInitialized = true;
      }

      if (!this.level().isClientSide()) {
         double bob = 0.075 + Math.sin(this.tickCount * 0.14) * 0.07;
         this.setPos(this.getX(), this.hoverBaseY + bob, this.getZ());
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack held = player.getItemInHand(hand);
      if (held.is(AldeanoPhoraRegistration.LLAVE_COBRE_ITEM)) {
         if (!this.level().isClientSide()) {
            if (!this.activated) {
               this.activated = true;
               this.entityData.set(PHORA_ACTIVATED_DATA, true);
               this.phoraAmbientTicks = 0;
               this.setSilent(false);
               this.pickupReady = false;
               this.setVillagerData(this.getVillagerData().withProfession(this.level().registryAccess(), VillagerProfession.FARMER).withLevel(1));
               this.setVillagerXp(0);
               this.setPermanentlyInvulnerable(true);
               this.setOffers(new MerchantOffers());
               this.updateTrades();
               this.triggerAnim("phora_controller", "despertar");
               this.level().playSound(null, this.blockPosition(), AldeanoPhoraRegistration.PHORA_ACTIVAR_SOUND, SoundSource.NEUTRAL, 1.0F, 1.0F);
               com.watonstudio.creepermetalico.compat.Compat.mensaje(player, Component.literal("Aldeano Phora activado."), true);
            } else {
               this.rerollAllUnlockedOffers();
               this.pickupReady = true;
               com.watonstudio.creepermetalico.compat.Compat.mensaje(player, Component.literal("Phora reiniciado. AgÃ¡chate y usa la mano vacÃ\u00ada para recogerlo."), true);
            }

            if (!player.getAbilities().instabuild) {
               held.shrink(1);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         if (!this.level().isClientSide() && !this.activated) {
            com.watonstudio.creepermetalico.compat.Compat.mensaje(player, Component.literal("Necesitas una Llave de Cobre para activar al Phora."), true);
            return InteractionResult.SUCCESS;
         }

         if (!this.level().isClientSide() && this.pickupReady && player.isShiftKeyDown() && held.isEmpty()) {
            ItemStack phoraStack = new ItemStack(AldeanoPhoraRegistration.ALDEANO_PHORA_ITEM);
            if (!player.getInventory().add(phoraStack)) {
               if (this.level() instanceof net.minecraft.server.level.ServerLevel sl$) this.spawnAtLocation(sl$, phoraStack);
            }

            this.discard();
            return InteractionResult.SUCCESS;
         } else {
            if (!this.level().isClientSide()) {
               this.sendPhoraMenu(player);
            }

            return InteractionResult.SUCCESS;
         }
      }
   }

   private void sendPhoraMenu(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         ArrayList encodedOffers = new ArrayList();

         for (MerchantOffer offer : this.getOffers()) {
            ItemStack first = offer.getCostA();
            ItemStack second = offer.getCostB();
            ItemStack result = offer.getResult();
            encodedOffers.add(encodePhoraStack(first) + "|" + encodePhoraStack(second) + "|" + encodePhoraStack(result) + "|" + offer.isOutOfStock());
         }

         ServerPlayNetworking.send(serverPlayer, new OpenPhoraS2C(this.getId(), encodedOffers));
      }
   }

   private static String encodePhoraStack(ItemStack stack) {
      return stack != null && !stack.isEmpty() ? BuiltInRegistries.ITEM.getKey(stack.getItem()).toString() + "|" + stack.getCount() : "minecraft:air|0";
   }

   public boolean executePhoraTrade(Player player, int offerIndex) {
      if (!this.level().isClientSide() && this.activated && this.isAlive()) {
         if (player.distanceToSqr(this) > 64.0) {
            return false;
         }

         MerchantOffers offers = this.getOffers();
         if (offerIndex >= 0 && offerIndex < offers.size()) {
            MerchantOffer offer = (MerchantOffer)offers.get(offerIndex);
            if (offer.isOutOfStock()) {
               return false;
            } else {
               ItemStack first = offer.getCostA().copy();
               ItemStack second = offer.getCostB().copy();
               if (!hasPhoraTradeItems(player, first, second)) {
                  com.watonstudio.creepermetalico.compat.Compat.mensaje(player, Component.literal("No tienes los materiales necesarios."), true);
                  return false;
               } else {
                  ItemStack result = offer.getResult().copy();
                  consumePhoraTradeItems(player, first);
                  consumePhoraTradeItems(player, second);
                  offer.increaseUses();
                  this.rewardTradeXp(offer);
                  player.getInventory().placeItemBackInInventory(result, net.minecraft.util.Prediction.SERVER_ONLY);
                  this.sendPhoraMenu(player);
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean hasPhoraTradeItems(Player player, ItemStack first, ItemStack second) {
      if (!first.isEmpty() && !second.isEmpty() && ItemStack.isSameItemSameComponents(first, second)) {
         int required = first.getCount() + second.getCount();
         return countPhoraItems(player, first) >= required;
      } else {
         return (first.isEmpty() || countPhoraItems(player, first) >= first.getCount())
            && (second.isEmpty() || countPhoraItems(player, second) >= second.getCount());
      }
   }

   private static int countPhoraItems(Player player, ItemStack required) {
      int total = 0;
      Inventory inventory = player.getInventory();

      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         ItemStack current = inventory.getItem(slot);
         if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, required)) {
            total += current.getCount();
         }
      }

      return total;
   }

   private static void consumePhoraTradeItems(Player player, ItemStack required) {
      if (!required.isEmpty()) {
         int remaining = required.getCount();
         Inventory inventory = player.getInventory();

         for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack current = inventory.getItem(slot);
            if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, required)) {
               int amount = Math.min(current.getCount(), remaining);
               current.shrink(amount);
               remaining -= amount;
            }
         }

         inventory.setChanged();
      }
   }

   protected void rewardTradeXp(MerchantOffer offer) {
      int previousLevel = this.getVillagerData().level();
      super.rewardTradeXp(offer);
      if (this.activated && this.getVillagerData().level() > previousLevel) {
         this.updateTrades();
      }
   }

   protected void updateTrades() {
      MerchantOffers offers = this.getOffers();
      if (!this.activated) {
         this.setOffers(offers);
      } else {
         int level = Math.max(1, Math.min(5, this.getVillagerData().level()));
         this.appendOffersForLevel(offers, level, level * 2);
         this.setOffers(offers);
      }
   }

   private void rerollAllUnlockedOffers() {
      MerchantOffers offers = new MerchantOffers();
      int level = Math.max(1, Math.min(5, this.getVillagerData().level()));

      for (int unlockedLevel = 1; unlockedLevel <= level; unlockedLevel++) {
         this.appendOffersForLevel(offers, unlockedLevel, unlockedLevel * 2);
      }

      this.setOffers(offers);
   }

   private void appendOffersForLevel(MerchantOffers offers, int unlockLevel, int targetOffers) {
      Set<Item> alreadyUsed = new HashSet<>();

      for (MerchantOffer offer : offers) {
         ItemStack original = offer.getCostA();
         if (!original.isEmpty()) {
            alreadyUsed.add(original.getItem());
         }
      }

      List<AldeanoPhoraEntity.TradeMaterial> available = new ArrayList<>();

      for (AldeanoPhoraEntity.TradeMaterial material : TRADE_MATERIALS) {
         if (material.unlockLevel() == unlockLevel && !alreadyUsed.contains(material.item())) {
            available.add(material);
         }
      }

      Collections.shuffle(available);
      ThreadLocalRandom random = ThreadLocalRandom.current();

      while (offers.size() < targetOffers && !available.isEmpty()) {
         AldeanoPhoraEntity.TradeMaterial material = available.remove(available.size() - 1);
         int amount = material.min() + random.nextInt(material.max() - material.min() + 1);
         offers.add(
            new MerchantOffer(
               new ItemCost(material.item(), amount),
               Optional.empty(),
               new ItemStack(AldeanoPhoraRegistration.FRAGMENTO_PHORA_ITEM, material.fragments()),
               12,
               material.merchantXp(),
               0.05F
            )
         );
      }
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
      super.addAdditionalSaveData(nbt);
      nbt.putBoolean("WatonPhoraActivated", this.activated);
      nbt.putBoolean("WatonPhoraPickup", this.pickupReady);
      nbt.putBoolean("WatonPhoraHoverInit", this.hoverInitialized);
      nbt.putInt("WatonPhoraTradeVersion", 4);
      if (this.hoverInitialized) {
         nbt.putDouble("WatonPhoraHoverY", this.hoverBaseY);
      }
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
      super.readAdditionalSaveData(nbt);
      this.activated = nbt.getBooleanOr("WatonPhoraActivated", false);
      this.entityData.set(PHORA_ACTIVATED_DATA, this.activated);
      this.pickupReady = nbt.getBooleanOr("WatonPhoraPickup", false);
      this.hoverBaseY = this.getY() - 0.075;
      this.hoverInitialized = true;
      this.setVillagerData(this.getVillagerData().withProfession(this.level().registryAccess(), VillagerProfession.FARMER));
      this.setNoGravity(true);
      this.setNoAi(true);
      this.setSilent(true);
      this.setPermanentlyInvulnerable(this.activated);
      this.setCustomName(null);
      this.setCustomNameVisible(false);
      if (this.activated && nbt.getIntOr("WatonPhoraTradeVersion", 0) < 4) {
         this.setVillagerData(this.getVillagerData().withLevel(1));
         this.setVillagerXp(0);
         this.setOffers(new MerchantOffers());
         this.updateTrades();
      }
   }

   protected Component getTypeName() {
      return Component.literal("Aldeano Phora de Recursos");
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return null;
   }

   public void playWorkSound() {
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.EMPTY;
   }

   protected SoundEvent getTradeUpdatedSound(boolean sold) {
      return SoundEvents.EMPTY;
   }

   public void notifyTradeUpdated(ItemStack stack) {
   }

   public void playCelebrateSound() {
   }

   public boolean showProgressBar() {
      return true;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController<>("phora_controller", 0, state -> state.setAndContinue(this.isPhoraActivatedSynced() ? IDLE : DORMANT))
            .triggerableAnim("despertar", DESPERTAR)
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   private record TradeMaterial(Item item, int min, int max, int unlockLevel, int fragments, int merchantXp) {
   }
}
