package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionResult;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public final class HeartItem extends Item {
   public static final int MAX_HEARTS = 50;
   private static final Identifier RED_MODIFIER = Identifier.fromNamespaceAndPath("nexo", "red_hearts");
   private static final int EFFECT_TICKS = 300;
   private static final int GOLD_HEARTS_TICKS = 3600;
   static final AttachmentType<Integer> RED_HEARTS = AttachmentRegistry.<Integer>builder()
      .persistent(Codec.INT)
      .copyOnDeath()
      .buildAndRegister(Identifier.fromNamespaceAndPath("nexo", "red_hearts"));
   static final AttachmentType<Float> LIMBO_LOST = AttachmentRegistry.<Float>builder()
      .persistent(Codec.FLOAT)
      .buildAndRegister(Identifier.fromNamespaceAndPath("nexo", "limbo_lost_health"));
   private final HeartItem.Kind kind;

   public HeartItem(Item.Properties var1, HeartItem.Kind var2) {
      super(var1);
      this.kind = var2;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (this.kind == HeartItem.Kind.ROJO && atMaxHearts(var2)) {
         if (!var1.isClientSide()) {
            com.novastudio.dedsafio4.compat.Compat.mensaje(var2, Component.translatable("message.nexo.max_hearts").withStyle(ChatFormatting.RED), true);
         }

         return InteractionResult.FAIL;
      } else {
         return super.use(var1, var2, var3);
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      if (!var2.isClientSide() && var3 instanceof Player var4) {
         switch (this.kind) {
            case ROJO:
               if (atMaxHearts(var4)) {
                  return var1;
               }

               data(var4).setAttached(RED_HEARTS, (Integer)data(var4).getAttachedOrElse(RED_HEARTS, 0) + 1);
               applyRedHearts(var4);
               var4.heal(2.0F);
               break;
            case DORADO:
               var4.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 4));
               var4.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 0));
               var4.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
               break;
            case LIMBO:
               data(var4).removeAttached(RED_HEARTS);
               applyRedHearts(var4);
               var4.setHealth(var4.getMaxHealth());
         }
      }

      return super.finishUsingItem(var1, var2, var3);
   }

   private static boolean atMaxHearts(Player var0) {
      return var0.getMaxHealth() + 2.0F > 100.0F;
   }

   private static AttachmentTarget data(Entity var0) {
      return var0;
   }

   static void applyRedHearts(Player var0) {
      int var1 = (Integer)data(var0).getAttachedOrElse(RED_HEARTS, 0);
      AttributeInstance var2 = var0.getAttribute(Attributes.MAX_HEALTH);
      if (var2 != null) {
         var2.removeModifier(RED_MODIFIER);
         if (var1 > 0) {
            var2.addPermanentModifier(new AttributeModifier(RED_MODIFIER, 2.0 * var1, AttributeModifier.Operation.ADD_VALUE));
         }
      }
   }

   static void registerEvents() {
      ServerPlayerEvents.AFTER_RESPAWN.register((AfterRespawn)(var0, var1, var2) -> applyRedHearts(var1));
   }

   public static enum Kind {
      ROJO,
      DORADO,
      LIMBO;
   }
}
