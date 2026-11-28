package com.ghostsafio.mitomod.mixin;

import com.ghostsafio.mitomod.ModItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
   @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
   private void ghostsafio$useMontanaTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         LivingEntity self = (LivingEntity)(Object)this;

         for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = self.getItemInHand(hand);
            if (stack.is(ModItems.TOTEM_MONTANA)) {
               ItemStack used = stack.copy();
               stack.shrink(1);
               if (self instanceof ServerPlayer player) {
                  player.awardStat(Stats.ITEM_USED.get(ModItems.TOTEM_MONTANA), 1);
                  CriteriaTriggers.USED_TOTEM.trigger(player, used);
                  self.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
               }

               self.setHealth(1.0F);
               self.removeAllEffects();
               self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
               self.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
               self.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
               self.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 254));
               self.level().broadcastEntityEvent(self, (byte)35);
               cir.setReturnValue(true);
               return;
            }
         }
      }
   }
}
