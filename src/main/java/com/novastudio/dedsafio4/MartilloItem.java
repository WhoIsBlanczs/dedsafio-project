package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public final class MartilloItem extends Item {
   private static final int REPAIR = 10;
   private static final int REPAIRED = 0;
   private static final int NOTHING = 1;
   private static final int NO_XP = 2;
   private static final EquipmentSlot[] ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

   public MartilloItem() {
      super(
         new Item.Properties()
            .stacksTo(1)
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                  .build()
            )
      );
   }

   public static Object register() {
      Identifier var0 = Identifier.fromNamespaceAndPath("nexo", "martillo");
      return BuiltInRegistries.ITEM.containsKey(var0)
         ? BuiltInRegistries.ITEM.getValue(var0)
         : Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(var0), new MartilloItem());
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      if (!(var3 instanceof Player var5)) {
         return InteractionResult.PASS;
      } else if (var2.level().isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         int var6 = 1;

         for (EquipmentSlot var10 : ARMOR) {
            var6 = repair(var2, var5.getItemBySlot(var10));
            if (var6 != 1) {
               break;
            }
         }

         feedback(var2, var5, var6, "Su armadura no necesita reparación.");
         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      ItemStack var5 = var2.getOffhandItem();
      if (var3 != InteractionHand.MAIN_HAND || var5.isEmpty()) {
         return InteractionResult.PASS;
      } else if (var1.isClientSide()) {
         return var5.isDamaged() ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         int var6 = repair(var2, var5);
         feedback(var2, var2, var6, "No hay nada que reparar en la Mano Secundaria.");
         return var6 == 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
      }
   }

   private static int repair(Player var0, ItemStack var1) {
      if (!var1.isEmpty() && var1.isDamageableItem() && var1.isDamaged()) {
         int var2 = Math.min(10, var1.getDamageValue());
         if (!var0.isCreative()) {
            int var3 = xpPoints(var0);
            int var4 = (var2 + 1) / 2;
            if (var3 <= 0) {
               return 2;
            }

            if (var3 < var4) {
               var4 = var3;
               var2 = Math.min(var2, var3 * 2);
            }

            var0.giveExperiencePoints(-var4);
         }

         var1.setDamageValue(var1.getDamageValue() - var2);
         return 0;
      } else {
         return 1;
      }
   }

   static int xpPoints(Player var0) {
      int var1 = var0.experienceLevel;
      long var2 = var1 <= 16
         ? (long)var1 * var1 + 6L * var1
         : (var1 <= 31 ? (long)(2.5 * var1 * var1 - 40.5 * var1 + 360.0) : (long)(4.5 * var1 * var1 - 162.5 * var1 + 2220.0));
      return (int)Math.min(2147483647L, var2 + (long)(var0.experienceProgress * var0.getXpNeededForNextLevel()));
   }

   private static void feedback(Player var0, Player var1, int var2, String var3) {
      if (var2 == 0) {
         Level var4 = var0.level();
         var4.playSound(null, var1.getX(), var1.getY(), var1.getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.35F, 1.6F);
         var4.playSound(null, var0.getX(), var0.getY(), var0.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3F, 1.2F);
         if (var4 instanceof ServerLevel var5) {
            var5.sendParticles(ParticleTypes.HAPPY_VILLAGER, var1.getX(), var1.getY() + 1.0, var1.getZ(), 4, 0.35, 0.5, 0.35, 0.0);
         }
      } else {
         com.novastudio.dedsafio4.compat.Compat.mensaje(var0, Component.literal(var2 == 2 ? "§cNo tienes experiencia suficiente." : "§7" + var3), true);
      }
   }
}
