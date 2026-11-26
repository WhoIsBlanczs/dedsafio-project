package ar.hombrosafio.ruleta.mixin;

import ar.hombrosafio.ruleta.DifficultyChanges;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathfinderMob.class)
public abstract class RouletteZombieLightMixin {
	@Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
	private void ruleta$anyLight(LevelAccessor level, EntitySpawnReason reason, CallbackInfoReturnable<Boolean> result) {
		PathfinderMob self = (PathfinderMob) (Object) this;
		if (DifficultyChanges.zombiesAnyLight() && DifficultyChanges.isRouletteZombie(self)) {
			result.setReturnValue(true);
		} else if (DifficultyChanges.creepersAnyLight() && self instanceof Creeper) {
			result.setReturnValue(true);
		}
	}
}
