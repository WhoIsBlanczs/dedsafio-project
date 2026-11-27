package net.ruleta.mixin;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Proteccion;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ExplosionCentro;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.ExplosionInmune;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionCentro {
   @Shadow
   @Final
   private Level field_9187;
   @Shadow
   @Final
   private double field_9195;
   @Shadow
   @Final
   private double field_9192;
   @Shadow
   @Final
   private double field_9189;
   @Unique
   private boolean ruleta$cancelada;

   @Override
   public Vec3 ruleta$centro() {
      return new Vec3(this.field_9195, this.field_9192, this.field_9189);
   }

   @Inject(method = "explode", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$inicio(CallbackInfo ci) {
      if (!this.field_9187.isClientSide() && Bus.hayOyentes(ExplosionEvent.Start.class) && Bus.post(new ExplosionEvent.Start(this.field_9187, (Explosion)(Object)this))) {
         this.ruleta$cancelada = true;
         ci.cancel();
      }
   }

   @Inject(method = "finalizeExplosion", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$fin(boolean particulas, CallbackInfo ci) {
      Level nivel = this.field_9187;
      ((Explosion)(Object)this).getToBlow().removeIf(pos -> Proteccion.protegido(nivel.getBlockState(pos).getBlock()));
      if (this.ruleta$cancelada) {
         ci.cancel();
      }
   }

   @Redirect(
      method = "explode",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      ),
      require = 0
   )
   private List<Entity> ruleta$detonar(Level nivel, @Nullable Entity origen, AABB zona) {
      List<Entity> entidades = nivel.getEntities(origen, zona);
      entidades.removeIf(e -> e instanceof ExplosionInmune);
      if (!nivel.isClientSide() && Bus.hayOyentes(ExplosionEvent.Detonate.class)) {
         Bus.post(new ExplosionEvent.Detonate(nivel, (Explosion)(Object)this, entidades));
      }

      return entidades;
   }
}
