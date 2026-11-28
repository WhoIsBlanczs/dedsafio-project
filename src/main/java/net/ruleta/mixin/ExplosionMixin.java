package net.ruleta.mixin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Proteccion;
import net.ruleta.compat.Bus;
import net.ruleta.compat.ExplosionCentro;
import net.ruleta.compat.ExplosionEvent;
import net.ruleta.compat.ExplosionInmune;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Eventos de explosión sobre ServerExplosion (26.3): inicio, bloques afectados y entidades alcanzadas. */
@Mixin(ServerExplosion.class)
public abstract class ExplosionMixin implements ExplosionCentro {
   @Shadow
   @Final
   private ServerLevel level;
   @Shadow
   @Final
   private Vec3 center;
   @Unique
   private List<BlockPos> ruleta$bloques;

   @Override
   public Vec3 ruleta$centro() {
      return this.center;
   }

   @Inject(method = "explode", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$inicio(CallbackInfoReturnable<Integer> cir) {
      if (Bus.hayOyentes(ExplosionEvent.Start.class) && Bus.post(new ExplosionEvent.Start(this.level, (Explosion)(Object)this))) {
         cir.setReturnValue(0);
      }
   }

   @Inject(method = "calculateExplodedPositions", at = @At("RETURN"), require = 0)
   private void ruleta$bloques(CallbackInfoReturnable<List<BlockPos>> cir) {
      List<BlockPos> lista = cir.getReturnValue();
      ServerLevel nivel = this.level;
      try {
         lista.removeIf(pos -> Proteccion.protegido(nivel.getBlockState(pos).getBlock()));
      } catch (UnsupportedOperationException e) {
         // lista inmutable: no hay nada que proteger
      }
      this.ruleta$bloques = lista;
   }

   @Redirect(
      method = "hurtEntities",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      ),
      require = 0
   )
   private List<Entity> ruleta$detonar(ServerLevel nivel, Entity origen, AABB zona) {
      List<Entity> entidades = nivel.getEntities(origen, zona);
      entidades.removeIf(e -> e instanceof ExplosionInmune);
      if (Bus.hayOyentes(ExplosionEvent.Detonate.class)) {
         Bus.post(new ExplosionEvent.Detonate(nivel, (Explosion)(Object)this, entidades, this.ruleta$bloques == null ? new java.util.ArrayList<>() : this.ruleta$bloques));
      }
      return entidades;
   }
}
