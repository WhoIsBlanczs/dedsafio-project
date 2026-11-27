package net.ruleta.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ruleta.compat.Lanzador;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
   @Unique
   private DamageSource ruleta$fuente;

   @Inject(method = "die", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$morir(DamageSource fuente, CallbackInfo ci) {
      if (Lanzador.muerte((LivingEntity)(Object)this, fuente)) {
         ci.cancel();
      }
   }

   @Inject(method = "actuallyHurt", at = @At("HEAD"), require = 0)
   private void ruleta$guardarFuente(DamageSource fuente, float cantidad, CallbackInfo ci) {
      this.ruleta$fuente = fuente;
   }

   @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true, require = 0)
   private float ruleta$danoBruto(float cantidad) {
      return Lanzador.dano((LivingEntity)(Object)this, this.ruleta$fuente, cantidad);
   }

   @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 1), argsOnly = true, require = 0)
   private float ruleta$danoFinal(float cantidad) {
      return Lanzador.danoTrasArmadura((LivingEntity)(Object)this, this.ruleta$fuente, cantidad);
   }

   @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true, require = 0)
   private void ruleta$visibilidad(@Nullable Entity observador, CallbackInfoReturnable<Double> cir) {
      cir.setReturnValue(Lanzador.visibilidad((LivingEntity)(Object)this, observador, cir.getReturnValueD()));
   }

   @Inject(method = "tick", at = @At("HEAD"), require = 0)
   private void ruleta$tick(CallbackInfo ci) {
      Lanzador.tick((LivingEntity)(Object)this);
   }

   @Redirect(
      method = "completeUsingItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
      ),
      require = 0
   )
   private ItemStack ruleta$terminarDeUsar(ItemStack objeto, Level nivel, LivingEntity quien) {
      ItemStack copia = objeto.copy();
      ItemStack resultado = objeto.finishUsingItem(nivel, quien);
      return Lanzador.terminarDeUsar(quien, copia, resultado);
   }

   @Redirect(method = "checkTotemDeathProtection", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), require = 0)
   private void ruleta$gastarTotem(ItemStack totem, int cuanto, DamageSource fuente) {
      ItemStack copia = totem.copy();
      totem.shrink(cuanto);
      Lanzador.totem((LivingEntity)(Object)this, fuente, copia);
   }

   @Inject(method = "dropAllDeathLoot", at = @At("TAIL"), require = 0)
   private void ruleta$botin(ServerLevel nivel, DamageSource fuente, CallbackInfo ci) {
      Lanzador.botin((LivingEntity)(Object)this, fuente);
   }
}
