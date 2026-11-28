package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperMetalicoMod;
import com.watonstudio.creepermetalico.TotemPopMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PlayerTotemMixin {
   @Unique
   private ItemStack watonstudio$capturedTotem = ItemStack.EMPTY;
   @Unique
   private boolean watonstudio$shouldAnnounce = false;

   @Inject(method = "tryUseTotem", at = @At("HEAD"))
   private void watonstudio$captureTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      this.watonstudio$capturedTotem = ItemStack.EMPTY;
      this.watonstudio$shouldAnnounce = false;
      if (this instanceof ServerPlayer player) {
         if (!player.getMainHandItem().is(CreeperMetalicoMod.TOTEM_EON) && !player.getOffhandItem().is(CreeperMetalicoMod.TOTEM_EON)) {
            ItemStack offhand = player.getOffhandItem();
            ItemStack mainhand = player.getMainHandItem();
            if (TotemPopMessages.isTotemLike(offhand)) {
               this.watonstudio$capturedTotem = offhand.copy();
               this.watonstudio$shouldAnnounce = true;
            } else if (TotemPopMessages.isTotemLike(mainhand)) {
               this.watonstudio$capturedTotem = mainhand.copy();
               this.watonstudio$shouldAnnounce = true;
            }
         }
      }
   }

   @Inject(method = "tryUseTotem", at = @At("RETURN"))
   private void watonstudio$announceTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      try {
         if (!cir.getReturnValueZ() || !this.watonstudio$shouldAnnounce || this.watonstudio$capturedTotem.isEmpty()) {
            return;
         }

         if (this instanceof ServerPlayer player) {
            TotemPopMessages.announcePop(player, this.watonstudio$capturedTotem.getHoverName());
            return;
         }
      } finally {
         this.watonstudio$capturedTotem = ItemStack.EMPTY;
         this.watonstudio$shouldAnnounce = false;
      }
   }
}
