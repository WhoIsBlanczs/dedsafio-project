package net.ruleta.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.ruleta.compat.Bus;
import net.ruleta.compat.Lanzador;
import net.ruleta.compat.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public abstract class PlayerMixin {
   @Unique
   private DamageSource ruleta$fuenteJugador;

   @Inject(
      method = {"actuallyHurt"},
      at = {@At("HEAD")},
      require = 0
   )
   private void ruleta$guardarFuente(DamageSource fuente, float cantidad, CallbackInfo ci) {
      this.ruleta$fuenteJugador = fuente;
   }

   @ModifyVariable(
      method = {"actuallyHurt"},
      at = @At("HEAD"),
      argsOnly = true,
      require = 0
   )
   private float ruleta$danoBruto(float cantidad) {
      return Lanzador.dano((LivingEntity)this, this.ruleta$fuenteJugador, cantidad);
   }

   @ModifyVariable(
      method = {"actuallyHurt"},
      at = @At(
         value = "STORE",
         ordinal = 1
      ),
      argsOnly = true,
      require = 0
   )
   private float ruleta$danoFinal(float cantidad) {
      return Lanzador.danoTrasArmadura((LivingEntity)this, this.ruleta$fuenteJugador, cantidad);
   }

   @Inject(
      method = {"getDisplayName"},
      at = {@At("RETURN")},
      cancellable = true,
      require = 0
   )
   private void ruleta$nombre(CallbackInfoReturnable<Component> cir) {
      if (Bus.hayOyentes(PlayerEvent.NameFormat.class)) {
         Player yo = (Player)this;
         PlayerEvent.NameFormat ev = new PlayerEvent.NameFormat(yo, yo.getName(), (Component)cir.getReturnValue());
         Bus.post(ev);
         cir.setReturnValue(ev.getDisplayname());
      }
   }
}
