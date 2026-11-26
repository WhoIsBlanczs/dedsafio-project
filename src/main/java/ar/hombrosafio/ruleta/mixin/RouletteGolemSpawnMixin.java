package ar.hombrosafio.ruleta.mixin;

import ar.hombrosafio.ruleta.DifficultyChanges;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class RouletteGolemSpawnMixin {
	@Inject(method = "addEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
	private void ruleta$blockIronAndSnowGolems(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (DifficultyChanges.golemsDisabled(entity)) {
			entity.discard();
			cir.setReturnValue(false);
		}
	}
}
