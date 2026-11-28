package com.watonstudio.creepermetalico.mixin;

import com.watonstudio.creepermetalico.CreeperMetalicoMod;
import com.watonstudio.creepermetalico.TotemPopMessages;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class TotemEonMixin {
   @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
   private void watonstudio$usarTotemEon(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
      LivingEntity entidad = (LivingEntity)this;
      if (entidad instanceof ServerPlayer jugador) {
         if (!jugador.level().isClientSide) {
            ItemStack principal = jugador.getMainHandItem();
            ItemStack secundaria = jugador.getOffhandItem();
            ItemStack elegido = null;
            if (principal.is(CreeperMetalicoMod.TOTEM_EON)) {
               elegido = principal;
            } else if (secundaria.is(CreeperMetalicoMod.TOTEM_EON)) {
               elegido = secundaria;
            } else {
               if (principal.is(Items.TOTEM_OF_UNDYING) || secundaria.is(Items.TOTEM_OF_UNDYING)) {
                  return;
               }

               for (int i = 0; i < jugador.getInventory().getContainerSize(); i++) {
                  ItemStack stack = jugador.getInventory().getItem(i);
                  if (stack.is(CreeperMetalicoMod.TOTEM_EON)) {
                     elegido = stack;
                     break;
                  }
               }
            }

            if (elegido != null) {
               Component nombreTotem = elegido.getHoverName().copy();
               elegido.shrink(1);
               jugador.setHealth(1.0F);
               jugador.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 4));
               TotemPopMessages.announcePop(jugador, nombreTotem);
               jugador.level().broadcastEntityEvent(jugador, (byte)36);
               cir.setReturnValue(true);
            }
         }
      }
   }
}
