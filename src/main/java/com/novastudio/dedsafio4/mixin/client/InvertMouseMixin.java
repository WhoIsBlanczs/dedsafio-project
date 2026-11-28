package com.novastudio.dedsafio4.mixin.client;

import com.novastudio.dedsafio4.CreeperExtras;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin({Entity.class})
public abstract class InvertMouseMixin {
   @ModifyVariable(
      method = {"turn"},
      at = {@At("HEAD")},
      argsOnly = true,
      ordinal = 0
   )
   private double nova$invertX(double value) {
      return this.nova$inverted() ? -value : value;
   }

   @ModifyVariable(
      method = {"turn"},
      at = {@At("HEAD")},
      argsOnly = true,
      ordinal = 1
   )
   private double nova$invertY(double value) {
      return this.nova$inverted() ? -value : value;
   }

   private boolean nova$inverted() {
      LocalPlayer player = Minecraft.getInstance().player;
      return (Object)this == player && CreeperExtras.CONTROLES_INVERTIDOS != null && player.hasEffect(CreeperExtras.CONTROLES_INVERTIDOS);
   }
}
