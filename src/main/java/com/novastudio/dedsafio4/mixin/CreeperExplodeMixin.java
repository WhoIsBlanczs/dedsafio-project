package com.novastudio.dedsafio4.mixin;

import com.novastudio.dedsafio4.CreeperAbilities;
import com.novastudio.dedsafio4.CreeperGeoEntity;
import com.novastudio.dedsafio4.CreeperNuke;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Creeper.class})
public abstract class CreeperExplodeMixin {
   @Shadow
   private int swell;
   @Shadow
   private int explosionRadius;

   @Shadow
   public abstract int getSwellDir();

   @Inject(
      method = {"explodeCreeper"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nova$creeperAbilities(CallbackInfo ci) {
      if ((Object)this instanceof CreeperGeoEntity geo && !geo.level().isClientSide()) {
         String id = geo.animationId();
         if (id.equals("creeper")) {
            CreeperNuke.explode(geo);
            ci.cancel();
         } else {
            if (id.equals("metalico")) {
               this.explosionRadius = 2;
            }

            if (!CreeperAbilities.onExplode(geo)) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void nova$restartFuse(CallbackInfo ci) {
      if ((Object)this instanceof CreeperGeoEntity && this.getSwellDir() < 0) {
         this.swell = 0;
      }
   }
}
