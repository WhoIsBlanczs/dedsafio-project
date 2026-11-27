package net.ruleta.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.ruleta.client.tornado.TornadoCliente;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
   @Inject(method = "tick", at = @At("TAIL"))
   private void ruleta$sinControlAtrapado(boolean ralentizado, float factor, CallbackInfo ci) {
      if (TornadoCliente.atrapado()) {
         this.up = false;
         this.down = false;
         this.left = false;
         this.right = false;
         this.jumping = false;
         this.shiftKeyDown = false;
         this.forwardImpulse = 0.0F;
         this.leftImpulse = 0.0F;
      }
   }
}
