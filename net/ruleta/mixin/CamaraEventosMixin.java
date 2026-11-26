package net.ruleta.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.ViewportEvent;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public abstract class CamaraEventosMixin {
   @Shadow
   private Quaternionf field_21518;
   @Shadow
   private float field_18717;
   @Shadow
   private float field_18718;

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Inject(
      method = {"setup"},
      at = {@At("TAIL")},
      require = 0
   )
   private void ruleta$angulos(BlockGetter nivel, Entity entidad, boolean tercera, boolean invertida, float parcial, CallbackInfo ci) {
      if (Bus.hayOyentes(ViewportEvent.ComputeCameraAngles.class)) {
         ViewportEvent.ComputeCameraAngles e = new ViewportEvent.ComputeCameraAngles((Camera)this, parcial, this.field_18718, this.field_18717, 0.0F);
         Bus.post(e);
         if (e.getYaw() != this.field_18718 || e.getPitch() != this.field_18717) {
            this.method_19325(e.getYaw(), e.getPitch());
         }

         if (e.getRoll() != 0.0F) {
            this.field_21518.rotateZ(-e.getRoll() * (float) (Math.PI / 180.0));
         }
      }
   }
}
