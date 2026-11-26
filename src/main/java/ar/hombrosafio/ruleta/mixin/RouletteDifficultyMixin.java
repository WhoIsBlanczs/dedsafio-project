package ar.hombrosafio.ruleta.mixin;

import ar.hombrosafio.ruleta.DifficultyChanges;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class RouletteDifficultyMixin {
	@ModifyVariable(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"),
		argsOnly = true, ordinal = 0)
	private float ruleta$damage(float amount, @Local(argsOnly = true) DamageSource source) {
		return DifficultyChanges.modifyDamage(source, amount);
	}

	@Inject(method = "getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
	private void ruleta$pearlBypassesArmor(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
		if (DifficultyChanges.ignoresArmor(source)) {
			cir.setReturnValue(amount);
		}
	}

	@Inject(method = "decreaseAirSupply(I)I", at = @At("RETURN"), cancellable = true)
	private void ruleta$consumeAirFaster(int air, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(DifficultyChanges.airAfterVanilla(cir.getReturnValue()));
	}

	@Inject(method = "baseTick()V", at = @At("TAIL"))
	private void ruleta$doorsDoNotGiveAir(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (DifficultyChanges.shouldBlockDoorAir(entity)) {
			entity.setAirSupply(DifficultyChanges.airAfterVanilla(entity.getAirSupply() - 1));
		}
	}
}
