package ar.hombrosafio.ruleta.mixin;

import ar.hombrosafio.ruleta.DifficultyChanges;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public abstract class RouletteZombieSpawnMixin {
	@Inject(method = "checkMonsterSpawnRules", at = @At("HEAD"), cancellable = true)
	private static void ruleta$anyLight(EntityType<? extends Mob> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos,
		RandomSource random, CallbackInfoReturnable<Boolean> result) {
		boolean zombie = type == EntityTypes.ZOMBIE || type == EntityTypes.HUSK || type == EntityTypes.ZOMBIE_VILLAGER;
		if (zombie && DifficultyChanges.zombiesAnyLight() || type == EntityTypes.CREEPER && DifficultyChanges.creepersAnyLight()) {
			result.setReturnValue(Mob.checkMobSpawnRules(type, level, reason, pos, random));
		}
	}
}
