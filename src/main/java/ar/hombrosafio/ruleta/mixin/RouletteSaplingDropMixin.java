package ar.hombrosafio.ruleta.mixin;

import ar.hombrosafio.ruleta.DifficultyChanges;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class RouletteSaplingDropMixin {
	@Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"),
		cancellable = true)
	private static void ruleta$removeSaplingDrops(Level level, BlockPos pos, ItemStack stack, CallbackInfo ci) {
		if (DifficultyChanges.suppressSapling(stack)) {
			ci.cancel();
		}
	}
}
