package net.ruleta.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.portal.DimensionTransition;
import net.ruleta.compat.Bus;
import net.ruleta.compat.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin {
   @Unique
   private float ruleta$dano = 5.0F;
   @Unique
   private boolean ruleta$anulada;

   @Redirect(
      method = "onHit",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/world/level/portal/DimensionTransition;)Lnet/minecraft/world/entity/Entity;",
         ordinal = 0
      ),
      require = 0
   )
   private Entity ruleta$teletransportar(Entity dueno, DimensionTransition destino) {
      this.ruleta$dano = 5.0F;
      this.ruleta$anulada = false;
      ThrownEnderpearl yo = (ThrownEnderpearl)this;
      if (dueno instanceof ServerPlayer jugador && Bus.hayOyentes(EntityTeleportEvent.EnderPearl.class)) {
         EntityTeleportEvent.EnderPearl ev = new EntityTeleportEvent.EnderPearl(jugador, yo.getX(), yo.getY(), yo.getZ(), yo, 5.0F);
         if (Bus.post(ev)) {
            this.ruleta$anulada = true;
            return null;
         }

         this.ruleta$dano = ev.getAttackDamage();
      }

      return dueno.changeDimension(destino);
   }

   @Redirect(
      method = "onHit",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      require = 0
   )
   private boolean ruleta$danar(Entity dueno, DamageSource fuente, float cantidad) {
      return this.ruleta$anulada ? false : dueno.hurt(fuente, this.ruleta$dano);
   }
}
