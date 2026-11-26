package net.ruleta.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.ruleta.compat.Bus;
import net.ruleta.compat.EntityJoinLevelEvent;
import net.ruleta.compat.MobCancelable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PersistentEntitySectionManager.class})
public abstract class PersistentEntitySectionManagerMixin {
   @Inject(
      method = {"addEntity(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$unirse(EntityAccess acceso, boolean cargadaDelDisco, CallbackInfoReturnable<Boolean> cir) {
      if (acceso instanceof Entity entidad) {
         if (entidad instanceof Mob mob && ((MobCancelable)mob).ruleta$spawnCancelado()) {
            cir.setReturnValue(false);
         } else {
            if (Bus.hayOyentes(EntityJoinLevelEvent.class) && Bus.post(new EntityJoinLevelEvent(entidad, entidad.level(), cargadaDelDisco))) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
