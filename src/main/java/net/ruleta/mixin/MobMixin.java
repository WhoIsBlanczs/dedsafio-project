package net.ruleta.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.ruleta.compat.Bus;
import net.ruleta.compat.MobCancelable;
import net.ruleta.compat.MobSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements MobCancelable {
   @Unique
   private boolean ruleta$spawnCancelado;

   @Override
   public boolean ruleta$spawnCancelado() {
      return this.ruleta$spawnCancelado;
   }

   @Override
   public void ruleta$cancelarSpawn() {
      this.ruleta$spawnCancelado = true;
   }

   @Inject(
      method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;",
      at = @At("HEAD"),
      require = 0
   )
   private void ruleta$preparar(
      ServerLevelAccessor nivel, DifficultyInstance dificultad, EntitySpawnReason tipo, SpawnGroupData datos, CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      if (Bus.hayOyentes(MobSpawnEvent.FinalizeSpawn.class)) {
         Mob yo = (Mob)this;
         MobSpawnEvent.FinalizeSpawn ev = new MobSpawnEvent.FinalizeSpawn(yo, nivel, dificultad, tipo);
         if (Bus.post(ev)) {
            this.ruleta$spawnCancelado = true;
         }
      }
   }
}
