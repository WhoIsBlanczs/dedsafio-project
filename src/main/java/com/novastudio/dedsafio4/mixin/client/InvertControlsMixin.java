package com.novastudio.dedsafio4.mixin.client;

import com.novastudio.dedsafio4.CreeperExtras;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardInput.class)
public abstract class InvertControlsMixin extends ClientInput {
   @Inject(method = "tick", at = @At("TAIL"))
   private void nova$invert(CallbackInfo ci) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && CreeperExtras.CONTROLES_INVERTIDOS != null && player.hasEffect(CreeperExtras.CONTROLES_INVERTIDOS)) {
         this.moveVector = new Vec2(-this.moveVector.x, -this.moveVector.y);
         Input k = this.keyPresses;
         this.keyPresses = new Input(k.backward(), k.forward(), k.right(), k.left(), k.jump(), k.shift(), k.sprint());
      }
   }
}
